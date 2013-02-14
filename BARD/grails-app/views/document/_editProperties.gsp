<%@ page import="bard.db.model.IDocumentType" %>

<div class="control-group ${hasErrors(bean: document, field: 'documentType', 'error')}">
    <label class="control-label" for="documentType">
        <g:message code="document.type.label"/>:</label>

    <div class="controls">
        <g:select id="documentType" name="documentType" from="${IDocumentType.DOCUMENT_TYPE_DISPLAY_ORDER}"
                  value="${document?.documentType}" noSelection="${['': 'Select a Type']}"/>
        <span class="help-inline"><g:fieldError field="documentType" bean="${document}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: document, field: 'documentName', 'error')}">
    <label class="control-label" for="documentName"><g:message code="document.name.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="documentName" name="documentName"
                     value="${document?.documentName}"/>
        <span class="help-inline"><g:fieldError field="documentName" bean="${document}"/></span>
    </div>
</div>

<r:require modules="richtexteditor"/>

<div class="control-group ${hasErrors(bean: document, field: 'documentContent', 'error')}">
    <label class="control-label" for="documentContent"><g:message code="document.content.label"/></label>

    <div class="controls">
        <g:textArea class="span8" id="documentContent" rows="20" name="documentContent"
                    value="${document?.documentContent}"/>
        <span class="help-inline"><g:fieldError field="documentContent" bean="${document}"/></span>
    </div>
</div>

<div class="control-group ">
    <label class="control-label" for="modifiedBy"><g:message code="default.modifiedBy.label"/></label>

    <div class="controls">
        <g:textField class="span4" disabled="disabled" id="modifiedBy" rows="20" name="modifiedBy"
                     value="${document?.modifiedBy}"/>
    </div>
</div>

<div class="control-group ">
    <label class="control-label" for="dateCreated"><g:message code="default.dateCreated.label"/></label>

    <div class="controls">
        <g:textField class="span4" disabled="disabled" id="dateCreated" rows="20" name="dateCreated"
                     value="${formatDate(date: document?.dateCreated)}"/>
    </div>
</div>

<div class="control-group ">
    <label class="control-label" for="lastUpdated"><g:message code="default.lastUpdated.label"/></label>

    <div class="controls">
        <g:textField class="span4" disabled="disabled" id="lastUpdated" rows="20" name="lastUpdated"
                             value="${formatDate(date: document?.lastUpdated)}"/>

    </div>
</div>