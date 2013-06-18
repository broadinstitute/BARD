<%@ page import="bard.db.enums.DocumentType" %>


<div class="control-group ${hasErrors(bean: document, field: 'documentType', 'error')}">
    <label class="control-label" for="documentType">
        <g:message code="document.type.label"/>:</label>

    <div class="controls">
        <g:textField id="documentType" name="documentType" value="${document?.documentType.id}" readonly="true"/>
        <span class="help-inline"><g:fieldError field="documentType" bean="${document}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: document, field: 'documentName', 'error')}">
    <label class="control-label" for="documentName">* <g:message code="document.name.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="documentName" name="documentName"
                     value="${document?.documentName}"/>
        <span class="help-inline"><g:fieldError field="documentName" bean="${document}"/></span>
    </div>
</div>

%{--TODO use text box if the document type is a External URL or publication use the url type in html5--}%
<g:if test="${document.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL || document.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION}">
    <div class="control-group ${hasErrors(bean: document, field: 'documentContent', 'error')}">
        <label class="control-label" for="documentContent"><g:message code="document.url.label"/></label>

        <div class="controls">
            <input type="url" class="span8" id="documentContent" name="documentContent" value="${document?.documentContent}" placeholder="Enter a valid URL"/>
            <span class="help-inline"><g:fieldError field="documentContent" bean="${document}"/></span>
        </div>
    </div>
</g:if>
<g:else>
    <div class="control-group ${hasErrors(bean: document, field: 'documentContent', 'error')}">
        <label class="control-label" for="documentContent"><g:message code="document.content.label"/></label>

        <div class="controls">
            <g:textArea class="span8" id="documentContent" rows="20" name="documentContent"
                        value="${document?.documentContent}"/>
            <span class="help-inline"><g:fieldError field="documentContent" bean="${document}"/></span>
        </div>
    </div>
</g:else>
