// Toggle display of other areas.
(function($, win, undef) {

$.plugin2('uploader', {
    '_init': function(selector) {
        var plugin = this;

        plugin.$caller.delegate(selector, 'change', function(event) {
            var files = event.target.files;
            var $this = $(this);
            $this.uploader('upload', files);
        });
    },

    '_beforeUpload': function(file, $inputSmall, index) {

        var plugin = this;
        var $fileSelector = $inputSmall.find('.fileSelector').first();

        $.ajax({
            url: '/cms/filePreview',
            data: { displayProgress : 'true' },
            dataType: 'html'
        }).done(function(html) {

            $inputSmall.append(html);
            var $uploadPreview = $inputSmall.find('.upload-preview').eq(index);

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
        var isMultiple = $caller.attr('multiple') ? true : false;

        for (var i = 0; i < files.length; i++) {
            var file = files[i];

            plugin._beforeUpload(file, $inputSmall, i);
            var filePath = $caller.attr('data-path-start') + "/" + encodeURIComponent(file.name);

            (function($caller, file, filePath, i) {
                window._e_.add({
                    name: filePath,
                    file: file,
                    notSignedHeadersAtInitiate: {
                        'Cache-Control': 'max-age=3600'
                    },
                    xAmzHeadersAtInitiate: {
                        'x-amz-acl': 'public-read'
                    },
                    complete: function () {
                        if (isMultiple) {
                            plugin._afterBulkUpload($caller, $inputSmall, filePath, i);
                        } else {
                            plugin._afterUpload($caller, $inputSmall, filePath);
                        }
                    },
                    progress: function (progress) {
                        plugin._progress($inputSmall, i, Math.round(Number(progress*100)));
                    }

                });
            })($caller, file, filePath, i);
        }
    },

    '_afterUpload': function($caller, $inputSmall, filePath) {
        var $uploadPreview  = $inputSmall.find('.upload-preview');
        var inputName = $caller.attr('data-input-name');
        var localSrc = $uploadPreview.find('img').first().attr('src');

        var params = { };
        params['isNewUpload'] = true;
        params['inputName'] = inputName;
        params['fieldName'] = $caller.attr('data-field-name');
        params['typeId'] = $caller.attr('data-type-id');
        params[inputName + '.path'] = filePath;
        params[inputName + '.storage'] = $caller.attr('data-storage');

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

    '_afterBulkUpload': function($caller, $inputSmall, filePath, index) {
        var $uploadPreview  = $inputSmall.find('.upload-preview').eq(index);
        $uploadPreview.removeClass('loading');
        var inputName = "file";

        $caller.detach();

        var params = { };
        params['writeInputsOnly'] = true;
        params['inputName'] = inputName;
        params[inputName + '.path'] = filePath;

        $.ajax({
            url: '/cms/content/uploadFiles',
            dataType: 'html',
            data: params
        }).done(function(html) {
            $inputSmall.prepend(html);
        });
    },

    '_progress': function($inputSmall, i, percentageComplete) {
        $inputSmall.find('[data-progress]').eq(i).attr('data-progress', percentageComplete);
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
