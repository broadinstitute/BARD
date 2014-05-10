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

$(document).ready(function () {
    $(document).on("click", "a.analogs",function (event) {
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
    }).on('keydown', 'ul.dropdown-menu', function (event) {
        if (event.keyCode == 27) {//ESC
            $(this).dropdown('toggle');
        }
    });

    //Use tooltip to display the SMILES string in case the it is larger than 30 character.
    $("li[rel=tooltip]").tooltip({container: 'body', placement: "auto bottom"});
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
