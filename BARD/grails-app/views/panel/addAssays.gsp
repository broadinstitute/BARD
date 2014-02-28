<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Add Assay(s) To Panel</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h3>Add Assay(s) To Panel</h3>
    </div>
    <g:if test="${draftAssays || retiredAssays}">
        <div class="row-fluid">
            <div class="span12">
                <div class="ui-widget">
                    <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                        <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>

                        <p><strong>Only approved assay definitions can be added to a panel.</strong></p>
                        <g:if test="${draftAssays}">
                            The following assay definitions are still draft:
                            <g:each in="${draftAssays}" var="draftAssay">
                                ${draftAssay.id}
                            </g:each>
                            <br/><br/>
                        </g:if>
                        <g:if test="${retiredAssays}">
                            The following assay definitions are retired:
                            <g:each in="${retiredAssays}" var="retiredAssay">
                                ${retiredAssay.id}
                            </g:each>
                        </g:if>

                    </div>
                </div>
            </div>
        </div>
    </g:if>
    <br/>
    <br/>

    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="addAssays" controller="panel">
                <g:hiddenField name="id" value="${associatePanelCommand?.id}"/>
                <div class="control-group ${hasErrors(bean: associatePanelCommand, field: 'assayIds', 'error')}">
                    <label class="control-label" for="assayIds">
                        <g:message code="panel.addAssay.label"/>:</label>

                    <div class="controls">
                        <g:textArea id="assayIds" name="assayIds" value="${associatePanelCommand?.assayIds}"
                                    required=""/>
                        <span class="help-inline"><g:fieldError field="assayIds" bean="associatePanelCommand"/></span>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="panel" action="myPanels"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Add To Panel">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>

</body>
</html>