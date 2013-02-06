<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 2/6/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Probe List</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobile, jqueryMobileInit, core"/>
    <r:layoutResources/>
</head>

<body>
<div data-role="page" id="home">
    <div data-role="content" style="text-align: center">

        <div>
            <g:render template="/layouts/templates/mobileLoginStrip"/>
        </div>

        <div>
            <g:render template="/mobile/templates/compounds" model="[results: results]"/>
        </div>
    </div>
</div>
<r:layoutResources/>
</body>
</html>