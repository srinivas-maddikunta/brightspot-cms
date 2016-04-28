define([ 'jquery', 'bsp-utils' ], function ($, bsp_utils) {
    var SAVABLE_DATA = 'search-savable';

    $(document).on('click', '.searchHistory a', function (event) {
        var $popup = $(event.target).popup('container');

        if ($popup) {
            $popup.removeData(SAVABLE_DATA);
        }
    });

    $(document).on('submit', '.searchForm form', function (event) {
        var $form = $(event.target);

        if ($form.length > 0) {
            var $popup = $form.popup('container');

            if ($popup) {
                var savable = $popup.data(SAVABLE_DATA);

                if (savable) {
                    $form.find('input[name="cx"], input[name="si"]').prop('disabled', false);

                } else {
                    setTimeout(function () {
                        $popup.data(SAVABLE_DATA, true);
                    }, 0);
                }
            }
        }
    });

    bsp_utils.onDomInsert(document, '.searchRecent', {
        insert: function (recent) {
            var $recent = $(recent);
            var $frame = $recent.closest('.frame');

            if ($frame.attr('name') !== 'miscSearch') {
                var $select = $('<select/>', {
                    change: function () {
                        $(this).find('> :selected').data('$link').click();
                        return false;
                    }
                });

                $select.append($('<option/>', {
                    text: $recent.find('> h2').text()
                }));

                $recent.find('> ul > li > a').each(function () {
                    var $link = $(this);
                    var $option = $('<option/>', {
                        text: $link.text()
                    });

                    $option.data('$link', $link);
                    $select.append($option);
                });

                $frame.find('.searchControls').prepend($('<form/>', {
                    'class': 'searchRecentSelect',
                    html: $select
                }));
            }
        }
    });
});