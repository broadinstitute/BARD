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

<%@ page import="bard.db.enums.Status; bard.db.enums.Status; bard.db.registration.ExternalReference" %>
<div class="container">
    <div>
        <g:set var="experimentNum" value="${moveExperimentsCommand.experiments?.size()}"/>
        <h2>${experimentNum} Experiment${experimentNum && experimentNum > 1 ? "s found - choose which ones to move" : " found"}</h2>

        <g:if test="${warningMessage}">
            <h4>${warningMessage}</h4>
        </g:if>
    </div>

    <div class="row">
        <div class="span12">
            <g:formRemote id="selectExperimentsToMoveForm"
                          url="[controller: 'moveExperiments', action: 'moveSelectedExperiments']"
                          name="splitExperiments"
                          update="[success: 'displayResponseDiv', failure: 'displayResponseDiv']"
                          onSuccess="disableFormAndControls(data)">
                <table id="myExperiments" class="table table-striped table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>%{--Select all check boxes by default otherwise toggle--}%
                        <g:checkBox name="selectAll" id="selectAll" class="selectAll" checked="true"/> select all <br/>
                        </th>
                        <th>Experiment ID</th>
                        <th>ADID</th>
                        <th>PubChem AID</th>
                        <th>Experiment Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each var="experiment" in="${moveExperimentsCommand.experiments}">
                        <tr>
                            <td>
                                <g:checkBox name="experimentIds" value="${experiment.id}" class="check"
                                            onclick="resetSelectAll();"/>
                            </td>
                            <td>
                                <g:link controller="experiment"
                                        action="show" id="${experiment.id}">${experiment.id}</g:link>
                            </td>
                            <td>
                                <g:link controller="assayDefinition"
                                        action="show" id="${experiment.assay.id}">${experiment.assay.id}</g:link>
                            </td>
                            <td>
                                <%
                                    ExternalReference externalReference = bard.db.registration.ExternalReference.findByExperiment(experiment)

                                    String aid = externalReference?.getAid() ?: ""
                                %>
                                ${aid}
                            </td>
                            <td>${experiment.experimentName}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
                <g:hiddenField name="targetAssay.id" value="${moveExperimentsCommand.targetAssay.id}"/>
                <br/>
                <br/>

                <div>
                    <h2>Assay Definition to move to:</h2>
                    <ul>
                        <g:link controller="assayDefinition" action="show" id="${moveExperimentsCommand.targetAssay.id}"
                                target="_blank">
                            <li>
                                ${moveExperimentsCommand.targetAssay.id} - ${moveExperimentsCommand.targetAssay.assayName}
                                <g:if test="${moveExperimentsCommand.targetAssay.assayStatus == Status.RETIRED}">
                                    <span class="alert-error">
                                        - ${moveExperimentsCommand.targetAssay.assayStatus}
                                    </span>
                                </g:if>
                            </li>
                        </g:link>
                    </ul>
                </div>

                <br/>
                <br/>
                <input type="submit" class="btn btn-primary" name="Move Experiments" value="Move Experiments">
            </g:formRemote>
            <div id="displayResponseDiv">

            </div>
        </div>

    </div>
    <script>
        $('.selectAll').on('click', function () {
            $('.check').prop('checked', isChecked('selectAll'));
        });
    </script>
</div>

