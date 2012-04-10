
<%@ page import="bard.db.model.ElementStatus" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'elementStatus.label', default: 'ElementStatus')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-elementStatus" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-elementStatus" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="elementStatus" title="${message(code: 'elementStatus.elementStatus.label', default: 'Element Status')}" />
					
						<g:sortableColumn property="capability" title="${message(code: 'elementStatus.capability.label', default: 'Capability')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'elementStatus.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'elementStatus.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="modifiedBy" title="${message(code: 'elementStatus.modifiedBy.label', default: 'Modified By')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${elementStatusInstanceList}" status="i" var="elementStatusInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${elementStatusInstance.id}">${fieldValue(bean: elementStatusInstance, field: "elementStatus")}</g:link></td>
					
						<td>${fieldValue(bean: elementStatusInstance, field: "capability")}</td>
					
						<td><g:formatDate date="${elementStatusInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${elementStatusInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: elementStatusInstance, field: "modifiedBy")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${elementStatusInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
