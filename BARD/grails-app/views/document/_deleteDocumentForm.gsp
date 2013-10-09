<%@ page import="bard.db.enums.DocumentType" %>
<br/>
<br/>

<div class="row-fluid">
    <div class="span12">
        <g:form action="delete" controller="document" params="${[type: document.owner.class.simpleName]}" id="${document.id}">
            <g:if test="${document.documentType in [DocumentType.DOCUMENT_TYPE_COMMENTS, DocumentType.DOCUMENT_TYPE_DESCRIPTION, DocumentType.DOCUMENT_TYPE_PROTOCOL, DocumentType.DOCUMENT_TYPE_OTHER]}">
                <g:link controller="document" action="edit"  params="${[type: document.owner.class.simpleName]}" id="${document.id}" class="btn">
                    <i class="icon-pencil"></i> Edit ${document.documentType.id}
                </g:link>
            </g:if>
            <button type="submit" class="btn"
                    onsubmit="return confirm('Are you sure you wish to delete the document?');">
                <i class="icon-trash"></i> Delete ${document.documentType.id}
            </button>
        </g:form>
    </div>
</div>
