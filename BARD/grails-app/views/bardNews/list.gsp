%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%


<%@ page import="bard.db.util.BardNews" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'bardNews.label', default: 'BardNews')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-bardNews" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-bardNews" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>

						<g:sortableColumn property="entryId" title="${message(code: 'bardNews.entryId.label', default: 'Entry Id')}" />

						<g:sortableColumn property="entryDateUpdated" title="${message(code: 'bardNews.entryDateUpdated.label', default: 'Entry Date Updated')}" />

						<g:sortableColumn property="title" title="${message(code: 'bardNews.title.label', default: 'Title')}" />

						<g:sortableColumn property="link" title="${message(code: 'bardNews.link.label', default: 'Link')}" />

						<g:sortableColumn property="authorName" title="${message(code: 'bardNews.authorName.label', default: 'Author Name')}" />

					</tr>
				</thead>
				<tbody>
				<g:each in="${bardNewsInstanceList}" status="i" var="bardNewsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td><g:link action="show" id="${bardNewsInstance.id}">${fieldValue(bean: bardNewsInstance, field: "entryId")}</g:link></td>

						<td><g:formatDate date="${bardNewsInstance.entryDateUpdated}" /></td>

						<td>${fieldValue(bean: bardNewsInstance, field: "title")}</td>

						<td>${fieldValue(bean: bardNewsInstance, field: "link")}</td>

						<td>${fieldValue(bean: bardNewsInstance, field: "authorName")}</td>

					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${bardNewsInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
