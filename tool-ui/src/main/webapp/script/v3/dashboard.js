define([
        'jquery',
        'bsp-utils'],

    function ($, bsp_utils) {

        var settings = {
            updateUrl: '/cms/misc/updateUserDashboard',
            dragClass: 'drag-active',
            editModeClass: 'dashboard-editable',
            placeholderClass: 'widget-ghost',
            throttleInterval: 100,
            dashboardSelector: '.dashboard-columns',
            columnSelector: '.dashboard-column',
            widgetSelector: '.dashboard-widget',
            addColumnClass: 'dashboard-addColumnButton',
            addWidgetClass: 'dashboard-addWidgetButton',
            addWidgetContainerClass: 'dashboard-add-widget-container',
            removeWidgetClass: 'widget-remove',
            columnGutterClass: 'dashboard-column-gutter',
            minColumnWidth: 320
        };

        bsp_utils.onDomInsert(document, '.dashboard-columns', {

            'insert': function (dashboard) {

                settings = $.extend({}, settings, {state: {}});

                var $body = $('body');
                var $dashboard = $(dashboard);
                var $columns = $dashboard.find(settings.columnSelector);
                var $widgets = $dashboard.find(settings.widgetSelector);

                /**
                 * Drag events for widgets when in edit mode
                 */
                $widgets
                    .on('dragstart', dragStart)
                    .on('dragend', dragEnd)
                    .on('dragover', bsp_utils.throttle(settings.throttleInterval, dragOver))
                    .on('dragenter', bsp_utils.throttle(settings.throttleInterval, dragEnter))
                    .on('dragleave', bsp_utils.throttle(settings.throttleInterval, dragLeave));
                //webkit bug not fixed, cannot use drop event v. 39.0.2171.99 https://bugs.webkit.org/show_bug.cgi?id=37012
                //$widgets.on('drop'     , drop);

                /**
                 * Drag events for resizing columns
                 */
                $body
                    .on('dragstart', '.' + settings.columnGutterClass, bsp_utils.throttle(settings.throttleInterval, function(e) {

                        var $gutter = $(this);
                        var originalEvent = e.originalEvent;
                        var dataTransfer = originalEvent.dataTransfer;

                        dataTransfer.originalX = originalEvent.pageX;
                        dataTransfer.prevColumnWidth = $gutter.prev(settings.columnSelector).data('actualWidth');
                        dataTransfer.effectAllowed = 'none';
                        dataTransfer.nextColumnWidth = $gutter.next(settings.columnSelector).data('actualWidth');
                    }))
                    .on('drag', '.' + settings.columnGutterClass, bsp_utils.throttle(settings.throttleInterval, function(e) {

                        var $gutter = $(this);
                        var originalEvent = e.originalEvent;
                        var dataTransfer = originalEvent.dataTransfer;
                        var horizontalDiff = originalEvent.pageX - dataTransfer.originalX;
                        var prevColNewWidth = dataTransfer.prevColumnWidth + horizontalDiff;
                        var nextColNewWidth = dataTransfer.nextColumnWidth - horizontalDiff;

                        if (prevColNewWidth >= 320 && nextColNewWidth >= 320) {
                            $gutter.prev(settings.columnSelector).width(prevColNewWidth);
                            $gutter.next(settings.columnSelector).width(nextColNewWidth);
                        }
                    }))
                    .on('dragend', '.' + settings.columnGutterClass, function(e) {
                        var params = {
                            action : 'dashboardColumns-resize',
                            y : [],
                            width : []
                        };

                        $columns.each(function(yIndex, col) {
                            params.y.push(yIndex);
                            params.width.push($(col).width());
                        });

                        $.ajax({
                            'url' : '/cms/misc/updateUserDashboard',
                            'type' : 'POST',
                            'data' : $.param(params, true)
                        });
                    });

                /**
                 * Enables dashboard edit mode
                 */
                $body.on('click', '.dashboard-edit', function () {
                    $(this).toggleClass('toggled');

                    $dashboard.toggleClass(settings.editModeClass);
                    $widgets.prop('draggable', !$widgets.prop('draggable'));

                    if ($dashboard.hasClass(settings.editModeClass)) {

                        $columns.each(function(yIndex, col) {
                            var $col = $(col);
                            $col.data('actualWidth', $col.width());
                            insertColumnGutter($col);
                            $col.find(settings.widgetSelector).each(function(xIndex, dashboardWidget) {
                                insertRowButtons(dashboardWidget);
                            });
                        });
                    } else {
                        $dashboard.find('.' + settings.addWidgetClass + ', .' + settings.addColumnClass + ', .' + settings.columnSizerClass + ', .' + settings.columnGutterClass + ', .' + settings.addWidgetContainerClass).remove();
                    }
                });

                /**
                 * Hover events to show/hide remove widget button
                 * when dashboard edit mode is enabled
                 */
                $body.on({
                    mouseenter: function(e) {
                        $(this).append(
                            $('<a/>', {'class' : settings.removeWidgetClass, 'href' : settings.updateUrl})
                        );
                    },
                    mouseleave: function() {
                        $(this).find('.'+ settings.removeWidgetClass).remove();
                    }
                }, '.' + settings.editModeClass + ' ' + settings.widgetSelector + ' h1');

                /**
                 * Handles removing of widgets
                 */
                $body.on('click', '.' + settings.removeWidgetClass, function(event) {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                    var $a = $(this);
                    var $widget = $a.closest(settings.widgetSelector);
                    var $column = $widget.closest(settings.columnSelector);
                    $.ajax({
                        'url' : $a.attr('href'),
                        'type': 'post',
                        'data': {
                            'x' : getRowIndex($widget),
                            'y' : getColumnIndex($column),
                            'action' : 'dashboardWidgets-remove'
                        }
                    });
                    $widget.next('.' + settings.addWidgetClass).detach();
                    $widget.detach();

                    //if no widgets remain in column, remove column
                    if ($column.find(settings.widgetSelector).size() === 0) {
                        $column.remove();
                    }

                });

                function dragStart(e) {
                    settings.state.activeWidget = this;
                    var $this = $(this);
                    var $widget = $(e.target);
                    var dataTransfer = e.originalEvent.dataTransfer;

                    settings.state.originalY = getColumnIndexFromWidget($this.closest(settings.columnSelector));
                    settings.state.originalX = getRowIndex($this);

                    $widget.toggleClass(settings.dragClass);

                    dataTransfer.setData('text/html', settings.state.activeWidget.innerHTML);
                    dataTransfer.effectAllowed = 'move';
                    dataTransfer.dropEffect = 'move';
                }

                function dragOver(e) {
                    if (e.preventDefault) e.preventDefault();

                    var dataTransfer = e.originalEvent.dataTransfer;
                    dataTransfer.effectAllowed = 'move';
                    dataTransfer.dropEffect = 'move';

                    return false;
                }

                function dragEnter(e) {
                    e.preventDefault();

                    if (this === settings.state.dragTarget
                        || this === settings.state.placeholder
                        || this === settings.state.activeWidget
                        || typeof settings.state.activeWidget === 'undefined') {
                        return false;
                    }

                    settings.state.dragTarget = this;

                    var $dragTarget = $(this);

                    if ($dragTarget.hasClass(settings.dragClass) || $dragTarget.hasClass(settings.placeholderClass)) {
                        return false;
                    }

                    var prev = $dragTarget.prev();

                    if (prev) {

                        if (typeof settings.state.placeholder === 'undefined') {
                            settings.state.placeholder = $('<div />', {'class': 'widget ' + settings.placeholderClass});
                        }

                        if (!prev.hasClass(settings.placeholderClass)) {
                            $dragTarget.before(settings.state.placeholder);
                        }

                    }

                    return false;
                }

                function dragLeave(e) {
                    e.preventDefault();
                    e.stopPropagation();

                    if (this === settings.state.dragTarget || this === settings.state.placeholder || this === settings.state.activeWidget) {
                        return false;
                    }

                    var $dropTarget = $(this);
                    var prev = $dropTarget.prev();

                    if (prev) {

                        if (prev.hasClass(settings.placeholderClass)) {
                            prev.detach();
                        }

                    }

                    settings.state.dragTarget = null;
                }

                //function drop(e) {
                //  if (e.stopPropagation) e.stopPropagation();
                //  console.log("DROP() - begin");
                //
                //  console.log("DROP() - end");
                //}

                function dragEnd(e) {
                    var $widget = $(e.target);
                    $widget.toggleClass(settings.dragClass);

                    var activeWidget = $(settings.state.activeWidget);
                    if (!settings.state.dragTarget) {
                        return false;
                    }

                    activeWidget.next('.' + settings.addWidgetClass).remove();
                    $(settings.state.placeholder).replaceWith(activeWidget);

                    var x = getRowIndex(activeWidget);
                    var y = getColumnIndexFromWidget(activeWidget);

                    $.ajax({
                        'type' : 'post',
                        'url'  : settings.updateUrl,
                        'data' :
                            {
                                'action'    : 'dashboardWidgets-move',
                                'x'         : x,
                                'y'         : y,
                                'originalX' : settings.state.originalX,
                                'originalY' : settings.state.originalY,
                                'id'        : activeWidget.attr('data-widget-id')
                            }
                    });

                    //e.originalEvent.dataTransfer.dropEffect = 'move';
                }

                function getColumnIndexFromWidget(dashboardWidget) {
                    return getColumnIndex($(dashboardWidget).closest(settings.columnSelector));
                }

                function getColumnIndex(dashboardColumn) {
                    return $(dashboardColumn).closest('.dashboard-columns').find('.dashboard-column').index(dashboardColumn);
                }

                function getRowIndex(dashboardWidget) {
                    return $(dashboardWidget).closest('.dashboard-column').find('.dashboard-widget').index(dashboardWidget);
                }

                function createAddWidgetButton(x, y, isAddColumnOperation) {
                    if (typeof isAddColumnOperation === 'undefined') {
                        isAddColumnOperation = false;
                    }

                    return $('<a/>', {
                        'class': settings.addWidgetClass,
                        'href': '/cms/createWidget?x=' + x + '&y=' + y + '&action=dashboardWidgets-add&addColumn=' + isAddColumnOperation,
                        'target': 'createWidget'
                    }).append($('<span/>').text("Add Widget"));
                }

                function createAddRowContainerAndButton(x, y) {
                    return $('<div/>', {
                        'class' : settings.addWidgetContainerClass
                    }).append(createAddWidgetButton(x, y));
                }

                function createColumnGutterAndButton(y) {
                    return $('<div/>', {
                        'class' : settings.columnGutterClass,
                        'draggable' : true
                    }).append($('<div/>', {
                        'class' : 'drag-handle'
                    })).append(createAddWidgetButton(0, y, true));
                }

                function insertRowButtons(dashboardWidget) {
                    var $dashboardWidget = $(dashboardWidget);
                    var xIndex = getRowIndex(dashboardWidget);
                    var yIndex = getColumnIndexFromWidget(dashboardWidget);
                    //var $widget = $dashboardWidget.find('.widget').first();

                    if (xIndex === 0) {
                        $dashboardWidget.before(createAddRowContainerAndButton(xIndex, yIndex));
                    }
                    //$widget.before(getAddColumnButton(yIndex));

                    $dashboardWidget.after(createAddRowContainerAndButton(xIndex + 1, yIndex));
                    //$widget.after(getAddColumnButton(yIndex + 1));
                }

                function insertColumnGutter(dashboardColumn) {
                    var $dashboardColumn = $(dashboardColumn);
                    var columnIndex = getColumnIndex($dashboardColumn);

                    if (columnIndex === 0) {
                        $dashboardColumn.before(createColumnGutterAndButton(columnIndex));
                    }

                    $dashboardColumn.after(createColumnGutterAndButton(columnIndex + 1));
                }

                bsp_utils.onDomInsert(document, 'meta[name="widget"]', {

                    'insert': function (meta) {
                        var $meta = $(meta);
                        $meta.closest('.popup').trigger('close.popup');
                        var newDashboardWidget =
                            $('<div/>', {'class' : 'frame dashboard-widget', 'draggable' : 'true'}).append(
                                $('<a/>', { 'href' : $meta.attr('content')})
                            );

                        var x = $meta.attr('data-x');
                        var y = $meta.attr('data-y');

                        var $dashboard = $('.dashboard-columns');
                        var $columns = $dashboard.find(settings.columnSelector);
                        var $column = $($columns.get(y));

                        if ($meta.attr('data-add-column') === 'true') {
                            var newColumn = $('<div/>', {
                                'class' : 'dashboard-column',
                                'style' : 'flex: 320 320 auto'
                            });
                            $column.before(newColumn);
                            insertColumnGutter(newColumn);
                            $column = newColumn;
                        }

                        var widgetInRow = $column.find(settings.widgetSelector).get(x);

                        if (widgetInRow) {
                            $(widgetInRow).before(newDashboardWidget);
                        } else {
                            $column.append(newDashboardWidget);
                        }


                        newDashboardWidget.find('a:only-child').click();
                        insertRowButtons(newDashboardWidget);
                        var attachInterval = setInterval(function() {
                            var newWidget = newDashboardWidget.find('.widget').first();
                            if (newWidget) {
                                //attachButtons(newDashboardWidget);
                                insertRowButtons(attachInterval);
                                this.clearInterval();
                            }
                        }, 1);
                    }

                });
            }

        });
    });