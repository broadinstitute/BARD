<g:hiddenField name="version" id="versionId" value="${project.version}"/>
<div class="row-fluid">
    <div class="span9">
        <dl class="dl-horizontal">
            <dt><g:message code="project.id.label" default="PID"/>:</dt>
            <dd>${project.id}</dd>


            <dt><g:message code="project.projectStatus.label" default="Status"/>:</dt>
            <dd>
                <span
                   data-sourceCache="true"
                   class="status ${project?.projectStatus?.id}"
                   data-toggle="manual"
                   id="${project?.projectStatus?.id}"
                   data-type="select"
                   data-value="${project?.projectStatus?.id}"
                   data-source="/BARD/project/projectStatus"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editProjectStatus"
                   data-original-title="Select Project Status">${project?.projectStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status" data-id="${project?.projectStatus?.id}"></a>
             </dd>

            <dt><g:message code="project.name.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="projectName"
                   id="nameId"
                   data-toggle="manual"
                   data-type="text"
                   data-value="${project?.name}"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editProjectName"
                   data-placeholder="Required"
                   data-original-title="Edit Project Name">${project?.name}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name" data-id="nameId"></a>
            </dd>

            <dt><g:message code="project.description.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="description"
                   id="descriptionId"
                   data-type="text"
                   data-toggle="manual"
                   data-type="text"
                   data-value="${project.description}"
                   data-pk="${project.id}"
                   data-url="/BARD/project/editDescription"
                   data-placeholder="Required"
                   data-original-title="Edit Description By">${project?.description}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Description" data-id="descriptionId"></a>
            </dd>

            <dt>Owner:</dt><dd>${projectOwner}</dd>
            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${project.dateCreated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${project.lastUpdated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:fieldValue bean="${project}" field="modifiedBy"/></dd>
        </dl>
    </div>
</div>






