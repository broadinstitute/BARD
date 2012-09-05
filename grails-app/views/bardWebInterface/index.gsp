<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/8/12
  Time: 3:46 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="assays.query.label" args="[]" default="Find Assay's Compounds"/></title>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><g:link controller="bardWebInterface" action="index"><g:message
                code="default.home.label"/></g:link>
        </li>
    </ul>
</div>

<g:form name="searchForm" controller="bardWebInterface" action="findCompoundsForAssay">
    <div class="listHeader">
        <div class="listTitle">
            <h1><g:message code="assay.query.text" args="[]"/></h1><br>
        </div>

        <div class="clear"></div>
    </div>

    <div class="content">
        <label for="assay">
            <g:message code="assay.id.label" default="AID"/>
        </label>
        <g:textField name="assay" value=""></g:textField>
        <g:submitButton name="findCompoundsForAssay"
                        value="${message(code: 'default.button.search.label', default: 'Search')}"/>
    </div>
</g:form>
</body>
</html>