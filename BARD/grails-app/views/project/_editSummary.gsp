<g:hiddenField name="version" id="versionId" value="${project.version}"/>
<div class="row-fluid">
    <div class="span9">
        <dl class="dl-horizontal">
            <dt><g:message code="project.id.label" default="PID"/>:</dt>
            <dd>${project.id}</dd>


            <dt><g:message code="project.projectStatus.label" default="Status"/>:</dt>
            <dd>
                <a href="#"
                   data-sourceCache="true"
                   class="status"
                   id="${project?.projectStatus?.id}"
                   data-type="select"
                   data-value="${project?.projectStatus?.id}"
                   data-source="/BARD/project/projectStatus"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editProjectStatus"
                   data-original-title="Select Project Status">${project?.projectStatus?.id}</a> <i
                    class="icon-pencil"></i>
             </dd>

            <dt><g:message code="project.name.label" default="Fix i18n"/>:</dt>
            <dd>
                <a href="#"
                   class="projectName"
                   id="nameId"
                   data-type="text"
                   data-value="${project?.name}"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editProjectName"
                   data-placeholder="Required"
                   data-original-title="Edit Project Name">${project?.name}</a> <i class="icon-pencil"></i>
            </dd>

            <dt><g:message code="project.description.label" default="Fix i18n"/>:</dt>
            <dd>
                <a href="#"
                   class="description"
                   id="descriptionId"
                   data-type="text"
                   data-value="${project.description}"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editDescription"
                   data-placeholder="Required"
                   data-original-title="Edit Description By">${project.description}</a> <i class="icon-pencil"></i>
            </dd>

            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${project.dateCreated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${project.lastUpdated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:fieldValue bean="${project}" field="modifiedBy"/></dd>
        </dl>
    </div>
</div>






