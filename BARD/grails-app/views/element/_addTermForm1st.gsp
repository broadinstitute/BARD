<%@ page import="bard.db.dictionary.*" %>
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': termCommand?.errors?.allErrors]"/>
<h3>Propose New Term (page 1 of 2)</h3>

<div class="control-group">
    <label>
        <h4>1. Select a parent term from the current BARD Hierarchy.</h4>
    </label>
</div>

<div class="control-group ${hasErrors(bean: termCommand, field: 'parentLabel', 'error')}">
    <label class="control-label" for="attributeElementId"><g:message
            code="termCommand.parentLabel.label"/>:</label>

    <div class="controls">
        <g:hiddenField id="attributeElementId" name="attributeElementId" class="span11"
                       value="${termCommand?.parentElementId}"/>
        <span class="help-inline"><g:fieldError field="parentLabel" bean="${termCommand}"/></span>

    </div>
</div>


<div class="control-group">
    <div class="controls" id="attributeElementErrorField">
    </div>
</div>

<div class="control-group">
    <div class="controls">
        <p><i class="icon-info-sign"></i> We set the parent term for you based on the attribute from your context item, however you can delete this and choose something else
        </p>
    </div>
</div>

<div class="control-group ${hasErrors(bean: termCommand, field: 'parentDescription', 'error')} required">
    <label class="control-label" for="parentDescription">
        <strong><g:message code="termCommand.parentDescription.label"/></strong>
    </label>

    <div class="controls">
        <g:textArea name="parentDescription" id="parentDescription" readonly="true" cols="40" rows="5"
                    maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}"
                    value="${termCommand?.parentDescription}"/>
        <span class="help-inline"><g:fieldError field="parentDescription" bean="${termCommand}"/></span>
    </div>
</div>
