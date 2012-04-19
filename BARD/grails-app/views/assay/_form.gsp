<%@ page import="bard.db.registration.Assay" %>



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

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="assay.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${assayInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'designedBy', 'error')} ">
	<label for="designedBy">
		<g:message code="assay.designedBy.label" default="Designed By" />
		
	</label>
	<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="assay.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${assayInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'assayStatus', 'error')} required">
	<label for="assayStatus">
		<g:message code="assay.assayStatus.label" default="Assay Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="assayStatus" name="assayStatus.id" from="${bard.db.registration.AssayStatus.list()}" optionKey="id" required="" value="${assayInstance?.assayStatus?.id}" class="many-to-one"/>
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

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'externalAssaies', 'error')} ">
	<label for="externalAssaies">
		<g:message code="assay.externalAssaies.label" default="External Assaies" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.externalAssaies?}" var="e">
    <li><g:link controller="externalAssay" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="externalAssay" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'externalAssay.label', default: 'ExternalAssay')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'measureContextItems', 'error')} ">
	<label for="measureContextItems">
		<g:message code="assay.measureContextItems.label" default="Measure Context Items" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.measureContextItems?}" var="m">
    <li><g:link controller="measureContextItem" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="measureContextItem" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'measureContextItem.label', default: 'MeasureContextItem')])}</g:link>
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

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'projectAssaies', 'error')} ">
	<label for="projectAssaies">
		<g:message code="assay.projectAssaies.label" default="Project Assaies" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.projectAssaies?}" var="p">
    <li><g:link controller="projectAssay" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="projectAssay" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'projectAssay.label', default: 'ProjectAssay')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: assayInstance, field: 'protocols', 'error')} ">
	<label for="protocols">
		<g:message code="assay.protocols.label" default="Protocols" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${assayInstance?.protocols?}" var="p">
    <li><g:link controller="protocol" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="protocol" action="create" params="['assay.id': assayInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'protocol.label', default: 'Protocol')])}</g:link>
</li>
</ul>

</div>

