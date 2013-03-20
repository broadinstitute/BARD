<div id="dialog_edit_project_summary" title="Edit Summary">
    <form id="editSummaryForm">
        <dl class="dl-horizontal">
            <dt><g:message code="default.id.label" default="Fix i18n"/></dt>
            <dd id="projectId">${project.id}</dd>

            <dt><g:message code="project.name.label" default="Fix i18n"/></dt>
            <dd><g:textField id="projectName" name="name" class="projectName" required="required" value="${project?.name}"/><span id="projectNameValidation" style="color:red" ></span></dd>

            <dt><g:message code="project.description.label" default="Fix i18n"/></dt>
            <dd><g:textField id="description" name="description" class="description" required="required" value="${project?.description}"/></dd>

            <dt><g:message code="project.projectStatus.label" default="Fix i18n"/></dt>
            <dd><g:select id="projectStatus" name="projectStatus"
                          from="${bard.db.enums.ProjectStatus.values()}"
                          value="${project.projectStatus}" /></dd>

            <dt><g:message code="default.dateCreated.label" default="Fix i18n"/></dt>
            <dd><g:formatDate date="${project.dateCreated}" format="yyyy-MM-dd"/>&nbsp;</dd>

            <dt><g:message code="default.lastUpdated.label" default="Fix i18n"/></dt>
            <dd><g:formatDate date="${project.lastUpdated}" format="yyyy-MM-dd"/>&nbsp;</dd>
        </dl>
    </form>
</div>






