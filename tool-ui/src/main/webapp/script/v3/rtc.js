define([ 'jquery', 'bsp-utils', 'tabex', 'atmosphere' ], function($, bsp_utils, tabex, atmosphere) {
  if (DISABLE_RTC) {
    return {
      restore: function () {
      },

      receive: function () {
      },

      execute: function () {
      }
    };
  }

  var RESTORE_CHANNEL = 'restore';
  var BROADCAST_CHANNEL = 'broadcast';
  var CLOSE_CHANNEL = 'close';
  var PUSH_KEY_PREFIX = 'brightspot.rtc.push.';
  var SESSION_ID_KEY = 'brightspot.rtc.sessionId';
  var CLOSES_KEY_PREFIX = 'brightspot.rtc.closes.';

  var share = tabex.client();
  var master;
  var closes = [ ];

  share.on('!sys.master', function (data) {
    if (data.node_id !== data.master_id) {
      return;
    }

    master = true;

    var request = {
      url: '/_rtc',
      contentType: 'application/json',
      disableDisconnect: true,
      fallbackTransport: 'sse',
      reconnect: false,
      trackMessageLength: true,
      transport: 'sse'
    };

    var socket;
    var subscribe = bsp_utils.throttle(5000, function () {
      socket = atmosphere.subscribe(request);
    });

    var isOnline = false;

    var offlineExecutes = [];
    var onlineExecutes = {
      push: function (message) {
        socket.push(JSON.stringify(message));
      }
    };

    var redoRestores = [];
    var offlineRestores = [];
    var onlineRestores = {
      push: function (message) {
        redoRestores.push(message);
        onlineExecutes.push(message);
        share.emit(RESTORE_CHANNEL, message.className, true);
      }
    };

    var offlineCloses = [];
    var onlineCloses = {
      push: function (message) {
        socket.push(JSON.stringify(message));
      }
    };

    request.onOpen = function () {
      isOnline = true;

      for (var i = 0, length = localStorage.length; i < length; ++ i) {
        var key = localStorage.key(i);

        if (key && key.indexOf(CLOSES_KEY_PREFIX) === 0) {
          var previousCloses = JSON.parse(localStorage.getItem(key));
          localStorage.removeItem(key);

          $.each(previousCloses, function (i, close) {
            onlineCloses.push(close);
          });
        }
      }

      var oldSessionId = localStorage.getItem(SESSION_ID_KEY);

      if (oldSessionId) {
        socket.push({
          type: 'migrate',
          oldSessionId: oldSessionId,
          newSessionId: socket.getUUID()
        })
      }

      $.each(redoRestores, function (i, message) {
        onlineExecutes.push(message);
        share.emit(RESTORE_CHANNEL, message.className, true);
      });

      $.each(offlineRestores, function (i, message) {
        onlineRestores.push(message);
      });

      offlineRestores = [];

      $.each(offlineExecutes, function (i, message) {
        onlineExecutes.push(message);
      });

      offlineExecutes = [];
    };

    request.onClose = function () {
      isOnline = false;
      console.log('onclose');
      subscribe();
    };

    request.onMessage = function (response) {
      share.emit(BROADCAST_CHANNEL, response.responseBody, true);
    };

    request.onMessagePublished = function (response) {
      $.each(response.messages, function (i, message) {
        share.emit(BROADCAST_CHANNEL, message, true);
      });
    };

    subscribe();

    share.on(CLOSE_CHANNEL, function (closes) {
      $.each(closes, function (i, close) {
        (isOnline ? onlineCloses : offlineCloses).push(close);
      });
    });

    setInterval(function () {
      if (isOnline) {
        onlineExecutes.push({
          type: 'ping'
        });
      }
    }, 10000);

    var checkRequests = bsp_utils.throttle(50, function () {
      var minKey;

      for (var j = 0; j < 100; ++j) {
        minKey = null;

        for (var i = 0, length = localStorage.length; i < length; ++i) {
          var key = localStorage.key(i);

          if (key && key.indexOf(PUSH_KEY_PREFIX) === 0 && (!minKey || minKey > key)) {
            minKey = key;
          }
        }

        if (!minKey) {
          return;
        }

        var push = JSON.parse(localStorage.getItem(minKey));
        localStorage.removeItem(minKey);

        if (push.restore) {
          (isOnline ? onlineRestores : offlineRestores).push(push.data);

        } else {
          (isOnline ? onlineExecutes : offlineExecutes).push(push.data);
        }
      }
    });

    setInterval(checkRequests, 50);
    $(window).on('storage', checkRequests);

    $(window).on('beforeunload', function () {
      var sessionId = socket.getUUID();

      localStorage.setItem(SESSION_ID_KEY, sessionId);
      localStorage.setItem(CLOSES_KEY_PREFIX + sessionId, JSON.stringify(closes));
    });
  });

  var restoreCallbacks = { };

  share.on(RESTORE_CHANNEL, function (state) {
    var callback = restoreCallbacks[state];

    if (callback) {
      callback();
    }
  });

  var broadcastCallbacks = { };

  share.on(BROADCAST_CHANNEL, function (messageString) {
    var message = JSON.parse(messageString);
    var callbacks = broadcastCallbacks[message.broadcast];

    if (callbacks) {
      $.each(callbacks, function(i, callback) {
        callback(message.data);
      });
    }
  });

  function push(restore, data) {
    localStorage.setItem(PUSH_KEY_PREFIX + $.now(), JSON.stringify({
      restore: restore,
      data: data
    }));
  }

  $(window).on('beforeunload', function () {
    if (!master) {
      share.emit(CLOSE_CHANNEL, closes);
    }
  });

  return {
    restore: function(state, data, callback) {
      restoreCallbacks[state] = callback;

      push(true, {
        type: 'restore',
        className: state,
        data: data
      });

      closes.push({
        type: 'close',
        className: state,
        data: data
      });
    },

    receive: function(broadcast, callback) {
      var callbacks = broadcastCallbacks[broadcast];

      if (!callbacks) {
        callbacks = broadcastCallbacks[broadcast] = [ ];
      }

      callbacks.push(callback);
    },

    execute: function(action, data) {
      push(false, {
        type: 'action',
        className: action,
        data: data
      });
    }
  };
});
