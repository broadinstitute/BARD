
<%@ page import="bard.db.model.UnitConversion" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'unitConversion.label', default: 'UnitConversion')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-unitConversion" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-unitConversion" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list unitConversion">
			
				<g:if test="${unitConversionInstance?.fromUnit}">
				<li class="fieldcontain">
					<span id="fromUnit-label" class="property-label"><g:message code="unitConversion.fromUnit.label" default="From Unit" /></span>
					
						<span class="property-value" aria-labelledby="fromUnit-label"><g:link controller="unit" action="show" id="${unitConversionInstance?.fromUnit?.id}">${unitConversionInstance?.fromUnit?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.toUnit}">
				<li class="fieldcontain">
					<span id="toUnit-label" class="property-label"><g:message code="unitConversion.toUnit.label" default="To Unit" /></span>
					
						<span class="property-value" aria-labelledby="toUnit-label"><g:link controller="unit" action="show" id="${unitConversionInstance?.toUnit?.id}">${unitConversionInstance?.toUnit?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.multiplier}">
				<li class="fieldcontain">
					<span id="multiplier-label" class="property-label"><g:message code="unitConversion.multiplier.label" default="Multiplier" /></span>
					
						<span class="property-value" aria-labelledby="multiplier-label"><g:fieldValue bean="${unitConversionInstance}" field="multiplier"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.offset}">
				<li class="fieldcontain">
					<span id="offset-label" class="property-label"><g:message code="unitConversion.offset.label" default="Offset" /></span>
					
						<span class="property-value" aria-labelledby="offset-label"><g:fieldValue bean="${unitConversionInstance}" field="offset"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.formula}">
				<li class="fieldcontain">
					<span id="formula-label" class="property-label"><g:message code="unitConversion.formula.label" default="Formula" /></span>
					
						<span class="property-value" aria-labelledby="formula-label"><g:fieldValue bean="${unitConversionInstance}" field="formula"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="unitConversion.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${unitConversionInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="unitConversion.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${unitConversionInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${unitConversionInstance?.modifiedBy}">
				<li class="fieldcontain">
					<span id="modifiedBy-label" class="property-label"><g:message code="unitConversion.modifiedBy.label" default="Modified By" /></span>
					
						<span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${unitConversionInstance}" field="modifiedBy"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${unitConversionInstance?.id}" />
					<g:link class="edit" action="edit" id="${unitConversionInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
