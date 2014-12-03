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
            url: '/cms/filePreview',
            data: { displayProgress : 'true' },
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
        var $fileSelector = $inputSmall.find('.fileSelector');

        for (var i = 0; i < files.length; i++) {
            var file = files[i];

            plugin._beforeUpload(file, $inputSmall);
            var filePath = $fileSelector.attr('data-new-path-start') + "/" + encodeURIComponent(file.name);

            _e_.add({
                name: filePath,
                file: file,
                notSignedHeadersAtInitiate: {
                    'Cache-Control': 'max-age=3600'
                },
                xAmzHeadersAtInitiate: {
                    'x-amz-acl': 'public-read'
                },
                complete: function (request) {
                    plugin._afterUpload($inputSmall, filePath);
                },
                progress: function (progress) {
                    plugin._progress($inputSmall, Math.round(Number(progress*100)));
                }

            });
        }
    },

    '_afterUpload': function($inputSmall, filePath) {
        var $uploadPreview  = $inputSmall.find('.upload-preview');
        var $fileSelector = $inputSmall.find('.fileSelector');
        var inputName = $fileSelector.attr('data-input-name');
        var localSrc = $uploadPreview.find('img').first().attr('src');

        var params = { };
        params['isNewUpload'] = true;
        params['inputName'] = inputName;
        params['fieldName'] = $fileSelector.attr('data-field-name');
        params['typeId'] = $fileSelector.attr('data-type-id');
        params[inputName + '.path'] = filePath;
        params[inputName + '.storage'] = $fileSelector.attr('data-storage');

        $uploadPreview.removeClass('loading');

        $.ajax({
            url: '/cms/filePreview',
            dataType: 'html',
            data: params
        }).done(function(html) {
            $uploadPreview.detach();
            $inputSmall.append(html);

            //prevent image pop-in
            var img = $inputSmall.find('.imageEditor-image').find('img').first();
            var remoteSrc = img.attr('src');
            img.attr('src', localSrc);
            $.ajax({
                url: remoteSrc
            }).done(function(html) {
                img.attr('src', remoteSrc);
            });
        });
    },

    '_progress': function($inputSmall, percentageComplete) {
        $inputSmall.find('[data-progress]').attr('data-progress', percentageComplete);
    },

    '_displayPreview': function(img, file) {

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
