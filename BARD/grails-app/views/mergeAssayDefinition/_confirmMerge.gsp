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

<%@ page import="bard.db.enums.Status" %>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">

            <g:formRemote url="[controller: 'mergeAssayDefinition', action: 'mergeAssays']"
                          name="mergeAssays"
                          update="[success: 'confirmResponse', failure: 'confirmResponse']">

                <div class="control-group">
                    <label><h3>Confirmation: You want to merge the following ${mergeAssayCommand.sourceAssays.size()} assay(s)</h3>
                    </label>
                </div>

                <div class="control-group">
                    <ol>

                        <g:each var="sourceAssay" in="${mergeAssayCommand.sourceAssays}" status="counter">
                            <g:hiddenField name="sourceAssayIds[${counter}]" value="${sourceAssay.id}"/>
                            <li>
                                <g:link controller="assayDefinition" action="show" id="${sourceAssay.id}"
                                        target="_blank">
                                    ${sourceAssay.id} - ${sourceAssay.assayName}
                                    <g:if test="${sourceAssay.assayStatus == Status.RETIRED}">
                                        <span class="alert-error">
                                            - ${sourceAssay.assayStatus}
                                        </span>
                                    </g:if>
                                </g:link>
                            </li>
                        </g:each>
                    </ol>
                </div>

                <div class="control-group">
                    <label><h3>Into</h3></label>
                </div>

                <div class="control-group">
                    <label>
                        <g:hiddenField name="targetAssayId" value="${mergeAssayCommand.targetAssay?.id}"/>
                        <g:link controller="assayDefinition" action="show" id="${mergeAssayCommand.targetAssay.id}"
                                target="_blank">
                            ${mergeAssayCommand.targetAssay.id} - ${mergeAssayCommand.targetAssay.assayName}
                            <g:if test="${mergeAssayCommand.targetAssay.assayStatus == Status.RETIRED}">
                                <span class="alert-error">
                                    - ${mergeAssayCommand.targetAssay.assayStatus}
                                </span>
                            </g:if>
                        </g:link>
                    </label>
                </div>
                <g:if test="${mergeAssayCommand.errorMessages}">
                    <div class="control-group">
                        <div class="alert-error">
                            <ul>
                                <g:each in="${mergeAssayCommand.errorMessages}" var="errorMessage">
                                    <li>${errorMessage}</li>
                                </g:each>
                            </ul>
                        </div>
                    </div>
                </g:if>
                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary" name="Merge" value="Merge">
                    </div>
                </div>

                <div id="confirmResponse"></div>
                <br/>

            </g:formRemote>

        </div>

        <div class="span2">

        </div>
    </div>
</div>
