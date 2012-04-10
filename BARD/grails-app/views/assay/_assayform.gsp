<%@ page import="bard.db.model.*" %>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'id', 'error')} ">
	<label for="assayName">
		<g:message code="assay.id.label" default="Assay ID" />		
	</label>
	<g:textField name="assayName" maxlength="128" value="${assayInstance?.id}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayStatus', 'error')} ">
	<label for="assayName">
		<g:message code="assay.assayStatus.label" default="Status" />		
	</label>
	<g:textField name="assayName" maxlength="128" value="${assayInstance?.assayStatus?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'lastUpdated', 'error')} ">
	<label for="assayName">
		<g:message code="assay.lastUpdated.label" default="Last Updated" />		
	</label>
	<g:textField name="lastUpdated" maxlength="128" value="${assayInstance?.lastUpdated}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayName', 'error')} ">
	<label for="assayName">
		<g:message code="assay.assayName.label" default="Assay Name" />
		
	</label>
	<g:textField name="assayName" maxlength="128" value="${assayInstance?.assayName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayVersion', 'error')} ">
	<label for="assayVersion">
		<g:message code="assay.assayVersion.label" default="Assay Version" />
		
	</label>
	<g:textField name="assayVersion" maxlength="10" value="${assayInstance?.assayVersion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'designedBy', 'error')} ">
	<label for="designedBy">
		<g:message code="assay.designedBy.label" default="Designed By" />
		
	</label>
	<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="assay.description.label" default="Description" />		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${assayInstance?.description}"/>
</div>
<div>
	<g:actionSubmit value="Add Protocol" />
	<g:actionSubmit value="Del Protocol" />
	
</div>
<div>
	<g:actionSubmit value="Add Project" />
	<g:actionSubmit value="Del Project" />
	
</div>



