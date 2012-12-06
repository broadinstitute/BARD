<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%-- A template for showing summary for both project and assay def --%>

<div>
    <dl class="dl-horizontal">
        <g:if test="${summary?.id}">
            <dt><g:message code="summary.id.label" default="ID:" /></dt>
            <dd><g:fieldValue bean="${summary}" field="id"/></dd>
        </g:if>
        <g:if test="${summary?.name}">
            <dt><g:message code="summary.name.label" default="name:" /></dt>
            <dd><g:fieldValue bean="${summary}" field="name"/></dd>
        </g:if>
        <g:if test="${summary?.description}">
            <dt><g:message code="summary.description.label" default="description:" /></dt>
            <dd><g:fieldValue bean="${summary}" field="description"/></dd>
        </g:if>
        <g:if test="${summary?.dateCreated}">
            <dt><g:message code="summary.dateCreated.label" default="Date Created:" /></dt>
            <dd><g:formatDate date="${summary?.dateCreated}" /></dd>
        </g:if>
    </dl>
</div>

