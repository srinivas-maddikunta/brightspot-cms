define([ 'jquery', 'bsp-utils', 'tabex' ], function($, bsp_utils, tabex) {

    // Fake methods that do nothing when RTC is disabled.
    if (DISABLE_RTC) {
        return {
            initialize: function () {
            },

            restore: function () {
            },

            receive: function () {
            },

            execute: function () {
            }
        };
    }

    var share;
    var socket;

    // For cross window communication.
    share = (function () {
        var REQUEST_KEY_PREFIX = 'brightspot.rtc.request.';
        var DISCONNECTS_KEY_PREFIX = 'brightspot.rtc.disconnects.';
        var RESTORE_CHANNEL = 'restore';
        var RESET_CHANNEL = 'reset';
        var BROADCAST_CHANNEL = 'broadcast';
        var DISCONNECT_CHANNEL = 'disconnect';

        var client = tabex.client();

        function sendDisconnects(disconnects) {
            $.each(disconnects, function (i, disconnect) {
                socket.send(disconnect);
            });
        }

        function forEachStorageItem(prefix, callback) {
            for (var i = 0, length = localStorage.length; i < length; ++ i) {
                var key = localStorage.key(i);

                if (key && key.indexOf(prefix) === 0) {
                    callback(key);
                }
            }
        }

        function removeFromStorage(key) {
            var value = JSON.parse(localStorage.getItem(key));
            localStorage.removeItem(key);
            return value;
        }

        var processRequestsInterval;
        var processRequests = bsp_utils.throttle(10, function () {
            for (var j = 0; j < 100; ++ j) {

                // Process the oldest request first.
                var oldestKey = null;

                forEachStorageItem(REQUEST_KEY_PREFIX, function (key) {
                    if (!oldestKey || oldestKey > key) {
                        oldestKey = key;
                    }
                });

                if (oldestKey) {
                    socket.send(removeFromStorage(oldestKey));

                } else {
                    return;
                }
            }
        });

        var firstRole;
        var isPrimary;

        client.on('!sys.master', function (data) {
            var isNodePrimary = data.node_id === data.master_id;

            if (!firstRole) {
                firstRole = isNodePrimary ? 'primary' : 'replica';
            }

            if (data.node_id === data.master_id) {
                if (!isPrimary) {
                    isPrimary = true;
                    socket.connect();

                    // Disconnect previous sessions.
                    forEachStorageItem(DISCONNECTS_KEY_PREFIX, function (key) {
                        sendDisconnects(removeFromStorage(key));
                    });

                    if (firstRole === 'replica') {
                        client.emit(RESET_CHANNEL, "unused", true);
                    }

                    client.on(DISCONNECT_CHANNEL, sendDisconnects);
                    $(window).on('storage', processRequests);
                    processRequestsInterval = setInterval(processRequests, 50);
                }

            } else if (isPrimary) {
                isPrimary = false;
                socket.disconnect();

                client.off(DISCONNECT_CHANNEL, sendDisconnects);
                $(window).off('storage', processRequests);

                if (processRequestsInterval) {
                    clearInterval(processRequestsInterval);
                    processRequestsInterval = null;
                }
            }
        });

        // Try to disconnect the session when the window is closed by either
        // remembering it until next page view or sending the message to the
        // primary.
        var disconnects = [ ];

        $(window).on('beforeunload', function () {
            if (isPrimary) {
                localStorage.setItem(
                        DISCONNECTS_KEY_PREFIX + $.now() + Math.random(),
                        JSON.stringify(disconnects));

            } else {
                client.emit(DISCONNECT_CHANNEL, disconnects);
            }
        });

        var restores = [ ];
        var restoreCallbacks = { };

        client.on(RESTORE_CHANNEL, function (state) {
            var callback = restoreCallbacks[state];

            if (callback) {
                callback();
            }
        });

        var requestId = 0;

        function queueRequest(data) {
            ++ requestId;
            localStorage.setItem(REQUEST_KEY_PREFIX + $.now() + requestId, JSON.stringify(data));
        }

        client.on(RESET_CHANNEL, function () {
            $.each(restores, function (i, restore) {
                queueRequest(restore);
            });
        });

        var broadcastCallbacks = { };

        client.on(BROADCAST_CHANNEL, function (messageString) {
            var message = JSON.parse(messageString);
            var callbacks = broadcastCallbacks[message.broadcast];

            if (callbacks) {
                $.each(callbacks, function(i, callback) {
                    callback(message.data);
                });
            }
        });

        // Public API methods:
        return {
            queueRequest: queueRequest,

            registerDisconnect: function (state, data) {
                disconnects.push({
                    type: 'disconnect',
                    className: state,
                    data: data
                });
            },

            registerRestore: function (state, data, callback) {
                var restore = {
                    type: 'restore',
                    className: state,
                    data: data
                };

                restores.push(restore);
                restoreCallbacks[state] = callback;
                queueRequest(restore)
            },

            triggerRestore: function (state) {
                client.emit(RESTORE_CHANNEL, state, true);
            },

            registerBroadcast: function (broadcast, callback) {
                var callbacks = broadcastCallbacks[broadcast];

                if (!callbacks) {
                    callbacks = broadcastCallbacks[broadcast] = [ ];
                }

                callbacks.push(callback);
            },

            triggerBroadcast: function (message) {
                client.emit(BROADCAST_CHANNEL, JSON.stringify(message), true);
            }
        };
    })();

    // For server/client communication.
    socket = (function () {
        var URL = ROOT_PATH + '/_rtc';

        // For sending messages to the server.
        var sends = [ ];
        var sending;
        var processSends;
        var sessionId;
        var reconnect;

        // Make sure that the messages are sent in order.
        function send(message) {
            sends.push(message);

            if (!sending) {
                sending = true;
                processSends();
            }
        }

        processSends = function () {

            // Session not available yet so try again in a bit.
            if (!sessionId) {
                setTimeout(processSends, 100);
                return;
            }

            var message = sends.shift();
            var type = message.type;

            $.ajax({
                type: 'post',

                // Query string not used for anything except debugging.
                url: URL + (type ? '?' + encodeURIComponent(type) : ''),
                cache: false,
                dataType: 'json',

                data: {
                    sessionId: sessionId,
                    message: JSON.stringify(message)
                },

                error: reconnect,
                success: function (messages) {
                    if (messages) {
                        $.each(messages, function (i, message) {
                            share.triggerBroadcast(message);
                        });
                    }

                    if (type === 'restore') {
                        share.triggerRestore(message.className);
                    }

                    if (sends.length > 0) {
                        processSends();

                    } else {
                        sending = false;
                    }
                }
            });
        };

        // For receiving messages from the server.
        var receiver;
        var pingInterval;

        function reset() {
            sends = [ ];
            sending = false;
            sessionId = null;

            if (receiver) {
                receiver.close();
                receiver = null;
            }

            if (pingInterval) {
                clearInterval(pingInterval);
                pingInterval = null;
            }
        }

        var ensure = bsp_utils.throttle(5000, function () {
            if (receiver) {
                var state = receiver.readyState;

                if (state === EventSource.CONNECTING) {
                    return;

                } else if (state === EventSource.OPEN && sessionId) {
                    return;
                }
            }

            reset();

            // Connect...
            receiver = new EventSource(URL);
            receiver.onmessage = function (event) {
                var data = JSON.parse(event.data);

                // First message along with the session ID.
                if (data._first) {
                    sessionId = data.sessionId;
                    receiver.onerror = reconnect;

                    // Ping roughly every minute to prevent the server from
                    // forcibly disconnecting the session.
                    pingInterval = setInterval(function () {
                        send({
                            type: 'ping'
                        });
                    }, 55000);

                // Broadcast regular message.
                } else if (!data._ping) {
                    share.triggerBroadcast(data);
                }
            };
        });

        reconnect = function () {
            reset();
            ensure();
        };

        // Public API methods:
        var ensureInterval;

        return {
            send: send,

            connect: function () {
                if (!ensureInterval) {
                    ensureInterval = setInterval(ensure, 100);
                    ensure();
                }
            },

            disconnect: function () {
                if (ensureInterval) {
                    clearInterval(ensureInterval);
                    ensureInterval = null;
                }

                reset();
            }
        };
    })();

    // Public API methods:
    return {
        initialize: function (state, data, callback) {
            share.registerRestore(state, data, callback);
            share.registerDisconnect(state, data);
        },

        restore: function(state, data, callback) {
            share.registerRestore(state, data, callback);
        },

        receive: function(broadcast, callback) {
            share.registerBroadcast(broadcast, callback);
        },

        execute: function(action, data) {
            share.queueRequest({
                type: 'execute',
                className: action,
                data: data
            });
        }
    };
});
