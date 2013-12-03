<%@ page import="bard.db.bard.db.people.Role" %>



<div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'authority', 'error')} required">
    <label for="authority">
        <g:message code="role.authority.label" default="Authority"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textArea name="authority" cols="40" rows="5" maxlength="255" required="" value="${roleInstance?.authority}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'modifiedBy', 'error')} ">
    <label for="modifiedBy">
        <g:message code="role.modifiedBy.label" default="Modified By"/>

    </label>
    <g:textField name="modifiedBy" maxlength="40" value="${roleInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'displayName', 'error')} ">
    <label for="displayName">
        <g:message code="role.displayName.label" default="Display Name"/>

    </label>
    <g:textField name="displayName" maxlength="40" value="${roleInstance?.displayName}"/>
</div>

