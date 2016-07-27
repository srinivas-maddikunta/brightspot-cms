define([ 'jquery', 'bsp-utils', 'atmosphere' ], function($, bsp_utils, atmosphere) {
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

  var request = {
    url: '/_rtc',
    closeAsync: true,
    contentType: 'application/json',
    fallbackTransport: 'sse',
    maxReconnectOnClose: 0,
    trackMessageLength: true,
    transport: 'sse'
  };

  var socket;
  var subscribe = bsp_utils.throttle(5000, function() {
    socket = atmosphere.subscribe(request);
  });

  var isOnline = false;

  var offlineExecutions = [ ];
  var onlineExecutions = {
    push: function(message) {
      socket.push(JSON.stringify(message));
    }
  };
  
  var offlineRestores = [ ];
  var onlineRestores = {
    push: function(restore) {
      onlineExecutions.push(restore.message);

      var callback = restore.callback;

      if (callback) {
        callback();
      }
    }
  };

  request.onOpen = function() {
    isOnline = true;

    $.each(offlineRestores, function(i, restore) {
      onlineRestores.push(restore);
    });
    
    offlineRestores = [ ];

    $.each(offlineExecutions, function(i, message) {
      onlineExecutions.push(message);
    });

    offlineExecutions = [ ];

    if (localStorage) {
      var KEY_PREFIX = 'brightspot.rtc.socket.';
      var INTERVAL = 1000;

      setInterval(function() {
        localStorage.setItem(KEY_PREFIX + socket.getUUID(), '' + $.now());

        for (var i = 0, length = localStorage.length; i < length; ++ i) {
          var key = localStorage.key(i);

          if (key &&
              key.indexOf(KEY_PREFIX) === 0 &&
              parseInt(localStorage.getItem(key), 10) + (INTERVAL * 5) < $.now()) {

            localStorage.removeItem(key);
            (isOnline ? onlineExecutions : offlineExecutions).push({
              type: 'disconnect',
              sessionId: key.substring(KEY_PREFIX.length)
            });
          }
        }
      }, INTERVAL);
    }
  };

  request.onClose = function() {
    isOnline = false;
  };

  request.onError = function() {
    isOnline = false;

    subscribe();
  };

  var broadcastCallbacks = { };
  
  function processMessage(message) {
    var messageJson = JSON.parse(message);
    var callbacks = broadcastCallbacks[messageJson.broadcast];

    if (callbacks) {
      $.each(callbacks, function(i, callback) {
        callback(messageJson.data);
      });
    }
  }

  request.onMessage = function(response) {
    processMessage(response.responseBody);
  };

  request.onMessagePublished = function(response) {
    $.each(response.messages, function(i, message) {
      processMessage(message);
    });
  };

  subscribe();
  
  setInterval(function () {
    if (isOnline) {
      onlineExecutions.push({
        type: 'ping'
      });
    }
  }, 10000);

  return {
    restore: function(state, data, callback) {
      (isOnline ? onlineRestores : offlineRestores).push({
        callback: callback,
        message: {
          type: 'state',
          className: state,
          data: data
        }
      });
    },

    receive: function(broadcast, callback) {
      (broadcastCallbacks[broadcast] = broadcastCallbacks[broadcast] || [ ]).push(callback);
    },

    execute: function(action, data) {
      (isOnline ? onlineExecutions : offlineExecutions).push({
        type: 'action',
        className: action,
        data: data
      });
    }
  };
});
