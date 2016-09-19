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
        var RESTORE_CHANNEL = 'restore';
        var RESET_CHANNEL = 'reset';
        var BROADCAST_CHANNEL = 'broadcast';

        var client = tabex.client();
        var requestId = 0;

        function queueRequest(data) {
            ++ requestId;
            localStorage.setItem(REQUEST_KEY_PREFIX + $.now() + requestId, JSON.stringify(data));
        }

        var restores = [ ];

        client.on(RESET_CHANNEL, function () {
            $.each(restores, function (i, restore) {
                queueRequest(restore);
            });
        });

        function reset() {
            client.emit(RESET_CHANNEL, "unused", true);
        }

        var processRequestsInterval;
        var processRequests = bsp_utils.throttle(10, function () {
            for (var j = 0; j < 100; ++ j) {

                // Process the oldest request first.
                var oldestKey = null;

                for (var i = 0, length = localStorage.length; i < length; ++ i) {
                    var key = localStorage.key(i);

                    if (key && key.indexOf(REQUEST_KEY_PREFIX) === 0 && (!oldestKey || oldestKey > key)) {
                        oldestKey = key;
                        break;
                    }
                }

                if (oldestKey) {
                    var item = localStorage.getItem(oldestKey);

                    if (item) {
                        localStorage.removeItem(key);
                        socket.send(JSON.parse(item));
                        return;
                    }

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

                    if (firstRole === 'replica') {
                        reset();
                    }

                    $(window).on('storage', processRequests);
                    processRequestsInterval = setInterval(processRequests, 50);
                }

            } else if (isPrimary) {
                isPrimary = false;
                socket.disconnect();

                $(window).off('storage', processRequests);

                if (processRequestsInterval) {
                    clearInterval(processRequestsInterval);
                    processRequestsInterval = null;
                }
            }
        });

        var disconnects = [ ];

        $(window).on('beforeunload', function () {
            $.each(disconnects, function (i, disconnect) {
                queueRequest(disconnect);
            });

            disconnects = [ ];
        });

        var restoreCallbacks = { };

        client.on(RESTORE_CHANNEL, function (state) {
            var callback = restoreCallbacks[state];

            if (callback) {
                callback();
            }
        });

        var broadcastCallbacks = { };

        client.on(BROADCAST_CHANNEL, function (message) {
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

            reset: reset,

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
                client.emit(BROADCAST_CHANNEL, message, true);
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

            // All done sending for now.
            if (sends.length <= 0) {
                sending = false;
                return;
            }

            var message = sends.shift();
            var type = message.type;

            $.ajax({
                type: 'post',

                // Query string only used for logging.
                url: URL + (type ? '?' + encodeURIComponent(type) : ''),
                cache: false,
                dataType: 'json',

                data: {
                    sessionId: sessionId,
                    message: JSON.stringify(message)
                },

                // Next?
                complete: processSends,

                error: function () {
                    sends.unshift(message);
                    reconnect();
                },

                success: function (messages) {
                    if (messages) {
                        $.each(messages, function (i, message) {
                            share.triggerBroadcast(message);
                        });
                    }

                    if (type === 'restore') {
                        share.triggerRestore(message.className);
                    }
                }
            });
        };

        // For receiving messages from the server.
        var receiver;
        var pingInterval;

        function reset() {
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

                reset();
            }

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
            share.reset();
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
