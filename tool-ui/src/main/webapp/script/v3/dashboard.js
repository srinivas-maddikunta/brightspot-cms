define([
        'jquery',
        'bsp-utils'],

    function ($, bsp_utils) {

        bsp_utils.onDomInsert(document, '.dashboard-columns', {

            'insert': function (dashboard) {

                var defaults = {
                    dragClass: 'drag-active',
                    editModeClass: 'dashboard-editable',
                    placeholderClass: 'widget-ghost',
                    throttleInterval: 100
                };

                var settings = $.extend({}, defaults, {state: {}});

                var $dashboard = $(dashboard);
                var $columns = $dashboard.find('.dashboard-column');
                var $widgets = $dashboard.find('.dashboard-widget');

                $('body').on('click', '.dashboard-edit', function () {
                    $dashboard.toggleClass(settings.editModeClass);
                    $widgets.prop('draggable', !$widgets.prop('draggable'));

                    var enteredEditMode = $dashboard.hasClass(settings.editModeClass);

                    if (enteredEditMode) {
                        $widgets.find('.widget').prepend($('<div/>', {'class': 'widget-overlay'}));
                        $columns.each(function (i, el) {
                            var $el = $(el);
                            $el.append(
                                $('<div />', {
                                    'class': 'dashboard-widget dashboard-add-widget'
                                }).append(
                                    $('<a/>', {
                                        'class': 'widget',
                                        'href': '/cms/addWidget?col=' + i,
                                        'target': 'addWidget'
                                    })
                                )
                            );
                        });
                    } else {
                        $dashboard.find('.dashboard-add-widget, .widget-overlay').detach();
                    }
                });

                $widgets.on('dragstart', dragStart);
                $widgets.on('dragend', dragEnd);
                $widgets.on('dragover', bsp_utils.throttle(settings.throttleInterval, dragOver));
                $widgets.on('dragenter', bsp_utils.throttle(settings.throttleInterval, dragEnter));
                $widgets.on('dragleave', bsp_utils.throttle(settings.throttleInterval, dragLeave));
                //webkit bug not fixed, cannot use drop event v. 39.0.2171.99 https://bugs.webkit.org/show_bug.cgi?id=37012
                //$widgets.on('drop'     , drop);

                function dragStart(e) {
                    settings.state.activeWidget = this;
                    var $this = $(this);

                    settings.state.originalY = $this.closest('.dashboard-column').index();
                    settings.state.originalX = $this.index();


                    var $widget = $(e.target);
                    $widget.toggleClass(settings.dragClass);

                    var dataTransfer = e.originalEvent.dataTransfer;
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
                        return;
                    }

                    settings.state.dragTarget = this;

                    var $dragTarget = $(this);

                    if ($dragTarget.hasClass(settings.dragClass) || $dragTarget.hasClass(settings.placeholderClass)) {
                        return;
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
                        return;
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
                        return;
                    }

                    $(settings.state.placeholder).replaceWith(activeWidget);
                    var column = activeWidget.closest('.dashboard-column');
                    var y = column.index();
                    var x = activeWidget.index();

                    $.ajax({
                        'type' : 'post',
                        'url'  : '/cms/misc/updateUserDashboard/',
                        'data' :
                            {
                                'action' : 'dashboardWidgets-move',
                                'targetX'      : activeWidget.index(),
                                'targetY'      : column.index(),
                                'originalX'   : settings.state.originalX,
                                'originalY'   : settings.state.originalY,
                                'id'     : activeWidget.attr('data-widget-id')
                            }
                    });

                    //e.originalEvent.dataTransfer.dropEffect = 'move';
                }


            }

        });

        bsp_utils.onDomInsert(document, 'meta[name="addWidget"]', {

            'insert': function (meta) {
                var $meta = $(meta);
                $meta.closest('.popup').trigger('close.popup');
                var col = $meta.attr('data-column');
                var newWidget =
                    $('<div/>', {'class' : 'frame dashboard-widget', 'draggable' : 'true'}).append(
                        $('<a/>', { 'href' : $meta.attr('content')})
                    );

                $($('.dashboard-columns').find('.dashboard-column').get(col)).find('.dashboard-add-widget').before(newWidget);
                newWidget.find('a:only-child').click();

                $.ajax({
                    'type': 'post',
                    'url' : $meta.attr('data-updateUrl'),
                    'data': { 'col' : $meta.attr('data-column')}
                });

            }

        });

    });