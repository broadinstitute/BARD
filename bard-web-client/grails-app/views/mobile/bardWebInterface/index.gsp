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
    <r:require modules="jqueryMobile, jqueryMobileInit, autocomplete, search, promiscuity, bootstrap"/>
    <r:layoutResources/>
    <r:script>
        $(document).ready(function () {
            $('#searchButton').click(function () {
//              Triggers the search process (in search.js). We use the button-link instead of a standard button-submit element
                var searchString = $("#searchString").val();
                handleMainFormSubmit(searchString);
            });

//          Hnadle the proble-list ajax-updating
            handleSearch('/bardwebclient/bardWebInterface/showProbeList', '', 'probeListTab', 'totalCompounds', 'Probes ', 'probeListDiv');
        });

        $(document).on('search.complete', function () {
            //We need to disable ajax behaviour on the pagination links for the mobile experiencr to woek
            $('#paginateBar a').attr("data-ajax", "false");
            //Adjust the structure thumbnail images to the mobile screen size.
            adjustStructureThumbnailSize()
        });

        //      We need that to adjust the size of the structure image dynamically based on the screen size
        function adjustStructureThumbnailSize() {
            var w = Math.round(window.innerWidth / 3);
            var width = (w > 180) ? 180 : w;
            var height = Math.round(width * 5 / 6);
            $('#structureThumbnail').attr('width', width).attr('height', height);
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

        <p><a href="#probeList" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="probeListTab">Probes (0)</a></p>


        <p><a href="#search" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="searchTab">Search BARD</a></p>
    </div>
</div>

<g:render template="/mobile/bardWebInterface/search"/>
<g:render template="/mobile/bardWebInterface/probeList"/>

<r:layoutResources/>
</body>
</html>