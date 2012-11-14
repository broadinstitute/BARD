

$(document).ready(function () {
    $('#idModalDiv').modal({
        show:false
    });
    $('#idModalDiv').css('width', 'auto') //Disable the default width=560px from bootstrap.css
    $('#idSearchButton').click(function () {
        var idSearchTypeSelected = $('input:radio[name=idSearchType]:checked').val();
        var ids = $('#idSearchString').val()
        //construct the query into a form that we want
        //replace with a single space
        var constructedSearch =ids.replace(/(\r\n|\n|\r)/gm," ");
        if(idSearchTypeSelected != 'ALL'){
            constructedSearch = idSearchTypeSelected + ":" +constructedSearch
        }
        $('#searchString').attr('value', constructedSearch);
        $('#searchForm').submit();

    });

});
