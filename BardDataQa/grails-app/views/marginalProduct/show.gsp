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
        <th>Ready AID's</th>
        <th>Difference</th>
        <th>Marginal Product</th>
        <th># Need MAAS</th>
        <th># Need RTA</th>
        <th># on hold</th>
    </tr>
    <g:each in="${marginalProductList}" var="marginalProduct">
        <tr>
            <td>${marginalProduct.projectUid}</td>
            <td>${marginalProduct.totalAids}</td>
            <td>${marginalProduct.readyAids}</td>
            <td>${marginalProduct.difference}</td>
            <td>${marginalProduct.marginalProduct}</td>
            <td>
                ${marginalProduct.countThatNeedMaas}
                <g:if test="${marginalProduct.countThatNeedMaas > 0}">
                    <g:link action="showMaas" params="[projectUid:marginalProduct.projectUid]" >
                        (click to list AID's)
                    </g:link>
                </g:if>
            </td>
            <td>
                ${marginalProduct.countThatNeedRta}
                <g:if test="${marginalProduct.countThatNeedRta > 0}">
                    <g:link action="showRta" params="[projectUid:marginalProduct.projectUid]">
                        (click to list AID's)
                    </g:link>
                </g:if>
            </td>
            <td>${marginalProduct.countOnHold}</td>
        </tr>
    </g:each>
</table>

</body>
</html>