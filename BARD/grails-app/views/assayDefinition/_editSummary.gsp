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
                   data-source="/BARD/assayDefinition/assayStatus"
                   data-pk="${assay.id}"
                   data-url="/BARD/assayDefinition/editAssayStatus"
                   data-original-title="Select Assay Status">${assay?.assayStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status" data-id="${assay?.assayStatus?.id}"></a>
            </dd>

            <dt><g:message code="assay.assayName.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="assayNameY"
                   id="nameId"
                   data-toggle="manual"
                   data-type="text"
                   data-value="${assay?.assayName}"
                   data-pk="${assay.id}"
                   data-url="/BARD/assayDefinition/editAssayName"
                   data-placeholder="Required"
                   data-original-title="Edit Assay Name">${assay?.assayName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name" data-id="nameId"></a>
            </dd>

            <dt><g:message code="assay.assayShortName.label" default="Fix i18n"/>:</dt>
            <dd id="shortNameId"><g:fieldValue bean="${assay}" field="assayShortName"/></dd>
            <dt><g:message code="assay.ownerRole.label" default="Owner"/>:</dt>
            <dd>
                <span
                        class="type"
                        data-toggle="manual"
                        data-sourceCache="false"
                        id="ownerRoleId"
                        data-type="select"
                        data-value="${assay?.ownerRole?.displayName}"
                        data-source="/BARD/assayDefinition/roles"
                        data-pk="${assay.id}"
                        data-url="/BARD/assayDefinition/editOwnerRole"
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
                   data-source="/BARD/assayDefinition/assayTypes"
                   data-pk="${assay.id}"
                   data-url="/BARD/assayDefinition/editAssayType"
                   data-original-title="Select Assay Type">${assay?.assayType?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}"  data-id="assayTypeId" title="Click to edit type"></a>
            </dd>

           <dt><g:message code="assay.assayVersion.label" default="Fix i18n"/>:</dt>
            <dd><g:fieldValue bean="${assay}" field="assayVersion"/></dd>

            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${assay.lastUpdated}" format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:fieldValue bean="${assay}" field="modifiedBy"/></dd>
        </dl>
    </div>
</div>