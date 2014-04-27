<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/2/13
  Time: 2:27 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Marginal Product for Dataset ${dataset.name}</title>
</head>
<body>
<h1>Marginal Product for Dataset ${dataset.name}</h1>
<h2>${dataset.description}</h2>
<h3>Click on a number under "# Need MAAS", "# Need RTA" to view the list of AID's that need MAAS or RTA respectively</h3>

<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>Project UID</th>
        <th>Total AID's</th>
        <th>Marginal Product</th>
        <th># missing</th>
        <th># need MAAS</th>
        <th># need RTA</th>
        <th># Result map problems</th>
        <th># on hold</th>
    </tr>
    <g:each in="${marginalProductList}" var="marginalProduct">
        <tr>
            <td>${marginalProduct.projectUid}</td>
            <td>${marginalProduct.totalAidCount}</td>
            <td>${marginalProduct.marginalProduct}</td>

            <td>
                ${marginalProduct.missingCount}
                <g:if test="${marginalProduct.missingCount > 0}">
                    <g:link action="showMissingAid" params="[projectUid:marginalProduct.projectUid, datasetId:dataset.id]">
                        (click to list)
                    </g:link>
                </g:if>
            </td>

            <td>
                ${marginalProduct.countThatNeedMaas}
                <g:if test="${marginalProduct.countThatNeedMaas > 0}">
                    <g:link action="showNeedMaas" params="[projectUid:marginalProduct.projectUid, datasetId:dataset.id]" >
                        (click to list)
                    </g:link>
                </g:if>
            </td>

            <td>
                ${marginalProduct.countThatNeedRta}
                <g:if test="${marginalProduct.countThatNeedRta > 0}">
                    <g:link action="showNeedRta" params="[projectUid:marginalProduct.projectUid, datasetId: dataset.id]">
                        (click to list)
                    </g:link>
                </g:if>
            </td>

            <td>
                ${marginalProduct.countWitheResultMapProblem}
                <g:if test="${marginalProduct.countWitheResultMapProblem > 0}">
                    <g:link action="showResultMapProblems" params="[projectUid:marginalProduct.projectUid]">
                        (click to list)
                    </g:link>
                </g:if>
            </td>

            <td>${marginalProduct.countOnHold}</td>
        </tr>
    </g:each>
</table>

</body>
</html>