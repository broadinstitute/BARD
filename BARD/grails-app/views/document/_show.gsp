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
    <li>
    <g:each in="${documents.sort{it.documentType}}" var="document">
        <g:if test="${document?.documentName}">
            <li>
                <span id="documentName-label"><g:message code="document.documentName.label" default="Name: " /></span>
                <span><g:fieldValue bean="${document}" field="documentName"/></span>
            </li>
        </g:if>

        <g:if test="${document?.documentContent}">
            <li>
                <g:message code="document.documentContent.label" default="Content:" />
                <span><g:fieldValue bean="${document}" field="documentContent"/></span>
            </li>
        </g:if>
        <br/>
    </g:each>
    </li>
</g:if>
<g:else>
    <span>No documents found</span>
</g:else>

</div>

