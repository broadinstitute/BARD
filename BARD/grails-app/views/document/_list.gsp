<%@ page import="bard.db.enums.DocumentType" %>
<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<div>
    <g:if test="${documents}">
        <g:set var="documentMap" value="${documents.sort { it.documentName }.groupBy { it.documentType }}"/>
        <g:each in="${DocumentType.DOCUMENT_TYPE_DISPLAY_ORDER}" var="type">
            <g:set var="documentTypeList" value="${documentMap.get(type)}"/>
            <g:if test="${documentTypeList}">
                <h4>${type}<g:if test="${documentTypeList.size()>1 && !DocumentType.DOCUMENT_TYPE_COMMENTS == type}">s</g:if>:</h4>
                <g:each in="${documentTypeList}" var="document">
                    <g:render template="${documentTemplate}" model="${[document: document]}"/>
                </g:each>
            </g:if>
        </g:each>
    </g:if>
    <g:else>
        <span>No documents found</span>
    </g:else>
</div>
