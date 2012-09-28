
<%@ page import="bard.db.registration.Assay" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assay.label', default: 'Assay')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-assay" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-assay" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="assayStatus" title="${message(code: 'assay.assayStatus.label', default: 'Assay Status')}" />
					
						<g:sortableColumn property="assayTitle" title="${message(code: 'assay.assayTitle.label', default: 'Assay Title')}" />
					
						<g:sortableColumn property="assayName" title="${message(code: 'assay.assayName.label', default: 'Assay Name')}" />
					
						<g:sortableColumn property="assayVersion" title="${message(code: 'assay.assayVersion.label', default: 'Assay Version')}" />
					
						<g:sortableColumn property="designedBy" title="${message(code: 'assay.designedBy.label', default: 'Designed By')}" />
					
						<g:sortableColumn property="readyForExtraction" title="${message(code: 'assay.readyForExtraction.label', default: 'Ready For Extraction')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${assayInstanceList}" status="i" var="assayInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${assayInstance.id}">${fieldValue(bean: assayInstance, field: "assayStatus")}</g:link></td>
					
						<td>${fieldValue(bean: assayInstance, field: "assayTitle")}</td>
					
						<td>${fieldValue(bean: assayInstance, field: "assayName")}</td>
					
						<td>${fieldValue(bean: assayInstance, field: "assayVersion")}</td>
					
						<td>${fieldValue(bean: assayInstance, field: "designedBy")}</td>
					
						<td>${fieldValue(bean: assayInstance, field: "readyForExtraction")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${assayInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
