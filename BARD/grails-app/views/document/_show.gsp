<dl class="dl-horizontal">
    <div>
        <g:form action="delete" controller="document" id="${document.id}" onsubmit="confirm('Are you sure you wish to delete the document?')">
            <g:link action="edit" controller="document" class="btn btn-primary" id="${document.id}">Edit</g:link>
            <input type="submit" value="Delete" class="btn btn-primary">
        </g:form>
    </div>
    <dt><g:message code="document.content.label" default="Name:"/></dt>
    <dd><g:fieldValue bean="${document}" field="documentName"/></dd>

    <dt><g:message code="document.content.label" default="Content:"/></dt>
    <dd>
        <g:if test="${document.documentType in [bard.db.model.IDocumentType.DOCUMENT_TYPE_EXTERNAL_URL, bard.db.model.IDocumentType.DOCUMENT_TYPE_PUBLICATION]}">
            <a href="${document.documentContent}">
                <g:fieldValue bean="${document}" field="documentContent"/>
            </a>
        </g:if>
        <g:else>
            <g:renderWithBreaks text="${document.documentContent}"/>
        </g:else>
    </dd>
</dl>
