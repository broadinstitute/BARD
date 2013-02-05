<%@ page import="bard.db.model.IDocumentType" %>

  <div class="control-group">
    <label class="control-label" for="documentType"><g:message code="document.type.label" default="Type:"/></label>
    <div class="controls">
        <g:select id="documentType" name="documentType" from="${IDocumentType.DOCUMENT_TYPE_DISPLAY_ORDER}" value="${document?.documentType}"/>
    </div>
  </div>

<div class="control-group">
    <label class="control-label" for="documentName"><g:message code="document.name.label" default="Name:"/></label>
    <div class="controls">
        <g:textField class="span8" id="documentName" name="documentName" value="${document?.documentName}"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="documentContent"><g:message code="document.content.label" default="Content:"/></label>
    <div class="controls">
        <g:textArea  class="span8" id="documentContent" rows="20" name="documentContent" value="${document?.documentContent}"/>
    </div>
</div>

