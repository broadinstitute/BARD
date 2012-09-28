<%@ page import="bard.db.registration.Assay" %>



<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayStatus', 'error')} required">
	<label for="assayStatus">
		<g:message code="assay.assayStatus.label" default="Assay Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="assayStatus" from="${bard.db.registration.AssayStatus?.values()}" keys="${bard.db.registration.AssayStatus.values()*.name()}" required="" value="${assayInstance?.assayStatus?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayTitle', 'error')} required">
	<label for="assayTitle">
		<g:message code="assay.assayTitle.label" default="Assay Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="assayTitle" maxlength="250" required="" value="${assayInstance?.assayTitle}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayName', 'error')} required">
	<label for="assayName">
		<g:message code="assay.assayName.label" default="Assay Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="assayName" cols="40" rows="5" maxlength="1000" required="" value="${assayInstance?.assayName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayVersion', 'error')} required">
	<label for="assayVersion">
		<g:message code="assay.assayVersion.label" default="Assay Version" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="assayVersion" maxlength="10" required="" value="${assayInstance?.assayVersion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'designedBy', 'error')} ">
	<label for="designedBy">
		<g:message code="assay.designedBy.label" default="Designed By" />
		
	</label>
	<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'readyForExtraction', 'error')} required">
	<label for="readyForExtraction">
		<g:message code="assay.readyForExtraction.label" default="Ready For Extraction" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="readyForExtraction" from="${bard.db.enums.ReadyForExtraction?.values()}" keys="${bard.db.enums.ReadyForExtraction.values()*.name()}" required="" value="${assayInstance?.readyForExtraction?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayType', 'error')} ">
	<label for="assayType">
		<g:message code="assay.assayType.label" default="Assay Type" />
		
	</label>
	<g:select name="assayType" from="${assayInstance.constraints.assayType.inList}" value="${assayInstance?.assayType}" valueMessagePrefix="assay.assayType" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="assay.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${assayInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayContexts', 'error')} ">
	<label for="assayContexts">
		<g:message code="assay.assayContexts.label" default="Assay Contexts" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.assayContexts?}" var="a">
    <li><g:link controller="assayContext" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="assayContext" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'assayContext.label', default: 'AssayContext')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayDocuments', 'error')} ">
	<label for="assayDocuments">
		<g:message code="assay.assayDocuments.label" default="Assay Documents" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.assayDocuments?}" var="a">
    <li><g:link controller="assayDocument" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="assayDocument" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'assayDocument.label', default: 'AssayDocument')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'experiments', 'error')} ">
	<label for="experiments">
		<g:message code="assay.experiments.label" default="Experiments" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.experiments?}" var="e">
    <li><g:link controller="experiment" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="experiment" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'experiment.label', default: 'Experiment')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'measures', 'error')} ">
	<label for="measures">
		<g:message code="assay.measures.label" default="Measures" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.measures?}" var="m">
    <li><g:link controller="measure" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="measure" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'measure.label', default: 'Measure')])}</g:link>
</li>
</ul>

</div>

