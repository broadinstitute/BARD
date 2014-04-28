%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/2/13
  Time: 9:35 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Datasets</title>
</head>
<body>
<h1>Datasets</h1>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Load Order</th>
        <th>Marginal Product</th>
        <th>Project UID's</th>
        <th>AID's</th>
    </tr>
    <g:each in="${datasetList}" var="dataset">
        <tr>
            <td>${dataset.id}</td>
            <td>${dataset.name}</td>
            <td>${dataset.description}</td>
            <td>${dataset.loadOrder}</td>
            <td><g:link controller="marginalProduct" action="show" params="[datasetId: dataset.id]">show marginal product</g:link> </td>
            <td><g:link controller="datasetProjectUid" action="index" params="[datasetId: dataset.id]">list project UID</g:link> </td>
            <td><g:link controller="datasetAid" action="index" params="[datasetId: dataset.id]">list AID</g:link></td>
        </tr>
    </g:each>
</table>
</body>
</html>
