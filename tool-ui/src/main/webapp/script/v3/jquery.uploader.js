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

    '_beforeUpload': function(file, $inputSmall) {

        var plugin = this;
        var $fileSelector = $inputSmall.find('.fileSelector').first();

        $.ajax({
            url: '/cms/imagePreview',
            'data': { upload: 'true' },
            dataType: 'html'
        }).done(function(html) {

            $inputSmall.append(html);
            var $uploadPreview = $inputSmall.find('.upload-preview').first();

            if (file.type.match('image.*')) {
                plugin._displayPreview($uploadPreview.find('img').first(), file);
            }

            var $select = $fileSelector.find('select').first();

            if($select.find('option[value="keep"]').size() < 1) {
                $select.prepend($('<option/>', {
                    'data-hide': '.fileSelectorItem',
                    'data-show': '.fileSelectorExisting',
                    'value': 'keep',
                    'text': 'Keep Existing'
                }));
            }

            $select.val('keep');
        });
    },

    'upload': function(files) {

        var plugin = this;
        var $caller = this.$caller;
        var $inputSmall = $caller.closest('.inputSmall');

        for (var i = 0; i < files.length; i++) {
            var file = files[i];

            plugin._beforeUpload(file, $inputSmall);

            _e_.add({
                name: encodeURIComponent(file.name),
                file: file,
                notSignedHeadersAtInitiate: {
                    'Cache-Control': 'max-age=3600'
                },
                xAmzHeadersAtInitiate: {
                    'x-amz-acl': 'public-read'
                },
                complete: function () {
                    plugin._afterUpload($inputSmall);
                },
                progress: function (progress) {
                    plugin._progress($inputSmall, Math.round(Number(progress*100)));
                }

            });
        }
    },

    '_afterUpload': function($inputSmall) {
        $inputSmall.find('.upload-preview').removeClass('loading');
    },

    '_progress': function($inputSmall, percentageComplete) {
        $inputSmall.find('[data-progress]').attr('data-progress', percentageComplete);
    },

    '_displayPreview': function(img, file) {

        if(!(window.File && window.FileReader && window.FileList)) {
            return null;
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
