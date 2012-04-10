
<%@ page import="bard.db.model.Protocol" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'protocol.label', default: 'Protocol')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-protocol" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-protocol" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list protocol">
			
				<g:if test="${protocolInstance?.protocolName}">
				<li class="fieldcontain">
					<span id="protocolName-label" class="property-label"><g:message code="protocol.protocolName.label" default="Protocol Name" /></span>
					
						<span class="property-value" aria-labelledby="protocolName-label"><g:fieldValue bean="${protocolInstance}" field="protocolName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${protocolInstance?.protocolDocument}">
				<li class="fieldcontain">
					<span id="protocolDocument-label" class="property-label"><g:message code="protocol.protocolDocument.label" default="Protocol Document" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${protocolInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="protocol.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${protocolInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${protocolInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="protocol.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${protocolInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${protocolInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="protocol.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${protocolInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${protocolInstance?.assay}">
				<li class="fieldcontain">
					<span id="assay-label" class="property-label"><g:message code="protocol.assay.label" default="Assay" /></span>
					
						<span class="property-value" aria-labelledby="assay-label"><g:link controller="assay" action="show" id="${protocolInstance?.assay?.id}">${protocolInstance?.assay?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${protocolInstance?.id}" />
					<g:link class="edit" action="edit" id="${protocolInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
