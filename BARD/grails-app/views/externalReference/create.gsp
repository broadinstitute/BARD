<%@ page import="bard.db.registration.ExternalSystem; bard.db.registration.ExternalReference" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix,card,histogram"/>
    <meta name="layout" content="basic"/>
    <title>Create an External Reference</title>
</head>

<body>
<g:hasErrors>
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <g:renderErrors bean="${externalReferenceInstance}"/>
                </div>
            </div>
        </div>
    </div>
</g:hasErrors>

<div class="row-fluid">
    <div class="span12">
        <h3>Create a New External Reference</h3>

        <g:form class="form-horizontal" controller="externalReference" action="save">
            <g:hiddenField name="project.id" value="${externalReferenceInstance.project?.id ?: ''}"/>
            <g:hiddenField name="experiment.id" value="${externalReferenceInstance.experiment?.id ?: ''}"/>

            <g:if test="${externalReferenceInstance.project}">
                <div class="control-group">
                    <label class="control-label" for="projectName">Project Name:</label>

                    <div class="controls">
                        <g:textField id="projectName" name="project.name" disabled="disabled" size="100"
                                     style="width: auto;" value="${externalReferenceInstance.project.name}"/>
                    </div>
                </div>
            </g:if>

            <g:if test="${externalReferenceInstance.experiment}">
                <div class="control-group">
                    <label class="control-label" for="experimentName">Experiment Name:</label>

                    <div class="controls">
                        <g:textField id="experimentName" name="experiment.name" disabled="disabled" size="100"
                                     style="width: auto;"
                                     value="${externalReferenceInstance.experiment.experimentName}"/>
                    </div>
                </div>
            </g:if>

            <div class="control-group">
                <label class="control-label" for="externalSystem.id">External System:</label>

                <div class="controls">
                    <g:select name="externalSystem.id" from="${ExternalSystem.list()}" optionKey="id"
                              optionValue="systemName"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="extAssayRef">External Assay Reference:</label>

                <div class="controls">
                    <g:textField id="extAssayRef" name="extAssayRef" value="${externalReferenceInstance.extAssayRef}"/>

                    <small>(please note: links to PubChem AID are formated as 'aid=xxxx' where xxxx is the AID number; links to Common Assay Reporting System (CARS) are formated as 'project_UID=xxxx')</small></label>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="${externalReferenceInstance.project ? 'project' : 'experiment'}" action="show"
                            id="${externalReferenceInstance.project?.id ?: externalReferenceInstance.experiment.id}"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create">
                </div>
            </div>
        </g:form>
    </div>
</div>

</body>
</html>