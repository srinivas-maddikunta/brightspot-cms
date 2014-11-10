$(function() {
    $('body').on('change', '#uploadTester', function(event) {
        console.log("changed...");
        files = event.target.files;
        for (var i = 0; i < files.length; i++){

            _e_.add({
                name: 'test_' + Math.floor(1000000000*Math.random()),
                file: files[i],
                notSignedHeadersAtInitiate: {
                    'Cache-Control': 'max-age=3600'
                },
                xAmzHeadersAtInitiate : {
                    'x-amz-acl': 'public-read'
                },
                complete: function(){
                    console.log('complete................yay!');
                },
                progress: function(progress){
                    console.log('making progress: ' + progress);
                }
            });
        }

        $(event.target).val('');
    });
});