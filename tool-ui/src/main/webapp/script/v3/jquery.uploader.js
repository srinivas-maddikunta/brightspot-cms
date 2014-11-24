// Toggle display of other areas.
(function($, win, undef) {

var $win = $(win),
    cacheNonce = 0;

$.plugin2('uploader', {
    '_init': function(selector) {
        var plugin = this;

        plugin.$caller.delegate(selector, 'change', function(event) {
            var files = event.target.files;
            var $this = $(this);
            $this.uploader('upload', files);
        });
    },

    '_beforeUpload': function(files) {

        var caller = this.$caller;

        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileSelector = caller.closest('.fileSelector');
            var uploadContainer = $('<div/>', { 'class': 'upload-preview loading' });
            var previewWrapper = $('<div/>', {'class': 'preview-wrapper'});

            var radialProgress = $('<div/>', {
                'class': 'radial-progress',
                'data-progress': '0'
            })
            .append($('<div/>', {'class': 'circle'})
                .append($('<div/>', {'class': 'mask full'})
                    .append($('<div/>', {'class': 'fill'})))
                .append($('<div/>', {'class': 'mask half'})
                    .append($('<div/>', {'class': 'fill'}))
                    .append($('<div/>', {'class': 'fill fix'}))
                )
            )
            .append($('<div/>', {'class': 'inset'})
                .append($('<div/>', {'class': 'percentage'})
                        .append($('<span/>'))
                )
            );

            if (file.type.match('image.*')) {
                var img = $('<img/>');
                this.preview(img, file);
                previewWrapper.append(img);
            }

            previewWrapper.append(radialProgress);
            uploadContainer
                .append(previewWrapper)
                .append($('<div/>', {
                    'class': 'caption',
                    'text': file.name
                }));

            fileSelector.find('.fileSelectorNewUpload').hide();
            fileSelector.append(uploadContainer);
        }
    },

    'upload': function(files) {

        var plugin = this;
        plugin._beforeUpload(files);

        for (var i = 0; i < files.length; i++) {

            var file = files[i];

            _e_.add({
                name: file.name,
                file: file,
                notSignedHeadersAtInitiate: {
                    'Cache-Control': 'max-age=3600'
                },
                xAmzHeadersAtInitiate: {
                    'x-amz-acl': 'public-read'
                },
                complete: function () {
                    plugin._complete(plugin.$caller);
                },
                progress: function (progress) {
                    plugin._progress(plugin.$caller, Math.round(Number(progress*100)));
                }
            });
        }
    },

    '_complete': function($caller) {
        $caller.closest('.fileSelector').find('.upload-preview').removeClass('loading');
    },

    '_progress': function($caller, percentageComplete) {
        $caller.closest('.fileSelector').find('[data-progress]').attr('data-progress', percentageComplete);
    },

    'preview': function(img, file) {

        //check browser support for File Apis
        if(!(window.File && window.FileReader && window.FileList)) {
            return;
        }

        var reader = new FileReader();
        reader.onload = (function(readFile) {
            return function(event) {
                img.attr('src', event.target.result);
            };
        })(file);

        reader.readAsDataURL(file);
    }
});

}(jQuery, window));
