$(document).ready(function () {
    $(document).on("click", "a.analogs", function (event) {
        var searchString = $(this).attr('data-structure-search-params');
        var cutoff = $(this).siblings("#cutoff").val()
        var intRegex = /^\d+(\.\d+)?$/;
        if (!intRegex.test(cutoff.trim()) || cutoff < 0 || cutoff > 100) {
            //prevent the submission if out of range; keep the dialog box open
            event.preventDefault();
            $(this).parent().parent().parent().find('[data-toggle="dropdown"]').dropdown('toggle');
            return false;
        }
        searchString = searchString + " threshold:" + cutoff
        $('#searchString').attr('value', searchString);
        $('#searchForm').submit();
    });

});

//Overrides the Twitter Bootstraps' Dropdown behavior that hides the menu when a menu item was clicked
$(document).on('click', '#cutoff', function (event) {
    $(this).select();
    $(this).keypress(function (event) {
        if (event.keyCode == 13) {//enter
            $(this).parent().find('a').click();
        }
    });
    //prevent default event handler
    return false;
});