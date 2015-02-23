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
            removeWidgetClass: 'widget-remove',
            columnSizerClass: 'columnSizer'
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
                $widgets.on('dragstart', dragStart);
                $widgets.on('dragend', dragEnd);
                $widgets.on('dragover', bsp_utils.throttle(settings.throttleInterval, dragOver));
                $widgets.on('dragenter', bsp_utils.throttle(settings.throttleInterval, dragEnter));
                $widgets.on('dragleave', bsp_utils.throttle(settings.throttleInterval, dragLeave));
                //webkit bug not fixed, cannot use drop event v. 39.0.2171.99 https://bugs.webkit.org/show_bug.cgi?id=37012
                //$widgets.on('drop'     , drop);

                /**
                 * Enables dashboard edit mode
                 */
                $body.on('click', '.dashboard-edit', function () {
                    $dashboard.toggleClass(settings.editModeClass);
                    $widgets.prop('draggable', !$widgets.prop('draggable'));

                    if ($dashboard.hasClass(settings.editModeClass)) {

                        $columns.each(function(yIndex, col) {
                            var $col = $(col);
                            addColumnSizer($col);
                            $col.find(settings.widgetSelector).each(function(xIndex, dashboardWidget) {
                                attachButtons(dashboardWidget);
                            });
                        });
                    } else {
                        $dashboard.find('.' + settings.addWidgetClass + ', .' + settings.addColumnClass + ', .' + settings.columnSizerClass).detach();
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
                        $(this).find('.'+ settings.removeWidgetClass).detach();
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
                });

                $body.on('change', '.' + settings.columnSizerClass, function() {
                    var $input = $(this);
                    var $column = $input.parent();
                    var currentSize = $column.css('flex-grow');

                    if (!(currentSize === $input.val())) {
                        $.ajax({
                            'url' : settings.updateUrl,
                            'type'   : 'post',
                            'data': {
                                'action' : 'dashboardColumns-resize',
                                'y'      : getColumnIndex($column),
                                'size'   : $input.val()
                            }
                        })

                        $column.css('flex-grow', $input.val());
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

                    if (this === settings.state.dragTarget || this === settings.state.placeholder || this === settings.state.activeWidget) {
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

                    activeWidget.next('.' + settings.addWidgetClass).detach();
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

                function getAddRowButton(x, y) {
                    return $('<a/>', {
                                'class': settings.addWidgetClass,
                                'href': '/cms/createWidget?x=' + x + '&y=' + y + '&action=dashboardWidgets-add',
                                'target': 'createWidget'
                            });

                }

                function getAddColumnButton(y) {
                    return $('<a/>', {
                               'class' : settings.addColumnClass,
                               'href'  : '/cms/createWidget?x=' + 0 + '&y=' + y + '&action=dashboardWidgets-add&addColumn=true',
                               'target': 'createWidget'
                           });
                }

                function attachButtons(dashboardWidget) {
                    var $dashboardWidget = $(dashboardWidget);
                    var xIndex = getRowIndex(dashboardWidget);
                    var yIndex = getColumnIndexFromWidget(dashboardWidget);
                    var $widget = $dashboardWidget.find('.widget').first();

                    $widget.before(getAddRowButton(xIndex, yIndex));
                    $widget.before(getAddColumnButton(yIndex));

                    $widget.after(getAddRowButton(xIndex + 1, yIndex));
                    $widget.after(getAddColumnButton(yIndex + 1));
                }

                function addColumnSizer(dashboardColumn) {
                    var $dashboardColumn = $(dashboardColumn);
                    $dashboardColumn.prepend($('<input/>', {
                            'class': settings.columnSizerClass,
                            'name' : getColumnIndex(dashboardColumn),
                            'value': $dashboardColumn.css('flex-grow')
                        }
                    ));
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
                                'style' : 'flex-grow: 1'
                            });
                            $column.before(newColumn);
                            $column = newColumn;
                        }

                        var widgetInRow = $column.find(settings.widgetSelector).get(x);

                        if (widgetInRow) {
                            $(widgetInRow).before(newDashboardWidget);
                        } else {
                            $column.append(newDashboardWidget);
                        }


                        newDashboardWidget.find('a:only-child').click();
                        attachButtons(newDashboardWidget);
                        var attachInterval = setInterval(function() {
                            var newWidget = newDashboardWidget.find('.widget').first();
                            if (newWidget) {
                                attachButtons(newDashboardWidget);
                                clearInterval(attachInterval);
                            }
                        }, 1);
                    }

                });
            }

        });
    });