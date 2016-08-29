require(['jquery'], function($) {
    $(window).load(function() {

        const SUCCESS_RESPONSE = 'Test message sent!';
        const FAILURE_RESPONSE = 'Unable to send test message!';

        var $smsButton = $('.smsButton');
        var $smsResponseText = $('.smsResponseText');
        var $phoneNumber = $smsButton.parent().find('.inputSmall').find('textarea');

        $smsButton.on('click', function (e) {
            e.preventDefault();
            $.ajax({
                url: CONTEXT_PATH + 'testSms',
                type: 'POST',
                data: {
                    phoneNumber: $phoneNumber.val()
                },
                beforeSend: function () {
                    $smsButton.hide();
                    $smsResponseText.addClass('smsPendingResponse');
                },
                error: function () {
                    $smsResponseText.removeClass('smsPendingResponse');
                    $smsResponseText.addClass('smsErrorResponse');
                    $smsResponseText.text(FAILURE_RESPONSE);
                },
                success: function (data) {
                    $smsResponseText.removeClass('smsPendingResponse');
                    if (data == SUCCESS_RESPONSE) {
                        $smsResponseText.addClass('smsSuccessResponse');
                    } else if (data == FAILURE_RESPONSE) {
                        $smsResponseText.addClass('smsErrorResponse');
                    }
                    $smsResponseText.text(data);
                }
            });
        });
    });
});
