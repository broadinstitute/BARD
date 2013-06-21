<g:hiddenField name="version" id="versionId" value="${assay.version}"/>
<div class="row-fluid">
    <div class="span9">
        <dl class="dl-horizontal">
            <dt><g:message code="assay.id.label" default="ADID"/>:</dt>
            <dd>${assay?.id}</dd>
            <dt><g:message code="assay.assayStatus.label" default="Fix i18n"/>:</dt>
            <dd>
                ${assay?.assayStatus?.id}
             </dd>
            <dt><g:message code="assay.assayName.label" default="Fix i18n"/>:</dt>
            <dd>
                ${assay?.assayName}
             </dd>
            <dt><g:message code="assay.assayShortName.label" default="Fix i18n"/>:</dt>
            <dd id="shortNameId"><g:fieldValue bean="${assay}" field="assayShortName"/></dd>
            <dt><g:message code="assay.designedBy.label" default="Fix i18n"/>:</dt>
            <dd>
                ${assay?.designedBy}
             </dd>
            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${assay.dateCreated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="assay.assayType.label" default="Fix i18n"/>:</dt>
            <dd>
                ${assay?.assayType?.id}
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