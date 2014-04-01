<%@ page import="bard.db.enums.Status" %>
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
                   data-source="${request.contextPath}/project/projectStatus"
                   data-pk="${project.id}"
                   data-url="${request.contextPath}/project/editProjectStatus"
                   data-original-title="Select Project Status">${project?.projectStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status" data-id="${project?.projectStatus?.id}"></a>
             </dd>

            <g:if test="${project?.projectStatus.equals(Status.APPROVED)}">
                <dt><g:message code="entity.approvedBy.label" default="Approved By"/>:</dt>
                <dd id="approvedById">
                    <g:if test="${project.approvedBy?.displayName}">
                        ${project.approvedBy?.displayName} (<g:formatDate date="${project.approvedDate}" format="MM/dd/yyyy"/>)
                    </g:if>
                </dd>
            </g:if>
            <g:if test="${project?.projectStatus.equals(Status.PROVISIONAL)}">
                <dt><g:message code="entity.provisionalApprovedBy.label" default="Provisionally Approved By"/>:</dt>
                <dd id="approvedById">
                    <g:if test="${project.approvedBy?.displayName}">
                        ${project.approvedBy?.displayName} (<g:formatDate date="${project.approvedDate}" format="MM/dd/yyyy"/>)
                    </g:if>
                </dd>
            </g:if>
            <dt><g:message code="project.name.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="projectName"
                   id="nameId"
                   data-toggle="manual"
                   data-inputclass="input-xxlarge"
                   data-type="textarea"

                   data-value="${project?.name}"
                   data-pk="${project.id}"
                   data-url="${request.contextPath}/project/editProjectName"
                   data-placeholder="Required"
                   data-original-title="Edit Project Name">${project?.name}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name" data-id="nameId"></a>
            </dd>

            <dt><g:message code="project.description.label" default="Fix i18n"/>:</dt>
            <dd>
                <span
                   class="description"
                   id="descriptionId"
                   data-inputclass="input-xxlarge"
                   data-type="textarea"
                   data-toggle="manual"
                   data-value="${project.description}"
                   data-pk="${project.id}"
                   data-url="${request.contextPath}/project/editDescription"
                   data-placeholder="Required"
                   data-original-title="Edit Description By">${project?.description}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Description" data-id="descriptionId"></a>
            </dd>
            <dt><g:message code="project.ownerRole.label" default="Owner"/>:</dt>
            <dd>
                <span
                        class="description"
                        data-toggle="manual"
                        data-sourceCache="true"
                        data-placeholder="Required"
                        id="ownerRoleId"
                        data-type="select"
                        data-value="${project?.ownerRole?.displayName}"
                        data-source="${request.contextPath}/assayDefinition/roles"
                        data-pk="${project.id}"
                        data-url="${request.contextPath}/project/editOwnerRole"
                        data-original-title="Select Owner Role">${project?.ownerRole?.displayName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}"  data-id="ownerRoleId" title="Click to edit owner role"></a>
            </dd>
            %{--<dt>Owner:</dt><dd>${projectOwner}</dd>--}%
            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${project.dateCreated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${project.lastUpdated}" format="MM/dd/yyyy"/></dd>
            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:renderModifiedByEnsureNoEmail entity="${project}" /></dd>
        </dl>
    </div>
</div>






