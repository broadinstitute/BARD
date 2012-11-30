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
    <title>Missing page error</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobile, jqueryMobileInit"/>
    <r:layoutResources/>
</head>

<body>

<div data-role="page">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center">
        <p>Sorry but this page has not been adjusted yet for a mobile experience</p>
        <g:link controller="bardWebInterface" action="index" data-ajax="false">Home page</g:link>
    </div><!-- /content -->
</div><!-- /page -->
<r:layoutResources/>
</body>
</html>