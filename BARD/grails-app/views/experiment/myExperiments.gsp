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

<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Experiments</title>

</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <h3>My Experiments</h3>
    </div>

    <div class="row-fluid">
        <g:if test="${experiments}">
            <g:render template="/layouts/templates/tableSorterTip"/>


            <div class="span1">

            </div>

            <div class="span10">
                <g:render template="/layouts/templates/tableSorterTip"/>
                <table>
                    <caption><b>Total:</b> ${experiments.size()}</caption>
                    <thead>
                    <tr>
                        <th data-sort="int">Experiment ID</th><th data-sort="string-ins">Name</th><th
                            data-sort="string">Status</th>
                        <th data-sort="int">Belongs to ADID</th>
                        <th data-sort="int">Date Created</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${experiments}" var="experiment">
                        <tr>
                            <td><g:link controller="experiment" id="${experiment.id}"
                                        action="show">${experiment.id}</g:link></td>
                            <td style="line-height: 150%"><p>${experiment.experimentName?.trim()}</p></td>
                            <td style="line-height: 150%"><p>${experiment.experimentStatus.id}</p></td>
                            <td style="line-height: 150%"><p>
                                <g:link controller="assayDefinition" id="${experiment.assay.id}"
                                        action="show">${experiment.assay.id}</g:link>
                            </p></td>
                            <td data-sort-value="${experiment.dateCreated?.time}"><g:formatDate
                                    date="${experiment.dateCreated}" format="MM/dd/yyyy"/></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </g:if>
        <br/>
    </div>
</div>
</body>
</html>
