
<%@ page import="bard.db.model.Project" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-project" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="projectName" title="${message(code: 'project.projectName.label', default: 'Project Name')}" />
					
						<g:sortableColumn property="groupType" title="${message(code: 'project.groupType.label', default: 'Group Type')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'project.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'project.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'project.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="modifiedBy" title="${message(code: 'project.modifiedBy.label', default: 'Modified By')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${projectInstanceList}" status="i" var="projectInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${projectInstance.id}">${fieldValue(bean: projectInstance, field: "projectName")}</g:link></td>
					
						<td>${fieldValue(bean: projectInstance, field: "groupType")}</td>
					
						<td>${fieldValue(bean: projectInstance, field: "description")}</td>
					
						<td><g:formatDate date="${projectInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${projectInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: projectInstance, field: "modifiedBy")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${projectInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
