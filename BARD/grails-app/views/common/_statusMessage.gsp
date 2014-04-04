<%@ page import="bard.db.enums.Status" %>
<g:if test="${status == Status.APPROVED}">
    <dt><g:message code="entity.approvedBy.label" default="Approved By"/>:</dt>
    <dd id="approvedById">
        <g:if test="${displayName}">
            ${displayName} (<g:formatDate date="${approvedDate}" format="MM/dd/yyyy"/>)
        </g:if>
    </dd>
</g:if>
<g:if test="${status == Status.PROVISIONAL}">
    <dt><g:message code="entity.provisionalApprovedBy.label" default="Provisionally Approved By"/>:</dt>
    <dd id="approvedById">
        <g:if test="${displayName}">
            ${displayName} (<g:formatDate date="${approvedDate}" format="MM/dd/yyyy"/>)
        </g:if>
    </dd>
</g:if>