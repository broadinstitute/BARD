%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 11/19/12
  Time: 3:33 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="autocomplete, search, bootstrap, core, jqueryMobile"/>
    <r:layoutResources/>
    <r:script>
        $(document).ready(function () {
//          Hnadle the proble-list ajax-updating
            handleSearch("${createLink(controller: 'bardWebInterface', action: 'showProbeList')}", '', 'probeListTab', 'totalCompounds', 'Probes ', 'probeListDiv');
        });

        $(document).on('search.complete', '#probeList', function () {
            //Restore the JQuery Mobile navigation button style after overriding the button's html with the total-value.
            updateNavigationButtonStyle($('#probeListTab'));

            //We need to disable ajax behaviour on the pagination links for the mobile experience to work
            $('#paginateBar a').attr("data-ajax", "false");
            //Adjust the structure thumbnail images to the mobile screen size.
            adjustStructureThumbnailSize()
        });

        //Resize structure thumbnails with every window-resize even.
        $(window).resize(adjustStructureThumbnailSize);

        //      We need that to adjust the size of the structure image dynamically based on the screen size
        function adjustStructureThumbnailSize() {
            var w = Math.round(window.innerWidth / 3);
            var width = (w > 180) ? 180 : w;
            var height = Math.round(width * 5 / 6);
            $('.structureThumbnail').attr('width', width).attr('height', height);
        }

        function updateNavigationButtonStyle(buttonElement) {
            var existingHtml = $(buttonElement).text();
            var newHtml = '<span class="ui-btn-inner ui-btn-corner-all"><span class="ui-btn-text">' + existingHtml + '</span>' + '<span class="ui-icon ui-icon-arrow-r ui-icon-shadow"> </span></span>'
            $(buttonElement).html(newHtml);
        }
    </r:script>

%{--<style>--}%
%{--.ui-page {--}%
%{---webkit-backface-visibility: hidden;--}%
%{--}--}%
%{--</style>--}%
</head>

<body>

<div data-role="page" id="home">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center">
        <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" align="middle" style="max-width: 80%;"
                 alt="BioAssay Research Database"/>
        </a>

        <p><br></p>

        <a href="#probeList" data-role="button"
           data-icon="arrow-r" data-iconpos="right" data-transition="slide"
           id="probeListTab">Probes (0)</a>


        <a href="${createLink(controller: 'bardWebInterface', action: 'search')}" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
           id="searchTab">Search BARD</a>
    </div>
</div>

<g:render template="/mobile/bardWebInterface/probeList"/>
<r:layoutResources/>
</body>
</html>
