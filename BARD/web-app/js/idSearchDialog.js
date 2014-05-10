/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//if some click the id search, run a regex on the search box string and populate box
//matches a number followed by a zero or more spaces, followed by max of one comma
//The question mark and the colon after the opening round bracket are the special syntax that
// you can use to tell the regex engine that this pair of brackets should not create a backreference
//You do that to optimize this regular expression
//see http://www.regular-expressions.info/brackets.html
var NUMBER_MATCHING_REGEX = /^\s*\d+\s*(?:,?\s*\d+\s*)*$/;
$(document).ready(function () {
    $(document).bind("idSearchBoxEvent", function (event, searchString) {
        var searchType = findSearchType(searchString)
        var currentSearchString = findSearchString(searchString)
        switch (searchType) { //must be one of these to qualify as a structure search

            case 'ADID': //this is an assay search with ids
                $("input[name='idSearchType'][value='ADID']").attr("checked", "checked");
                break;
            case 'CID':  //this is a compound search with ids
                $("input[name='idSearchType'][value='CID']").attr("checked", "checked");
                break;
            case 'PID':  //this is a project search with ids
                $("input[name='idSearchType'][value='PID']").attr("checked", "checked");
                break;
            case 'EID':  //this is an experiment search with ids
                $("input[name='idSearchType'][value='EID']").attr("checked", "checked");
                break;
            case 'ID':
                $("input[name='idSearchType'][value='ALL']").attr("checked", "checked");
                break;

        }
        $('#idSearchString').val(currentSearchString)

    });
    $('#idModalDiv').modal({
        show: false,
        keyboard: true //ESC closes the modal
    });
    $('#idModalDiv').css('width', 'auto'); //Disable the default width=560px from bootstrap.css
//    $('#idModalDiv').css('left', '40%');
    $("#idModalDiv").draggable({
        handle: ".modal-header"
    });
    $('#idModalDiv').on('show', function () {
        var currentSearch = $('#searchString').val()
        //populate the Id Box if the search box contains id searches
        if (currentSearch.length) {
            $(document).trigger("idSearchBoxEvent", currentSearch);
        }
    });
    $('#ids_image').on('click', function () {
        $('#idModalDiv').modal('show'); //open the modal
    });
    $('.idSearchButton').click(function () {
        var idSearchTypeSelected = $('input:radio[name=idSearchType]:checked').val();
        var ids = $('#idSearchString').val()
        //construct the query into a form that we want
        //replace with a single space
        var constructedSearch = ids.replace(/(\r\n|\n|\r)/gm, " ");
        if (idSearchTypeSelected != 'ALL') {
            constructedSearch = idSearchTypeSelected + ":" + constructedSearch
        }
        $('#searchString').attr('value', constructedSearch);

        $('#searchForm').submit();

    });

});
function findSearchString(searchString) {

    if (searchString.match(NUMBER_MATCHING_REGEX)) {//this is an id match
        return searchString;
    }
    var searchStringSplit = searchString.split(":");
    var stringAfterColon = $.trim(searchStringSplit[1])
    if (stringAfterColon.match(NUMBER_MATCHING_REGEX)) {
        return  stringAfterColon;
    }
    return ""
}
function findSearchType(searchString) {

    if (searchString.match(NUMBER_MATCHING_REGEX)) {//this is an id match
        return "ID";
    }
    //we want to find out if this is a Structure search
    var searchStringSplit = searchString.split(":");
    var searchType = searchStringSplit[0];
    if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
        searchType = searchType.toLowerCase();
        var stringAfterColon = $.trim(searchStringSplit[1])
        switch (searchType) { //must be one of these to qualify as a structure search

            case 'adid': //this is an assay search with ids
                if (stringAfterColon.match(NUMBER_MATCHING_REGEX)) {//this is an id match
                    return 'ADID';
                }

            case 'cid':  //this is a compound search with ids
                if (stringAfterColon.match(NUMBER_MATCHING_REGEX)) {//this is an id match
                    return 'CID'
                }
            case 'pid':  //this is an project search with ids
                if (stringAfterColon.match(NUMBER_MATCHING_REGEX)) {//this is an id match
                    return 'PID'
                }
            case 'eid':  //this is an project search with ids
                if (stringAfterColon.match(NUMBER_MATCHING_REGEX)) {//this is an id match
                    return 'EID'
                }
        }
    }
    return "NOT_ID"
}
