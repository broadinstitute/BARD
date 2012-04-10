
<%@ page import="bard.db.model.Unit" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'unit.label', default: 'Unit')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-unit" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-unit" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list unit">
			
				<g:if test="${unitInstance?.unit}">
				<li class="fieldcontain">
					<span id="unit-label" class="property-label"><g:message code="unit.unit.label" default="Unit" /></span>
					
						<span class="property-value" aria-labelledby="unit-label"><g:fieldValue bean="${unitInstance}" field="unit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="unit.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${unitInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="unit.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${unitInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="unit.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${unitInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="unit.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${unitInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.elements}">
				<li class="fieldcontain">
					<span id="elements-label" class="property-label"><g:message code="unit.elements.label" default="Elements" /></span>
					
						<g:each in="${unitInstance.elements}" var="e">
						<span class="property-value" aria-labelledby="elements-label"><g:link controller="element" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.measures}">
				<li class="fieldcontain">
					<span id="measures-label" class="property-label"><g:message code="unit.measures.label" default="Measures" /></span>
					
						<g:each in="${unitInstance.measures}" var="m">
						<span class="property-value" aria-labelledby="measures-label"><g:link controller="measure" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.resultTypes}">
				<li class="fieldcontain">
					<span id="resultTypes-label" class="property-label"><g:message code="unit.resultTypes.label" default="Result Types" /></span>
					
						<g:each in="${unitInstance.resultTypes}" var="r">
						<span class="property-value" aria-labelledby="resultTypes-label"><g:link controller="resultType" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.results}">
				<li class="fieldcontain">
					<span id="results-label" class="property-label"><g:message code="unit.results.label" default="Results" /></span>
					
						<g:each in="${unitInstance.results}" var="r">
						<span class="property-value" aria-labelledby="results-label"><g:link controller="result" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.unitConversionsForFromUnit}">
				<li class="fieldcontain">
					<span id="unitConversionsForFromUnit-label" class="property-label"><g:message code="unit.unitConversionsForFromUnit.label" default="Unit Conversions For From Unit" /></span>
					
						<g:each in="${unitInstance.unitConversionsForFromUnit}" var="u">
						<span class="property-value" aria-labelledby="unitConversionsForFromUnit-label"><g:link controller="unitConversion" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${unitInstance?.unitConversionsForToUnit}">
				<li class="fieldcontain">
					<span id="unitConversionsForToUnit-label" class="property-label"><g:message code="unit.unitConversionsForToUnit.label" default="Unit Conversions For To Unit" /></span>
					
						<g:each in="${unitInstance.unitConversionsForToUnit}" var="u">
						<span class="property-value" aria-labelledby="unitConversionsForToUnit-label"><g:link controller="unitConversion" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${unitInstance?.id}" />
					<g:link class="edit" action="edit" id="${unitInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
