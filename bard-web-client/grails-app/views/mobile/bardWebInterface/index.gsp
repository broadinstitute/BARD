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
    <r:require modules="jqueryMobile, jqueryMobileInit, autocomplete"/>
    <r:layoutResources/>
</head>

<body>

<div data-role="page">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    %{--<div data-role="header">--}%
    %{--<h1>BARD</h1>--}%
    %{--</div><!-- /header -->--}%

    <div data-role="content" style="text-align: center">
        <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" align="middle" style="max-width: 80%;"
                 alt="BioAssay Research Database"/>
        </a>

        <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm">
            <div data-role="fieldcontain">
                <label for="searchString">Search BARD:</label>
                <input type="search" name="searchString" id="searchString"
                       value="${flash.searchString ?: params?.searchString}"/>
            </div>

            <button value="Search" name="search" data-theme="b" type="submit" class="ui-btn-hidden" aria-disabled="false" data-inline="true" data-theme="b">Submit</button>

        </g:form>
    </div><!-- /content -->
</div><!-- /page -->
<r:layoutResources/>
</body>
</html>