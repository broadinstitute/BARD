<%@ page import="bard.db.command.BardCommand; bard.db.people.Role; bard.db.enums.AssayType; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,assayshow"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Definition</title>

</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save" controller="assayDefinition">

            <div class="control-group ${hasErrors(bean: assayCommand, field: 'assayName', 'error')}">
                <label class="control-label" for="assayName">
                    <g:message code="assay.assayName.label"/>:</label>

                <div class="controls">
                    <g:textField id="assayName" name="assayName" value="${assayCommand?.assayName}" required=""/>
                    <span class="help-inline"><g:fieldError field="assayName" bean="assay"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: assayCommand, field: 'assayType', 'error')}">
                <label class="control-label" for="assayType">* <g:message code="assay.assayType.label"/>:</label>

                <div class="controls">
                    <g:select name="assayType" id="assayType"
                              from="${AssayType.values()}"
                              value="${assayCommand?.assayType}"
                              optionValue="id"/>

                </div>
            </div>
            <div class="control-group ${hasErrors(bean: assayCommand, field: 'ownerRole', 'error')}">
                <label class="control-label" for="ownerRole">* <g:message code="entity.ownerRole.label"/>:</label>

                <div class="controls">
                    <g:if test="${bard.db.command.BardCommand.userRoles()}">
                        <g:select name="ownerRole" id="ownerRole"  required="required"
                                  from="${BardCommand.userRoles()}"
                                  value="${assayCommand?.ownerRole}"
                                  optionValue="displayName" optionKey="id"/>
                    </g:if>
                    <g:else>
                        <p> You need to be part of a team to create Assays. Follow this <g:link controller="assayDefinition" action="teams">link</g:link> to the Teams Page</p>
                    </g:else>


                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <g:link controller="assayDefinition" action="groupAssays"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create New Assay Definition">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>