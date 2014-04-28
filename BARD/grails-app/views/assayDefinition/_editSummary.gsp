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
<g:hiddenField name="version" id="versionId" value="${assay.version}"/>
<div class="row-fluid">
    <div class="span9">
        <dl class="dl-horizontal">
            <dt><g:message code="assay.id.label" default="ADID"/>:</dt>
            <dd>${assay?.id}</dd>


            <dt><g:message code="assay.assayStatus.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   data-sourceCache="true"
                   class="status ${assay?.assayStatus?.id}"
                   data-toggle="manual"
                   id="${assay?.assayStatus?.id}"
                   data-type="select"
                   data-value="${assay?.assayStatus?.id}"
                   data-source="${request.contextPath}/assayDefinition/assayStatus"
                   data-pk="${assay.id}"
                   data-url="${request.contextPath}/assayDefinition/editAssayStatus"
                   data-original-title="Select Assay Status">${assay?.assayStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status" data-id="${assay?.assayStatus?.id}"></a>
                <g:render template="/common/statusIcons" model="[status:assay?.assayStatus?.id, entity: 'Assay']"/>

            </dd>
             <g:render template="/common/statusMessage" model="[status:assay?.assayStatus,displayName:assay.approvedBy?.displayName,approvedDate:assay.approvedDate]"/>

            <dt><g:message code="assay.assayName.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="assayNameY"
                   id="nameId"
                   data-toggle="manual"
                   data-inputclass="input-xxlarge"
                   data-type="textarea"
                   data-value="${assay?.assayName}"
                   data-pk="${assay.id}"
                   data-url="${request.contextPath}/assayDefinition/editAssayName"
                   data-placeholder="Required"
                   data-original-title="Edit Assay Name">${assay?.assayName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name" data-id="nameId"></a>
            </dd>

            <dt><g:message code="assay.ownerRole.label" default="Owner"/>:</dt>
            <dd>
                <span
                        class="type"
                        data-toggle="manual"
                        data-sourceCache="false"
                        id="ownerRoleId"
                        data-type="select"
                        data-value="${assay?.ownerRole?.displayName}"
                        data-source="${request.contextPath}/assayDefinition/roles"
                        data-pk="${assay.id}"
                        data-url="${request.contextPath}/assayDefinition/editOwnerRole"
                        data-original-title="Select Owner Role">${assay?.ownerRole?.displayName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}"  data-id="ownerRoleId" title="Click to edit owner role"></a>
            </dd>


            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${assay.dateCreated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="assay.assayType.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="type"
                   data-toggle="manual"
                   data-sourceCache="true"
                   id="assayTypeId"
                   data-type="select"
                   data-value="${assay?.assayType?.id}"
                   data-source="${request.contextPath}/assayDefinition/assayTypes"
                   data-pk="${assay.id}"
                   data-url="${request.contextPath}/assayDefinition/editAssayType"
                   data-original-title="Select Assay Type">${assay?.assayType?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}"  data-id="assayTypeId" title="Click to edit type"></a>
            </dd>

           <dt><g:message code="assay.assayVersion.label" default="Fix i18n"/>:</dt>
            <dd><g:fieldValue bean="${assay}" field="assayVersion"/></dd>

            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${assay.lastUpdated}" format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:renderModifiedByEnsureNoEmail entity="${assay}" /></dd>
        </dl>
    </div>
</div>
