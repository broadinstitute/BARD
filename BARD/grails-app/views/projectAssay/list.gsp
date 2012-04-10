
<%@ page import="bard.db.model.ProjectAssay" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'projectAssay.label', default: 'ProjectAssay')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-projectAssay" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-projectAssay" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="stage" title="${message(code: 'projectAssay.stage.label', default: 'Stage')}" />
					
						<g:sortableColumn property="sequenceNo" title="${message(code: 'projectAssay.sequenceNo.label', default: 'Sequence No')}" />
					
						<g:sortableColumn property="promotionThreshold" title="${message(code: 'projectAssay.promotionThreshold.label', default: 'Promotion Threshold')}" />
					
						<g:sortableColumn property="promotionCriteria" title="${message(code: 'projectAssay.promotionCriteria.label', default: 'Promotion Criteria')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'projectAssay.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'projectAssay.lastUpdated.label', default: 'Last Updated')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${projectAssayInstanceList}" status="i" var="projectAssayInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${projectAssayInstance.id}">${fieldValue(bean: projectAssayInstance, field: "stage")}</g:link></td>
					
						<td>${fieldValue(bean: projectAssayInstance, field: "sequenceNo")}</td>
					
						<td>${fieldValue(bean: projectAssayInstance, field: "promotionThreshold")}</td>
					
						<td>${fieldValue(bean: projectAssayInstance, field: "promotionCriteria")}</td>
					
						<td><g:formatDate date="${projectAssayInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${projectAssayInstance.lastUpdated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${projectAssayInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
