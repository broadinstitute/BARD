
<%@ page import="bard.db.model.ProjectAssay" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'projectAssay.label', default: 'ProjectAssay')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-projectAssay" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-projectAssay" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list projectAssay">
			
				<g:if test="${projectAssayInstance?.stage}">
				<li class="fieldcontain">
					<span id="stage-label" class="property-label"><g:message code="projectAssay.stage.label" default="Stage" /></span>
					
						<span class="property-value" aria-labelledby="stage-label"><g:fieldValue bean="${projectAssayInstance}" field="stage"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.sequenceNo}">
				<li class="fieldcontain">
					<span id="sequenceNo-label" class="property-label"><g:message code="projectAssay.sequenceNo.label" default="Sequence No" /></span>
					
						<span class="property-value" aria-labelledby="sequenceNo-label"><g:fieldValue bean="${projectAssayInstance}" field="sequenceNo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.promotionThreshold}">
				<li class="fieldcontain">
					<span id="promotionThreshold-label" class="property-label"><g:message code="projectAssay.promotionThreshold.label" default="Promotion Threshold" /></span>
					
						<span class="property-value" aria-labelledby="promotionThreshold-label"><g:fieldValue bean="${projectAssayInstance}" field="promotionThreshold"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.promotionCriteria}">
				<li class="fieldcontain">
					<span id="promotionCriteria-label" class="property-label"><g:message code="projectAssay.promotionCriteria.label" default="Promotion Criteria" /></span>
					
						<span class="property-value" aria-labelledby="promotionCriteria-label"><g:fieldValue bean="${projectAssayInstance}" field="promotionCriteria"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="projectAssay.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${projectAssayInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="projectAssay.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${projectAssayInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="projectAssay.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${projectAssayInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.assay}">
				<li class="fieldcontain">
					<span id="assay-label" class="property-label"><g:message code="projectAssay.assay.label" default="Assay" /></span>
					
						<span class="property-value" aria-labelledby="assay-label"><g:link controller="assay" action="show" id="${projectAssayInstance?.assay?.id}">${projectAssayInstance?.assay?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${projectAssayInstance?.project}">
				<li class="fieldcontain">
					<span id="project-label" class="property-label"><g:message code="projectAssay.project.label" default="Project" /></span>
					
						<span class="property-value" aria-labelledby="project-label"><g:link controller="project" action="show" id="${projectAssayInstance?.project?.id}">${projectAssayInstance?.project?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${projectAssayInstance?.id}" />
					<g:link class="edit" action="edit" id="${projectAssayInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
