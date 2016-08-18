define([ 'jquery', 'bsp-utils', 'v3/rtc', 'v3/color-utils' ], function ($, bsp_utils, rtc, color_utils) {

    var colorsByUuid = { };

    function backgroundColor(uuid) {

        if (!colorsByUuid[uuid]) {
            colorsByUuid[uuid] = color_utils.generateFromHue(color_utils.changeHue(Math.random()));
        }

        return colorsByUuid[uuid];
    }

    rtc.receive('com.psddev.cms.tool.page.content.EditFieldUpdateBroadcast', function(data) {
        var contentId = data.contentId;
        var $containers = $('[data-rtc-content-id="' + contentId + '"]');
        
        if ($containers.length === 0) {
            return;
        }
        
        var userId = data.userId;
        var fieldNamesByObjectId = data.fieldNamesByObjectId;

        $containers.each(function() {

            var $container = $(this);

            var $viewersContainer = $container.find('[data-rtc-edit-field-update-viewers]');

            if ($viewersContainer.length > 0) {
                var userAvatarHtml = data.userAvatarHtml;
                var closed = data.closed;
                var $viewers = $viewersContainer.find('> .EditFieldUpdateViewers');
                var $some;

                if ($viewers.length === 0) {
                    var $none = $('<div/>', {
                        'class': 'EditFieldUpdateViewers-none',
                        html: $viewersContainer.html()
                    });

                    $some = $('<div/>', {
                        'class': 'EditFieldUpdateViewers-some'
                    });

                    $viewers = $('<div/>', {
                        'class': 'EditFieldUpdateViewers',
                        html: [
                            $none,
                            $some
                        ]
                    });

                    $viewers.append($none);
                    $viewers.append($some);
                    $viewersContainer.html($viewers);

                } else {
                    $some = $viewers.find('> .EditFieldUpdateViewers-some');
                }

                var $viewer = $some.find('> .EditFieldUpdateViewers-viewer[data-user-id="' + userId + '"]');

                if ($viewer.length > 0) {
                    if (closed) {
                        $viewer.remove();
                    }

                } else if (!closed) {
                    $viewer = $('<div/>', {
                        'class': 'EditFieldUpdateViewers-viewer',
                        'data-user-id': userId,
                        html: userAvatarHtml
                    });

                    $viewer.find('.ToolUserAvatar').css({
                        'background-color': backgroundColor(userId)
                    });

                    $some.append($viewer);
                }

                if (fieldNamesByObjectId && Object.keys(fieldNamesByObjectId).length > 0) {
                    $viewer.attr('data-editing', true);

                } else {
                    $viewer.removeAttr('data-editing');
                }

                if ($some.find('> .EditFieldUpdateViewers-viewer').length > 0) {
                    $viewers.attr('data-some', true);

                } else {
                    $viewers.removeAttr('data-some');
                }
            }

            if (!$container.is('form')) {
                return;
            }

            $container.find('.inputPending[data-user-id="' + userId + '"]').each(function() {
                var $pending = $(this);

                $pending.closest('.inputContainer').removeClass('inputContainer-pending');
                $pending.remove();
            });

            if (!fieldNamesByObjectId) {
                return;
            }

            var userName = data.userName;

            $.each(fieldNamesByObjectId, function (objectId, fieldNames) {
                var $inputs = $container.find('.objectInputs[data-id="' + objectId + '"]');

                if ($inputs.length === 0) {
                    return;
                }

                $.each(fieldNames, function (i, fieldName) {
                    var $container = $inputs.find('> .inputContainer[data-field-name="' + fieldName + '"]');
                    var nested = false;

                    $container.find('.objectInputs').each(function() {
                        if (fieldNamesByObjectId[$(this).attr('data-id')]) {
                            nested = true;
                            return false;
                        }
                    });

                    if (!nested) {
                        $container.addClass('inputContainer-pending');

                        $container.find('> .inputLabel').after($('<div/>', {
                            'class': 'inputPending',
                            'data-user-id': userId,
                            'html': [
                                'Pending edit from ' + userName + ' - ',
                                $('<a/>', {
                                    'text': 'Unlock',
                                    'click': function() {
                                        if (confirm('Are you sure you want to forcefully unlock this field?')) {
                                            rtc.execute('com.psddev.cms.tool.page.content.EditFieldUpdateAction', {
                                                contentId: $container.closest('form').attr('data-rtc-content-id'),
                                                unlockObjectId: $container.closest('.objectInputs').attr('data-id'),
                                                unlockFieldName: $container.attr('data-field-name')
                                            });
                                        }

                                        return false;
                                    }
                                })
                            ]
                        }));
                    }
                });
            });
        })
    });

    bsp_utils.onDomInsert(document, '[data-rtc-content-id]', {
        insert: function (container) {
            var $container = $(container);
            var contentId = $container.attr('data-rtc-content-id');
            
            if (!contentId) {
                return;
            }
            
            if (!$container.is('form')) {
                rtc.restore('com.psddev.cms.tool.page.content.EditFieldUpdateState', {
                    contentId: contentId
                });
                
                return;
            }
            
            function update() {
                var fieldNamesByObjectId = {};

                $container.find('.inputContainer.state-changed, .inputContainer.state-focus').each(function () {
                    var $container = $(this);
                    var objectId = $container.closest('.objectInputs').attr('data-id');

                    (fieldNamesByObjectId[objectId] = fieldNamesByObjectId[objectId] || []).push($container.attr('data-field-name'));
                });

                if (fieldNamesByObjectId) {
                    rtc.execute('com.psddev.cms.tool.page.content.EditFieldUpdateAction', {
                        contentId: contentId,
                        fieldNamesByObjectId: fieldNamesByObjectId
                    });
                }
            }

            rtc.initialize('com.psddev.cms.tool.page.content.EditFieldUpdateState', {
                contentId: contentId
            }, update);

            var updateTimeout;

            function throttledUpdate() {
                if (updateTimeout) {
                    clearTimeout(updateTimeout);
                }

                updateTimeout = setTimeout(function () {
                    updateTimeout = null;
                    update();
                }, 50);
            }

            $container.on('blur focus change', ':input', throttledUpdate);
            $container.on('content-state-differences', throttledUpdate);
        }
    });
});
