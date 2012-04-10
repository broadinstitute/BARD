
<%@ page import="bard.db.model.ElementStatus" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'elementStatus.label', default: 'ElementStatus')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-elementStatus" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-elementStatus" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list elementStatus">
			
				<g:if test="${elementStatusInstance?.elementStatus}">
				<li class="fieldcontain">
					<span id="elementStatus-label" class="property-label"><g:message code="elementStatus.elementStatus.label" default="Element Status" /></span>
					
						<span class="property-value" aria-labelledby="elementStatus-label"><g:fieldValue bean="${elementStatusInstance}" field="elementStatus"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.capability}">
				<li class="fieldcontain">
					<span id="capability-label" class="property-label"><g:message code="elementStatus.capability.label" default="Capability" /></span>
					
						<span class="property-value" aria-labelledby="capability-label"><g:fieldValue bean="${elementStatusInstance}" field="capability"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="elementStatus.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${elementStatusInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="elementStatus.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${elementStatusInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="elementStatus.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${elementStatusInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.elements}">
				<li class="fieldcontain">
					<span id="elements-label" class="property-label"><g:message code="elementStatus.elements.label" default="Elements" /></span>
					
						<g:each in="${elementStatusInstance.elements}" var="e">
						<span class="property-value" aria-labelledby="elements-label"><g:link controller="element" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${elementStatusInstance?.resultTypes}">
				<li class="fieldcontain">
					<span id="resultTypes-label" class="property-label"><g:message code="elementStatus.resultTypes.label" default="Result Types" /></span>
					
						<g:each in="${elementStatusInstance.resultTypes}" var="r">
						<span class="property-value" aria-labelledby="resultTypes-label"><g:link controller="resultType" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${elementStatusInstance?.id}" />
					<g:link class="edit" action="edit" id="${elementStatusInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
