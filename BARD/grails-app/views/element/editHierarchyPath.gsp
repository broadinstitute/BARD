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
  Date: 4/22/13
  Time: 11:01 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Edit BARD Ontology</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_CURATOR">
    <h1>Edit BARD Ontology</h1>
    <table>
        <g:each in="${list}" var="elementAndPath">
            <tr>
                <g:form name="elementEditForm" url="[controller: 'element', action: 'updateHierarchyPath']">
                    <td>
                        <g:textField name="newPathString" value="${elementAndPath}" size="${maxPathLength}"/>
                    </td>

                    <g:hiddenField name="elementId" value="${elementAndPath.element.id}"/>
                    <g:each in="${elementAndPath.path}" status="index" var="elementHierarchy">
                        <g:hiddenField name="elementHierarchyIdList[${index}]" value="${elementHierarchy.id}"/>
                    </g:each>
                </g:form>
            </tr>
        </g:each>
    </table>
</sec:ifAnyGranted>
</body>
</html>
