<%@ page import="bard.db.model.ProjectAssay" %>



<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'stage', 'error')} ">
	<label for="stage">
		<g:message code="projectAssay.stage.label" default="Stage" />
		
	</label>
	<g:textField name="stage" maxlength="20" value="${projectAssayInstance?.stage}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'sequenceNo', 'error')} ">
	<label for="sequenceNo">
		<g:message code="projectAssay.sequenceNo.label" default="Sequence No" />
		
	</label>
	<g:field type="number" name="sequenceNo" value="${fieldValue(bean: projectAssayInstance, field: 'sequenceNo')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'promotionThreshold', 'error')} ">
	<label for="promotionThreshold">
		<g:message code="projectAssay.promotionThreshold.label" default="Promotion Threshold" />
		
	</label>
	<g:field type="number" name="promotionThreshold" value="${fieldValue(bean: projectAssayInstance, field: 'promotionThreshold')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'promotionCriteria', 'error')} ">
	<label for="promotionCriteria">
		<g:message code="projectAssay.promotionCriteria.label" default="Promotion Criteria" />
		
	</label>
	<g:textArea name="promotionCriteria" cols="40" rows="5" maxlength="1000" value="${projectAssayInstance?.promotionCriteria}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="projectAssay.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${projectAssayInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'assay', 'error')} required">
	<label for="assay">
		<g:message code="projectAssay.assay.label" default="Assay" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="assay" name="assay.id" from="${bard.db.model.Assay.list()}" optionKey="id" required="" value="${projectAssayInstance?.assay?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: projectAssayInstance, field: 'project', 'error')} required">
	<label for="project">
		<g:message code="projectAssay.project.label" default="Project" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="project" name="project.id" from="${bard.db.model.Project.list()}" optionKey="id" required="" value="${projectAssayInstance?.project?.id}" class="many-to-one"/>
</div>

