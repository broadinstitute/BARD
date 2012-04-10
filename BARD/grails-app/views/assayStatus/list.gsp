
<%@ page import="bard.db.model.AssayStatus" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assayStatus.label', default: 'AssayStatus')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-assayStatus" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-assayStatus" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="status" title="${message(code: 'assayStatus.status.label', default: 'Status')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'assayStatus.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'assayStatus.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="modifiedBy" title="${message(code: 'assayStatus.modifiedBy.label', default: 'Modified By')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${assayStatusInstanceList}" status="i" var="assayStatusInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${assayStatusInstance.id}">${fieldValue(bean: assayStatusInstance, field: "status")}</g:link></td>
					
						<td><g:formatDate date="${assayStatusInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${assayStatusInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: assayStatusInstance, field: "modifiedBy")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${assayStatusInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
