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
    <title></title>
</head>

<body>
<div>
    <ul>
        <g:each var="compound" in="${assayCompoundsJsonArray}">
            <g:set var="cid" value="${compound.split('/').toList().last()}"/>
            <li>
                <g:link action="showCompound" params="[cid: cid]">
                    <g:message code="assay.compound.display" args="[cid]" default="${cid}"/>
                </g:link>
            </li>
        </g:each>
    </ul>
</div>

<div class="pagination">
    <g:paginate total="${totalCompounds}" params="[assay: aid]"/>
</div>

</body>
</html>