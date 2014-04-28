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

%{-- Extract this to a taglib to simplify the logic --}%
<%@ page import="bard.db.enums.Status; bard.db.enums.Status; org.apache.commons.lang3.BooleanUtils" %>
<dl>
    <dt><g:message code="assay.label" default="Assay Definition"/>:</dt>
    <dd>
        <ul>
            <li>
                <g:link controller="assayDefinition" action="show"
                        id="${experiment?.assay?.id}">${experiment?.assay?.name}
                    <g:render template="/common/statusIcons" model="[status:experiment?.assay?.assayStatus?.id, entity: 'Assay']"/>
            </g:link>

            </li>
        </ul>
    </dd>
    <g:if test="${excludedLinks?.contains('experiment.show') == false}">

        <dt>Experiment Links:</dt>
        <dd>
            <ul>
                <li>
                    <g:link controller="experiment" action="show"
                            id="${experiment?.id}">View Experiment Details
                        <g:render template="/common/statusIcons" model="[status:experiment?.experimentStatus?.id, entity: 'Experiment']"/>
                    </g:link>
                </li>

            </ul>
        </dd>
    </g:if>
    <dt>Projects:</dt>
    <dd>
        <ul>
            <g:each in="${experiment?.projectExperiments}" var="projectExperiment">
                <g:if test="${projectExperiment.project.projectStatus != Status.RETIRED}">
                    <li><g:link controller="project" action="show"
                                id="${projectExperiment.project.id}">${projectExperiment.project.name}
                        <g:render template="/common/statusIcons" model="[status:projectExperiment?.project?.projectStatus?.id, entity: 'Project']"/>
                    </g:link></li>
                </g:if>
            </g:each>
        </ul>
    </dd>
    <dt>External references</dt>
    <dd>
        <ul>
            <g:each in="${experiment?.externalReferences}" var="xRef">
                <li>
                    <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                       target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                    <g:if test="${editable == 'canedit'}">
                        <g:set var="externalReferenceParams"
                               value="${[ownerClass: experiment.class.simpleName, ownerId: experiment.id, xRefId: xRef.id]}"></g:set>
                        <g:form class="no-padding" controller="externalReference" action="delete"
                                params="${externalReferenceParams}" method="POST">
                            <button type="submit" title="Delete" class="btn btn-mini"
                                    onclick="return confirm('Are you sure you wish to delete this external reference?');"><i
                                    class="icon-trash"></i></button>
                        </g:form>
                        <g:link controller="externalReference" action="create" params="${externalReferenceParams}"
                                class="btn btn-mini"><i class="icon-pencil" title="Edit"></i></g:link>
                    </g:if>
                </li>
            </g:each>
        </ul>
    </dd>
</dl>
