<%@ page import="bard.db.model.Project" %>



<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'projectName', 'error')} ">
	<label for="projectName">
		<g:message code="project.projectName.label" default="Project Name" />
		
	</label>
	<g:textArea name="projectName" cols="40" rows="5" maxlength="256" value="${projectInstance?.projectName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'groupType', 'error')} ">
	<label for="groupType">
		<g:message code="project.groupType.label" default="Group Type" />
		
	</label>
	<g:textField name="groupType" maxlength="20" value="${projectInstance?.groupType}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="project.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${projectInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="project.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${projectInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'experiments', 'error')} ">
	<label for="experiments">
		<g:message code="project.experiments.label" default="Experiments" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${projectInstance?.experiments?}" var="e">
    <li><g:link controller="experiment" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="experiment" action="create" params="['project.id': projectInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'experiment.label', default: 'Experiment')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'projectAssaies', 'error')} ">
	<label for="projectAssaies">
		<g:message code="project.projectAssaies.label" default="Project Assaies" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${projectInstance?.projectAssaies?}" var="p">
    <li><g:link controller="projectAssay" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="projectAssay" action="create" params="['project.id': projectInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'projectAssay.label', default: 'ProjectAssay')])}</g:link>
</li>
</ul>

</div>

