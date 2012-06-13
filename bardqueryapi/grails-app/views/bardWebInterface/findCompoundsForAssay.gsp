<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/12/12
  Time: 1:29 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="assays.compounds.label" args="[]" default="Assay Compounds"/></title>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><g:link controller="bardWebInterface" action="index"><g:message
                code="default.home.label"/></g:link>
        </li>
    </ul>
</div>

<g:form controller="bardWebInterface" action="findCompoundsForAssay" params="[assay: aid]">
    <div class="projectListHeader">
        <div class="projectListTitle">
            <h1><g:message code="assay.compound.text" args="[aid, totalCompounds]"/></h1><br>
        </div>

        <div class="projectListFilter">
            <label for="max">Max per page:</label>
            <g:textField id="max" name="max" value="${params.max}" maxlength="4" style="width: 8%"/>
            <g:actionSubmit value="Update" action="findCompoundsForAssay" style="background-color: #acD4F4;"/>
        </div>

        <div class="clear"></div>
    </div>
</g:form>

<div>
    <div>
        <g:each var="compound" in="${assayCompoundsJsonArray}">
            <g:set var="cid" value="${compound.split('/').toList().last()}"/>
            <g:link action="showCompound" params="[cid: cid]">
                <g:message code="assay.compound.display" args="[cid]" default="${cid}"/><br>
            </g:link>
        </g:each>
    </div>

    <div class="pagination">
        <g:paginate total="${totalCompounds}" params="[assay: aid]"/>
    </div>
</div>
</body>
</html>