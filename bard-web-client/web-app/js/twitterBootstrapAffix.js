//This is to apply the current position to Twitter Bootstrap's Affix to make the scrolling natural
$(document).ready(function () {
    $('#twitterBootstrapAffixNavBar').affix({
        offset:$('#twitterBootstrapAffixNavBar').position()
    });
    //This to fix a bug in Chrome position:fixed where sometime the div disappears
    $(document).on('click', '#twitterBootstrapAffixNavBar', function () {
        window.scrollTo(window.pageXOffset, window.pageYOffset - 1);
    });
});
