//This is to apply the current position to Twitter Bootstrap's Affix to make the scrolling natural
$(document).ready(function () {
    $('.twitterBootstrapAffixNavBar').each(function () {
        $(this).affix({
            offset: $(this).position()
        }).css({'height': 'auto', 'overflow': 'visible'});
    });

    //This to fix a bug in Chrome position:fixed where sometime the div disappears
    $(document).on('click', '.twitterBootstrapAffixNavBar', function () {
        window.scrollTo(window.pageXOffset, window.pageYOffset - 1);
    });

    //This is needed to attach the SpyScroll to the body of the page
    $(document).ready(function () {
        $('body').scrollspy({target: '.bs-docs-sidebar'});
    });
});
