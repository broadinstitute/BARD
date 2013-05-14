
<%@ page import="bard.db.registration.Assay" %>
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

				<g:if test="${assayInstance?.assayStatus}">
				<li class="fieldcontain">
					<span id="assayStatus-label" class="property-label"><g:message code="assay.assayStatus.label" default="Assay Status" /></span>

						<span class="property-value" aria-labelledby="assayStatus-label"><g:fieldValue bean="${assayInstance}" field="assayStatus.id"/></span>

				</li>
				</g:if>

				<g:if test="${assayInstance?.assayShortName}">
				<li class="fieldcontain">
					<span id="assayShortName-label" class="property-label"><g:message code="assay.assayShortName.label" default="Assay Short Name" /></span>

						<span class="property-value" aria-labelledby="assayShortName-label"><g:fieldValue bean="${assayInstance}" field="assayShortName"/></span>

				</li>
				</g:if>

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

				<g:if test="${assayInstance?.designedBy}">
				<li class="fieldcontain">
					<span id="designedBy-label" class="property-label"><g:message code="assay.designedBy.label" default="Designed By" /></span>

						<span class="property-value" aria-labelledby="designedBy-label"><g:fieldValue bean="${assayInstance}" field="designedBy"/></span>

				</li>
				</g:if>

				<g:if test="${assayInstance?.readyForExtraction}">
				<li class="fieldcontain">
					<span id="readyForExtraction-label" class="property-label"><g:message code="assay.readyForExtraction.label" default="Ready For Extraction" /></span>

						<span class="property-value" aria-labelledby="readyForExtraction-label"><g:fieldValue bean="${assayInstance}" field="readyForExtraction"/></span>

				</li>
				</g:if>

				<g:if test="${assayInstance?.assayType}">
				<li class="fieldcontain">
					<span id="assayType-label" class="property-label"><g:message code="assay.assayType.label" default="Assay Type" /></span>

						<span class="property-value" aria-labelledby="assayType-label"><g:fieldValue bean="${assayInstance}" field="assayType.id"/></span>

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

				<g:if test="${assayInstance?.assayContexts}">
				<li class="fieldcontain">
					<span id="assayContexts-label" class="property-label"><g:message code="assay.assayContexts.label" default="Assay Contexts" /></span>

						<g:each in="${assayInstance.assayContexts}" var="a">
						<span class="property-value" aria-labelledby="assayContexts-label"><g:link controller="assayContext" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>

				</li>
				</g:if>

				<g:if test="${assayInstance?.assayDocuments}">
				<li class="fieldcontain">
					<span id="assayDocuments-label" class="property-label"><g:message code="assay.assayDocuments.label" default="Assay Documents" /></span>

						<g:each in="${assayInstance.assayDocuments}" var="a">
						<span class="property-value" aria-labelledby="assayDocuments-label"><g:link controller="assayDocument" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>

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

				<g:if test="${assayInstance?.measures}">
				<li class="fieldcontain">
					<span id="measures-label" class="property-label"><g:message code="assay.measures.label" default="Measures" /></span>

						<g:each in="${assayInstance.measures}" var="m">
						<span class="property-value" aria-labelledby="measures-label"><g:link controller="measure" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></span>
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
