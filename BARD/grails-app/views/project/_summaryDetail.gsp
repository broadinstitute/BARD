<div id="summaryDetailSection">
    <dl class="dl-horizontal">
        <dt><g:message code="default.id.label" default="Fix i18n"/></dt>
        <dd>${project.id}</dd>

        <dt><g:message code="project.name.label" default="Fix i18n"/></dt>
        <dd><g:fieldValue bean="${project}" field="name"/></dd>

        <dt><g:message code="project.description.label" default="Fix i18n"/></dt>
        <dd><g:fieldValue bean="${project}" field="description"/></dd>

        <dt><g:message code="default.dateCreated.label" default="Fix i18n"/></dt>
        <dd><g:formatDate date="${project.dateCreated}" format="yyyy-MM-dd"/>&nbsp;</dd>

        <dt><g:message code="default.lastUpdated.label" default="Fix i18n"/></dt>
        <dd><g:formatDate date="${project.lastUpdated}" format="yyyy-MM-dd"/>&nbsp;</dd>
    </dl>
</div>