<%@ page import="bard.db.model.AssayStatus" %>



<div class="fieldcontain ${hasErrors(bean: assayStatusInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="assayStatus.status.label" default="Status" />
		
	</label>
	<g:textField name="status" maxlength="20" value="${assayStatusInstance?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayStatusInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="assayStatus.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${assayStatusInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayStatusInstance, field: 'assays', 'error')} ">
	<label for="assays">
		<g:message code="assayStatus.assays.label" default="Assays" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayStatusInstance?.assays?}" var="a">
    <li><g:link controller="assay" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="assay" action="create" params="['assayStatus.id': assayStatusInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'assay.label', default: 'Assay')])}</g:link>
</li>
</ul>

</div>

