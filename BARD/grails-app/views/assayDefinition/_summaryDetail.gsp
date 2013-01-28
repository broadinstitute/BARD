    <div id="summaryDetailSection">
    <dl class="dl-horizontal">
        <dt><g:message code="default.id.label" default="Fix i18n"/>:</dt>
        <dd>${assay.id}</dd>

        <dt><g:message code="assay.assayVersion.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayVersion"/></dd>

        <dt><g:message code="assay.assayShortName.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayShortName"/></dd>

        <dt><g:message code="assay.assayName.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayName"/></dd>

        <dt><g:message code="assay.designedBy.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="designedBy"/></dd>

        <dt><g:message code="assay.assayStatus.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayStatus"/></dd>

        <dt><g:message code="assay.assayType.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayType"/></dd>

        <dt><g:message code="default.dateCreated.label"/>:</dt>
        <dd><g:formatDate date="${assay.dateCreated}" format="yyyy-MM-dd"/></dd>

        <dt><g:message code="default.lastUpdated.label"/>:</dt>
        <dd><g:formatDate date="${assay.lastUpdated}" format="yyyy-MM-dd"/>&nbsp;</dd>

    </dl>
    </div>