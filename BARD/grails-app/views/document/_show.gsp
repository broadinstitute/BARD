<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def --%>

<%-- Let's use cool HTML5

<div>
    <g:if test="${documents}">
        <g:each in="${documents.sort {it.documentType}}" var="document" status="i">
            <details>
                <summary>${document?.documentType}</summary>
                <p>${document?.documentName}</p>
                <p>${document?.documentContent.encodeAsHTML()}</p>
            </details>
        </g:each>
    </g:if>
    <g:else>
        <span>No documents found</span>
    </g:else>
</div>
--%>

<div>
    <g:if test="${documents}">
        <g:set var="documentMap" value="${documents.sort { it.documentName }.groupBy { it.documentType }}"/>
        <g:each in="${bard.db.model.IDocumentType.DOCUMENT_TYPE_DISPLAY_ORDER}" var="type">
            <g:set var="documentTypeList" value="${documentMap.get(type)}"/>
            <g:if test="${documentTypeList}">
                <h4>${type}<g:if test="${documentTypeList.size()>1}">(s)</g:if>:</h4>
                <dl class="dl-horizontal">
                <g:each in="${documentTypeList}" var="document">
                    <dt><g:message code="document.content.label" default="Name:"/></dt>
                    <dd><g:fieldValue bean="${document}" field="documentName"/></dd>

                    <dt><g:message code="document.content.label" default="Content:"/></dt>
                    <dd>
                        <g:if test="${ document.documentType in [bard.db.model.IDocumentType.DOCUMENT_TYPE_EXTERNAL_URL, bard.db.model.IDocumentType.DOCUMENT_TYPE_PAPER]}">
                            <a href="${document.documentContent}">
                                <g:fieldValue bean="${document}" field="documentContent"/>
                            </a>
                        </g:if>
                        <g:else>
                            <g:fieldValue bean="${document}" field="documentContent"/>
                        </g:else>
                    </dd>
                    <br/>
                </g:each>
            </g:if>
        </g:each>
        </dl>
    </g:if>
    <g:else>
        <span>No documents found</span>
    </g:else>
</div>

