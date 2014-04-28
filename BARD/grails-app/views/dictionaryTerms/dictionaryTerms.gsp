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

<%@ page import="bard.db.dictionary.OntologyItem" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Glossary</title>
    <r:require  modules="dictionaryPage"/>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid"><div class="span12">
        <h3>BARD Glossary</h3>
        <p id="msg">&nbsp;</p>
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <th data-sort="int">ID</th>
                <th data-sort="string-ins">Term</th>
                <th data-sort="string-ins">Description</th>
                <th data-sort="string-ins">Units</th>
                <th>External Ontology References</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${capDictionary}" var="descriptor">
                <g:if test="${descriptor.label}">
                    <tr>
                        <td>
                            ${descriptor?.element?.id}
                        </td>
                        <td>
                            <a name="${descriptor?.element?.id}"></a>
                            ${descriptor.label}
                        </td>
                        <td>
                            <small class="text-info">${descriptor.fullPath}</small><br/>
                            ${descriptor.description}
                        </td>
                        <td>
                            ${descriptor?.unit?.abbreviation}
                        </td>
                        <td>
                            <%
                                List<OntologyItem> ontologyItems = descriptor.element.ontologyItems as List<OntologyItem>
                            %>
                            <g:each in="${ontologyItems}" var="ontologyItem">
                                ${ontologyItem.displayValue()}
                            </g:each>
                        </td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
    </div>
    </div>
</div>
</body>
</html>

