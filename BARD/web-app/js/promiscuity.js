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

var bigSpinnerImage = '<div class="tab-message">' + '<img src="' + bardAppContext + '/images/ajax-loader.gif"' + ' alt="loading" title="loading"/></div>';
var waitingImage = bigSpinnerImage;

var PromiscuityHandler = {
    setup:function () {
        $(".promiscuity:not(.processed)").each(function () {
            var promiscuityDivId = $(this).attr('id');

            var url = $(this).attr('href');
            $.ajax({
                url:url,
                type:'GET',
                cache:false,
                //timeout: 10000,
                beforeSend:function () {
                    $('#' + promiscuityDivId).html(waitingImage);
                },
                success:function (promData) {
                    $('#' + promiscuityDivId).html(promData);
                    $('#' + promiscuityDivId + ' .popover-link').popover({
                        content:function () {
                            var scaffoldId = $(this).attr('data-detail-id');
                            return $('#' + scaffoldId).html();
                        },
                        placement:'bottom',
                        html:true,
                        trigger:'manual',
                        animation:false
                    }).click(function (ee) {
                            $('.popover-link').not(this).popover('hide');
                            $(this).popover('toggle');
                            //needed to block firing of the document's click event which would hide in return all popoevers (used when the user clicks away)
                            ee.stopPropagation();
                        });
                },
                error:handleAjaxError(function () {
                    $('#' + promiscuityDivId).html('No data found');
                }),
                complete:function () {
                    $(this).addClass("processed");
                }
            });
        });
    }
};

$(document).on('search.complete', '#compounds', PromiscuityHandler.setup);

$(document).ready(function () {
    $(document).on("click", "html:not(.popover-link)", function (ee) {
        $('.popover-link').popover('hide');
    });
    PromiscuityHandler.setup();
});
