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
    <r:require modules="jqueryMobile, jqueryMobileInit, autocomplete, search, promiscuity, activeVrsTested, bootstrap"/>
    <r:layoutResources/>
    <r:script>
        $(document).ready(function () {
            $('#searchButton').click(function () {
//              Triggers the search process (in search.js). We use the button-link instead of a standard button-submit element
                var searchString = $("#searchString").val();
                handleMainFormSubmit(searchString);
            });
        });

        $(document).on('search.complete', function () {
            //We need to disable ajax behaviour on the pagination links for the mobile experiencr to woek
            $('#paginateBar a').attr("data-ajax", "false");
            //Adjust the structure thumbnail images to the mobile screen size.
            adjustStructureThumbnailSize()
        });

        function adjustStructureThumbnailSize() {
            var w = Math.round(window.innerWidth / 3);
            var width = (w > 180) ? 180 : w;
            var height = Math.round(width * 5/6);
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

<div data-role="page" id="search">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center">
        <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" align="middle" style="max-width: 80%;"
                 alt="BioAssay Research Database"/>
        </a>

        <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" data-ajax="false">
            <div data-role="fieldcontain">
                <label for="searchString">Search BARD:</label>
                <input type="search" name="searchString" id="searchString"
                       value="${flash.searchString ?: params?.searchString}"/>
            </div>

        %{--<button value="Search" name="search" id="searchButton" data-theme="b" type="submit" class="ui-btn-hidden"--}%
        %{--aria-disabled="false" data-inline="true" data-theme="b">Submit</button>--}%

            <a id="searchButton" data-inline="true" data-transition="slide" data-role="button" href="#searchResults"
               data-corners="true"
               data-shadow="true" data-iconshadow="true" data-wrapperels="span" data-theme="b"
               class="ui-btn ui-shadow ui-btn-corner-all ui-btn-inline ui-btn-up-b"><span
                    class="ui-btn-inner ui-btn-corner-all"><span class="ui-btn-text">Submit</span></span></a>
        </g:form>
        <div>
            <g:link controller="bardWebInterface" action="turnoffMobileExperience" data-ajax="false">
                <g:message code="mobile.disable.experience" default="Switch to the regular website"/>
            </g:link>
        </div>
    </div><!-- /content -->
</div><!-- /page -->

<g:render template="/mobile/bardWebInterface/searchResults"/>

<r:layoutResources/>
</body>
</html>