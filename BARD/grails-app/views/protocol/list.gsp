
<%@ page import="bard.db.model.Protocol" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'protocol.label', default: 'Protocol')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-protocol" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-protocol" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="protocolName" title="${message(code: 'protocol.protocolName.label', default: 'Protocol Name')}" />
					
						<g:sortableColumn property="protocolDocument" title="${message(code: 'protocol.protocolDocument.label', default: 'Protocol Document')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'protocol.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'protocol.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="modifiedBy" title="${message(code: 'protocol.modifiedBy.label', default: 'Modified By')}" />
					
						<th><g:message code="protocol.assay.label" default="Assay" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${protocolInstanceList}" status="i" var="protocolInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${protocolInstance.id}">${fieldValue(bean: protocolInstance, field: "protocolName")}</g:link></td>
					
						<td>${fieldValue(bean: protocolInstance, field: "protocolDocument")}</td>
					
						<td><g:formatDate date="${protocolInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${protocolInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: protocolInstance, field: "modifiedBy")}</td>
					
						<td>${fieldValue(bean: protocolInstance, field: "assay")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${protocolInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
