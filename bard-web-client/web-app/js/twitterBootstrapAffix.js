//This is to apply the current position to Twitter Bootstrap's Affix to make the scrolling natural
$(document).ready(function () {
    $('#twitterBootstrapAffixNavBar').each(function () {
        $(this).affix({
            offset:$(this).position()
        });
    });

    //This to fix a bug in Chrome position:fixed where sometime the div disappears
    $(document).on('click', '#twitterBootstrapAffixNavBar', function () {
        window.scrollTo(window.pageXOffset, window.pageYOffset - 1);
    });
});
