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
    <title><g:message code="target.show.label" args="[target]" default="Show Assays For Target ${target}"/></title>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><g:link controller="bardWebInterface" action="index"><g:message
                code="default.home.label"/></g:link>
        </li>
        <li><g:link controller="bardWebInterface" action="targets">Find Assays For Target</g:link>
        </li>
    </ul>
</div>
<div class="listHeader">
    <div class="listTitle">
        <h2>Assays for accession number: ${target}</h2> <br/>
        <h3>Click on each AID to view the list of compounds used in the assay</h3>
        <br/>
    </div>

    <div class="clear"></div>
</div>

            <table>
                <tr>
                    <th>AID</th>
                </tr>
                <g:each var="assay" in="${assays}">
                    <tr><td><g:link controller="bardWebInterface" action="findCompoundsForAssay" params="[assay:assay]">${assay}</g:link> </td></tr>
                </g:each>
            </table>

</body>
</html>