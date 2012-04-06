
<%@ page import="bard.db.model.Assay" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assay.label', default: 'Assay')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-assay" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-assay" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list assay">
			
				<g:if test="${assayInstance?.assayName}">
				<li class="fieldcontain">
					<span id="assayName-label" class="property-label"><g:message code="assay.assayName.label" default="Assay Name" /></span>
					
						<span class="property-value" aria-labelledby="assayName-label"><g:fieldValue bean="${assayInstance}" field="assayName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.assayVersion}">
				<li class="fieldcontain">
					<span id="assayVersion-label" class="property-label"><g:message code="assay.assayVersion.label" default="Assay Version" /></span>
					
						<span class="property-value" aria-labelledby="assayVersion-label"><g:fieldValue bean="${assayInstance}" field="assayVersion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="assay.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${assayInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.designedBy}">
				<li class="fieldcontain">
					<span id="designedBy-label" class="property-label"><g:message code="assay.designedBy.label" default="Designed By" /></span>
					
						<span class="property-value" aria-labelledby="designedBy-label"><g:fieldValue bean="${assayInstance}" field="designedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="assay.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${assayInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="assay.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${assayInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="assay.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${assayInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.assayStatus}">
				<li class="fieldcontain">
					<span id="assayStatus-label" class="property-label"><g:message code="assay.assayStatus.label" default="Assay Status" /></span>
					
						<span class="property-value" aria-labelledby="assayStatus-label"><g:link controller="assayStatus" action="show" id="${assayInstance?.assayStatus?.id}">${assayInstance?.assayStatus?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.experiments}">
				<li class="fieldcontain">
					<span id="experiments-label" class="property-label"><g:message code="assay.experiments.label" default="Experiments" /></span>
					
						<g:each in="${assayInstance.experiments}" var="e">
						<span class="property-value" aria-labelledby="experiments-label"><g:link controller="experiment" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.externalAssaies}">
				<li class="fieldcontain">
					<span id="externalAssaies-label" class="property-label"><g:message code="assay.externalAssaies.label" default="External Assaies" /></span>
					
						<g:each in="${assayInstance.externalAssaies}" var="e">
						<span class="property-value" aria-labelledby="externalAssaies-label"><g:link controller="externalAssay" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.measureContextItems}">
				<li class="fieldcontain">
					<span id="measureContextItems-label" class="property-label"><g:message code="assay.measureContextItems.label" default="Measure Context Items" /></span>
					
						<g:each in="${assayInstance.measureContextItems}" var="m">
						<span class="property-value" aria-labelledby="measureContextItems-label"><g:link controller="measureContextItem" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.measures}">
				<li class="fieldcontain">
					<span id="measures-label" class="property-label"><g:message code="assay.measures.label" default="Measures" /></span>
					
						<g:each in="${assayInstance.measures}" var="m">
						<span class="property-value" aria-labelledby="measures-label"><g:link controller="measure" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.projectAssaies}">
				<li class="fieldcontain">
					<span id="projectAssaies-label" class="property-label"><g:message code="assay.projectAssaies.label" default="Project Assaies" /></span>
					
						<g:each in="${assayInstance.projectAssaies}" var="p">
						<span class="property-value" aria-labelledby="projectAssaies-label"><g:link controller="projectAssay" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.protocols}">
				<li class="fieldcontain">
					<span id="protocols-label" class="property-label"><g:message code="assay.protocols.label" default="Protocols" /></span>
					
						<g:each in="${assayInstance.protocols}" var="p">
						<span class="property-value" aria-labelledby="protocols-label"><g:link controller="protocol" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${assayInstance?.id}" />
					<g:link class="edit" action="edit" id="${assayInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
