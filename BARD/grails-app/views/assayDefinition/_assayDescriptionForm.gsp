<%@ page import="bard.db.registration.*" %>

<div class="control-group">
	<label class="control-label" for="assayStatus"><g:message code="assay.assayStatus.label" default="Assay Status:" /></label>
	<div class="controls">
		<g:select name="assayStatus" from="${bard.db.registration.AssayStatus?.values()}" keys="${bard.db.registration.AssayStatus.values()*.name()}" required="" value="${assayInstance?.assayStatus?.name()}"/>
	</div>
</div>
							
<div class="control-group  fieldcontain ${hasErrors(bean: assayInstance, field: 'assayTitle', 'error')} required">
	<label class="control-label" for="assayTitle"><g:message code="assay.assayTitle.label" default="Assay Title:" /></label>
	<div class="controls">
		<g:textField name="assayTitle" maxlength="250" required="" value="${assayInstance?.assayTitle}"/>
		<span class="help-inline">
			<g:hasErrors bean="${assayInstance}" field="assayTitle">
				<div class="alert alert-block alert-error fade in">
				<g:eachError bean="${assayInstance}" field="assayTitle">
					<p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message error="${it}"/></p>
				</g:eachError>
				</div>
			</g:hasErrors>
		</span>		
	</div>
</div>
							
<div class="control-group fieldcontain ${hasErrors(bean: assayInstance, field: 'assayName', 'error')} required">
	<label class="control-label" for="assayName"><g:message code="assay.assayName.label" default="Assay Name" /></label>
	<div class="controls">
		<g:textArea name="assayName" cols="40" rows="5" maxlength="1000" required="*" value="${assayInstance?.assayName}"/>
		<span class="help-inline">
			<g:hasErrors bean="${assayInstance}" field="assayName">
				<div class="alert alert-block alert-error fade in">				
				<g:eachError bean="${assayInstance}" field="assayName">
					<p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message error="${it}"/></p>
				</g:eachError>
				</div>
			</g:hasErrors>
		</span>		
	</div>
</div>
							
<div class="control-group  fieldcontain ${hasErrors(bean: assayInstance, field: 'assayVersion', 'error')} required"">
	<label class="control-label" for="assayVersion"><g:message code="assay.assayVersion.label" default="Assay Version" /></label>
	<div class="controls">
		<g:textField name="assayVersion" maxlength="10" required="" value="${assayInstance?.assayVersion}"/>
		<span class="help-inline">
			<g:hasErrors bean="${assayInstance}" field="assayVersion">
				<div class="alert alert-block alert-error fade in">
				<g:eachError bean="${assayInstance}" field="assayVersion">
					<p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message error="${it}"/></p>
				</g:eachError>
				</div>
			</g:hasErrors>
		</span>		
	</div>
</div>
							
<div class="control-group  fieldcontain ${hasErrors(bean: assayInstance, field: 'designedBy', 'error')} required"">
	<label class="control-label" for="designedBy"><g:message code="assay.designedBy.label" default="Designed By" /></label>
	<div class="controls">
		<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
		<span class="help-inline">
			<g:hasErrors bean="${assayInstance}" field="designedBy">
				<div class="alert alert-block alert-error fade in">
				<g:eachError bean="${assayInstance}" field="designedBy">
					<p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message error="${it}"/></p>
				</g:eachError>
				</div>
			</g:hasErrors>
		</span>		
	</div>
</div>
							
<div class="control-group">
	<label class="control-label" for="assayType"><g:message code="assay.assayType.label" default="Assay Type" /></label>
	<div class="controls">
		<g:select name="assayType" from="${assayInstance.constraints.assayType.inList}" value="${assayInstance?.assayType}" valueMessagePrefix="assay.assayType" noSelection="['': '']"/>
	</div>
</div>