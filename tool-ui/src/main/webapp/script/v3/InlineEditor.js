require([ 'bsp-utils', 'jquery' ], function (bsp_utils, $) {
    var $document = $(window.document),
            $body = $($document[0].body),
            $parent = $(window.parent),
            $parentDocument = $($parent[0].document),
            $parentBody = $($parentDocument[0].body),
            $editor = $parentBody.find('.BrightspotCmsInlineEditor'),
            mainObjectData;

    // Find all objects in the parent document.
    var MAIN_OBJECT_PREFIX = 'BrightspotCmsMainObject ';
    var OBJECT_BEGIN_PREFIX = 'BrightspotCmsObjectBegin ';
    var parentCommentWalker = $parentDocument[0].createTreeWalker($parentBody[0], NodeFilter.SHOW_COMMENT, null, null);

    while (parentCommentWalker.nextNode()) {
        var comment = parentCommentWalker.currentNode;
        var commentValue = comment.nodeValue;

        if (commentValue.indexOf(MAIN_OBJECT_PREFIX) === 0) {
            mainObjectData = $.parseJSON(commentValue.substring(MAIN_OBJECT_PREFIX.length));

        } else if (commentValue.indexOf(OBJECT_BEGIN_PREFIX) === 0) {
            $(comment.nextElementSibling).attr(
                    'data-brightspot-cms-object',
                    commentValue.substring(OBJECT_BEGIN_PREFIX.length));
        }
    }

    var ids = [ mainObjectData.id ];

    // Create controls for all the objects in the parent document.
    $parentBody.find('[data-brightspot-cms-object]').each(function() {
        var $begin = $(this),
                objectData = $.parseJSON($begin.attr('data-brightspot-cms-object')),
                id = objectData.id,
                $outline,
                $edit,
                $controls;

        if ($.inArray(id, ids) > -1) {
            return;
        }

        ids.push(id);

        $outline = $('<div/>', {
            'class': 'InlineEditorOutline'
        });

        $edit = $('<a/>', {
            'class': 'icon icon-action-edit',
            'href': CONTEXT_PATH + '/content/edit.jsp?id=' + objectData.id,
            'target': '_blank',
            'text': objectData.typeLabel,

            'mouseenter': function() {
                var box = $.data($begin[0], 'InlineEditor-box');

                $controls.addClass('InlineEditorControls-hover');

                // Fade all the controls that overlap with this one.
                $parentBody.find('.BrightspotCmsObject').each(function() {
                    var previousBox = $.data(this, 'InlineEditor-box');

                    if (previousBox &&
                            previousBox !== box &&
                            previousBox.controlsCss.left <= box.controlsCss.left + box.outlineCss.width &&
                            box.controlsCss.left <= previousBox.controlsCss.left + previousBox.controlsDimension.width &&
                            previousBox.controlsCss.top + previousBox.outlineCss.top <= box.controlsCss.top + box.outlineCss.top + box.outlineCss.height &&
                            box.controlsCss.top + box.outlineCss.top <= previousBox.controlsCss.top + previousBox.outlineCss.top + previousBox.controlsDimension.height) {
                        previousBox.$controls.addClass('InlineEditorControls-under');
                    }
                });
            },

            'mouseleave': function() {
                $controls.removeClass('InlineEditorControls-hover');
                $body.find('.InlineEditorControls').removeClass('InlineEditorControls-under');
            }
        });

        $controls = $('<ul/>', {
            'class': 'InlineEditorControls',
            'html': $('<li/>', {
                'html': [ $outline, $edit ]
            })
        });

        $.data(this, 'InlineEditor-$controls', $controls);
        $body.append($controls);
        $begin.addClass('BrightspotCmsObject');
    });

    var positionControls = bsp_utils.throttle(5, function () {
        var previousBoxes = [ ];

        $parentBody.find('.BrightspotCmsObject').each(function() {
            var $begin = $(this);
            var beginOffset = $begin.offset();
            var minX = beginOffset.left;
            var maxX = minX + $begin.outerWidth();
            var minY = beginOffset.top;
            var maxY = minY + $begin.outerHeight();

            if (minY >= 0 && minY < 37) {
                minY = 37;
            }

            var $controls = $.data(this, 'InlineEditor-$controls');
            var box = {
                '$controls': $controls,
                'controlsCss': {
                    'left': minX,
                    'top': minY
                },
                'controlsDimension': {
                    'height': $controls.outerHeight() + 5,
                    'width': $controls.outerWidth()
                },
                'outlineCss': {
                    'height': maxY - minY,
                    'top': 0,
                    'width': maxX - minX
                }
            };

            $.data($begin[0], 'InlineEditor-box', box);

            // Move the controls down until they don't overlay with
            // any other controls.
            if (previousBoxes.length > 0) {
                do {
                    retry = false;

                    $.each(previousBoxes, function(i, previousBox) {
                        if (previousBox.controlsCss.left <= box.controlsCss.left + box.controlsDimension.width &&
                                box.controlsCss.left <= previousBox.controlsCss.left + previousBox.controlsDimension.width &&
                                previousBox.controlsCss.top <= box.controlsCss.top + box.controlsDimension.height &&
                                box.controlsCss.top <= previousBox.controlsCss.top + previousBox.controlsDimension.height) {
                            retry = true;

                            box.controlsCss.top += 1;
                            box.outlineCss.top -= 1;
                            return false;
                        }
                    });
                } while (retry);
            }

            previousBoxes.push(box);
            $controls.css(box.controlsCss);
            $controls.find('.InlineEditorOutline').css(box.outlineCss);
            $controls.show();
        });
    });

    positionControls();
    setInterval(positionControls, 1000 / 60);
    $parent.scroll(positionControls);

    // Enable "click-through" editor IFRAME.
    $parentDocument.on('mousemove', function(event) {
        if ($($document[0].elementFromPoint(event.pageX, event.pageY)).closest('.InlineEditorControls').length > 0) {
            $editor.css('pointer-events', 'auto');
        }
    });

    $document.on('mousemove', function(event) {
        if ($body.find('.popup:visible').length === 0 &&
                $($document[0].elementFromPoint(event.pageX, event.pageY)).closest('.InlineEditorControls').length === 0) {
            $editor.css('pointer-events', 'none');
        }
    });

    $document.on('click', 'a[target]', function() {
        $editor.css('pointer-events', 'auto');
    });

    // Collapse the editor on right click because Chrome activates it on the
    // editor even with pointer-events: none.
    $document.on('contextmenu', function(event) {
        var $logo = $body.find('.InlineEditorLogo');

        $editor.css({
            'max-height': $logo.outerHeight(true),
            'max-width': $logo.outerWidth(true)
        });

        $logo.find('a').one('click', function() {
            $editor.css({
                'max-height': '',
                'max-width': ''
            });

            return false;
        });
    });

    // Make sure that the editor IFRAME is at least as high as the parent
    // document.
    function equalizeHeight() {
        $editor.height(Math.max($document.height(), $parentBody.height()));
        $editor.css({
            'border': 'none',
            'left': 0,
            'margin': 0,
            'position': 'absolute',
            'top': 0,
            'width': '100%',
            'z-index': 1000000
        });
    }

    equalizeHeight();
    setInterval(equalizeHeight, 100);

    // Make sure that the main object controls are fixed at the top.
    $parent.scroll(function() {
        $('.InlineEditorControls-main').css('top', $parent.scrollTop());
    });
});
