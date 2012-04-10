
<%@ page import="bard.db.model.AssayStatus" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assayStatus.label', default: 'AssayStatus')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-assayStatus" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-assayStatus" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list assayStatus">
			
				<g:if test="${assayStatusInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="assayStatus.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${assayStatusInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayStatusInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="assayStatus.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${assayStatusInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayStatusInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="assayStatus.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${assayStatusInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayStatusInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="assayStatus.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${assayStatusInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayStatusInstance?.assays}">
				<li class="fieldcontain">
					<span id="assays-label" class="property-label"><g:message code="assayStatus.assays.label" default="Assays" /></span>
					
						<g:each in="${assayStatusInstance.assays}" var="a">
						<span class="property-value" aria-labelledby="assays-label"><g:link controller="assay" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${assayStatusInstance?.id}" />
					<g:link class="edit" action="edit" id="${assayStatusInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
