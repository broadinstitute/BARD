<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,projectsummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>Panel</title>
</head>

<body>
<g:hiddenField name="id" id="panelId" value="${panelInstance?.id}"/>

<g:if test="${message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <g:if test="${panelInstance?.id}">
                    <h4>View Panel (Panel ID: ${panelInstance?.id})</h4>
                </g:if>
            </div>
        </div>
    </div>
</div>
<g:if test="${panelInstance?.id}">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3">

            </div>

            <div class="span9">
                <h3>Overview</h3>

                <div class="row-fluid">
                    <div id="msg" class="alert hide"></div>

                    <div id="showSummary">
                        <g:hiddenField name="version" id="versionId" value="${panelInstance.version}"/>
                        <div class="row-fluid">
                            <div class="span9">
                                <dl class="dl-horizontal">
                                    <dt><g:message code="panel.id.label" default="PLID"/>:</dt>
                                    <dd>${panelInstance?.id}</dd>

                                    <dt><g:message code="panel.label" default="Name"/>:</dt>
                                    <dd>
                                        <span
                                                class="panelNameY"
                                                id="nameId"
                                                data-toggle="manual"
                                                data-type="text"
                                                data-value="${panelInstance?.name}"
                                                data-pk="${panelInstance.id}"
                                                data-url="/BARD/panel/editPanelName"
                                                data-placeholder="Required"
                                                data-original-title="Edit Panel Name">${panelInstance?.name}</span>
                                        <a href="#" class="icon-pencil documentPencil ${editable}"
                                           title="Click to edit Name"
                                           data-id="nameId"></a>
                                    </dd>
                                    <dt><g:message code="panel.description.label" default="Description"/>:</dt>
                                    <dd>
                                        <span class="description"
                                                id="descriptionId"
                                                data-type="text"
                                                data-toggle="manual"
                                                data-type="text"
                                                data-value="${panelInstance?.description}"
                                                data-pk="${panelInstance.id}"
                                                data-url="/BARD/panel/editDescription"
                                                data-placeholder="Required"
                                                data-inputclass="input-xxlarge"
                                                data-original-title="Edit Panel Description">${panelInstance?.description}</span>
                                        <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Description" data-id="descriptionId"></a>
                                    </dd>
                                    <g:hiddenField name="version" id="versionId" value="${panelInstance.version}"/>
                                    <dt><g:message code="panel.ownerRole.label" default="Owner"/>:</dt>
                                    <dd>
                                        <span
                                                class="description"
                                                data-toggle="manual"
                                                data-sourceCache="true"
                                                data-placeholder="Required"
                                                id="ownerRoleId"
                                                data-type="select"
                                                data-value="${panelInstance?.ownerRole?.displayName}"
                                                data-source="/BARD/assayDefinition/roles"
                                                data-pk="${panelInstance.id}"
                                                data-url="/BARD/panel/editOwnerRole"
                                                data-original-title="Select Owner Role">${panelInstance?.ownerRole?.displayName}</span>
                                        <a href="#" class="icon-pencil documentPencil ${editable}"  data-id="ownerRoleId" title="Click to edit owner role"></a>
                                    </dd>
                                    %{--<dt>Owner:</dt>--}%
                                    %{--<dd>${panelOwner}</dd>--}%

                                    <dt><g:message code="default.dateCreated.label"/>:</dt>
                                    <dd><g:formatDate date="${panelInstance.dateCreated}" format="MM/dd/yyyy"/></dd>

                                    <dt><g:message code="default.lastUpdated.label"/>:</dt>
                                    <dd id="lastUpdatedId"><g:formatDate date="${panelInstance.lastUpdated}"
                                                                         format="MM/dd/yyyy"/></dd>

                                    <dt><g:message code="default.modifiedBy.label"/>:</dt>
                                    <dd id="modifiedById"><g:fieldValue bean="${panelInstance}"
                                                                        field="modifiedBy"/></dd>
                                </dl>
                            </div>
                        </div>

                        <div>
                            <g:if test="${panelInstance.panelAssays}">
                                <h3>Associated Assay Definitions</h3>
                                <table class="table table-striped table-hover table-bordered">
                                   <thead>
                                   <tr>
                                       <th>ADID</th><th>Assay Name</th><th></th></thead>
                                   </tr>
                                   </thead>
                                <tbody>
                                <g:each in="${panelInstance.panelAssays}" var="panelAssay">
                                    <tr>
                                        <td><g:link controller="assayDefinition" action="show"
                                                    id="${panelAssay.assay.id}">${panelAssay.assay.id}</g:link></td><td>${panelAssay.assay.assayName}</td>
                                        <td>
                                            <g:if test="${editable == 'canedit'}">
                                                <g:link controller="panel" action="removeAssay"
                                                        params="${[id: panelInstance.id, assayIds: panelAssay.assay.id]}"
                                                        class="btn btn-mini" title="Remove From Panel"
                                                        onclick="return confirm('Are you sure you wish to remove this Assay Definition from this Panel?');"><i
                                                        class="icon-trash"></i>Remove From Panel</g:link>
                                            </g:if>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                            </g:if>
                            <g:if test="${editable == 'canedit'}">
                                <g:link controller="panel" action="addAssays" params="${[id: panelInstance.id]}"
                                        class="btn"><i class="icon-plus"></i>Add Assay Definitions To This Panel</g:link>

                                <g:link controller="panel" action="deletePanel" params="${[id: panelInstance.id]}"
                                        class="btn" title="Delete Panel"
                                        onclick="return confirm('Are you sure you wish to delete this Panel?');"><i
                                        class="icon-trash"></i>Delete Panel</g:link>
                            </g:if>
                            <g:link controller="panel" action="create"
                                    class="btn"><i class="icon-plus"></i>Create a New Panel</g:link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>