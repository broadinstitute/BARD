<%@ page import="bard.db.registration.IdType; bard.db.project.EntityType" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,addExperimentsToProject"/>
    <meta name="layout" content="basic"/>
    <title>PID ${command?.project?.id}: ${command?.project?.name}</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2"></div>

        <div class="span10"><h4>Add Experiments or Panel-Experiments To Project ${command?.project?.id} - ${command?.project?.name}</h4>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span2"></div>

        <div class="span10">
            <g:form action="showExperimentsToAddProject" controller="project">

                <g:hasErrors bean="${command}">
                    <div class="alert alert-error">
                        <button type="button" class="close" data-dismiss="alert">×</button>
                        <g:renderErrors bean="${command}"/>
                    </div>
                </g:hasErrors>
                <g:if test="${command.errorMessages}">
                    <div class="alert alert-error">
                        <button type="button" class="close" data-dismiss="alert">×</button>

                        <ul>
                            <g:each in="${command.errorMessages}" var="errorMessage">
                                <li>${errorMessage}</li>
                            </g:each>
                        </ul>
                    </div>
                </g:if>

                <input type="hidden" name="fromAddPage" value="true"/>
                <g:hiddenField name="projectId" id="projectId" value="${command?.project?.id}"/>

            %{--make this field read-only if a type has already been selected--}%
                <h5><div>Choose Experiment or Panel-Experiment</div></h5>
                <g:select name="entityType" id="entityType" required=""
                          from="${EntityType.values()}"
                          value="${command.entityType}"
                          optionValue="id"/>

                <h5><div>Select Type of ID to Search By</div></h5>
                <g:select name="idType" id="idType" required=""
                          from="${IdType.values()}"
                          value="${command.idType}"
                          optionValue="name"/>

                <h5><div>Paste Ids to search by (space delimited).<br/>
                    If you chose the AssayDefinition ID, Panel ID, Panel-Experiment ID (or for some PubChem AIDs) above, you would be prompted to select from a list of matching experiments or panel-experiments.
                </div></h5>
                <g:textArea class="input-xxlarge" id="sourceEntityIds" name="sourceEntityIds"
                            value="${command.sourceEntityIds}" required=""/>


                <h5><div>Experiment Stage</div></h5>
                <g:hiddenField id="stageId" name="stageId" class="span10" style="margin-left: 0;"
                               value="${command?.stage?.id}"/>
                <br/>
                <br/>
                <h5><div>Selected Stage's Description</div></h5>
                <g:textArea id="stageDescription" name="stageDescription" class="span10"
                            value="${command.stage?.description}" disabled="disabled"/>

                <div id="selectExperimentsId">
                    <g:render template="selectExperimentsToAddToProjects" model="[command: command]"/>
                </div>

                <div id="selectPanelExperimentsId">
                    <g:render template="selectPanelExperimentsToAddToProjects" model="[command: command]"/>
                </div>

                <g:link controller="project" action="show" fragment="experiment-and-step-header"
                        id="${command.projectId}"
                        class="btn">Cancel</g:link>
                <input type="submit" class="btn btn-primary" name="addExperimentsOrPanelExperimentBtn" value="Add Experiments or Panel-Experiments"/>
            </g:form>
        </div>
    </div>
</div>
</body>
</html>