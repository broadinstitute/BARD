<div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'primaryGroup', 'error')} required">
    <label class="control-label" for="primaryGroup"><g:message
            code="register.primarygroup.label"
            default="Primary Group"/>:</label>

    <div class="controls">
        <g:select required="" id="primaryGroup" name="primaryGroup.id"
                  from="${Role.teamRoles}"
                  optionKey="id"
                  optionValue="displayName" noSelection="${['': '']}"
                  value="${command?.primaryGroup?.id}"/>

        <span class="help-inline">
            <g:hasErrors bean="${command}" field="primaryGroup">
                <div class="alert alert-block alert-error fade in">
                    <g:eachError bean="${command}" field="primaryGroup">
                        <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                error="${it}"/></p>
                    </g:eachError>
                </div>
            </g:hasErrors>
        </span>
    </div>
</div>

<div class="control-group">
    <div class="controls">
        <g:link class="btn" action="listUsersAndGroups">Cancel</g:link>
        <input type="submit" class="btn btn-primary">
    </div>
</div>
