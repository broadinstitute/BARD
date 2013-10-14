<%@ page import="bard.db.dictionary.*" %>
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': termCommand?.errors?.allErrors]"/>
<h3>Propose New Term (page 1 of 2)</h3>
<div class="control-group">
    <label>
        <h4>1. Select a parent term from the current BARD Hierarchy.</h4>
    </label>
</div>
%{--<div class="control-group ${hasErrors(bean: termCommand, field: 'parentLabel', 'error')} required">--}%
    %{--<label class="control-label" for="parentLabel">--}%
        %{--<strong><g:message code="termCommand.parentLabel.label"/>  </strong>--}%
        %{--<span class="required-indicator">*</span>--}%

    %{--</label>--}%

    %{--<div class="controls">--}%
        %{--<g:textField id="parentLabel" name="parentLabel" maxlength="${bard.db.dictionary.Element.LABEL_MAX_SIZE}" value="${termCommand?.parentLabel}"/>--}%
        %{--<span class="help-inline"><g:fieldError field="parentLabel" bean="${termCommand}"/></span>--}%
    %{--</div>--}%
%{--</div>--}%
%{--Search & autocomplete text field was taken from contextItem edit/create; depends on the contextItem.js--}%
<div class="control-group ${hasErrors(bean: termCommand, field: 'parentLabel', 'error')}">
    <label class="control-label" for="attributeElementId"><g:message
            code="termCommand.parentLabel.label"/>:</label>

    <div class="controls">
        <g:hiddenField id="attributeElementId" name="attributeElementId" class="span11"
                       value="${termCommand?.parentElementId}"/>
        <span class="help-inline"><g:fieldError field="parentLabel" bean="${termCommand}"/></span>

    </div>
</div>

<div class="control-group ${hasErrors(bean: termCommand, field: 'parentDescription', 'error')} required">
    <label class="control-label" for="description">
        <strong><g:message code="termCommand.parentDescription.label"/></strong>
    </label>

    <div class="controls">
        <g:textArea name="parentDescription" id="parentDescription" readonly="true" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}" value="${termCommand?.parentDescription}"/>
        <span class="help-inline"><g:fieldError field="parentDescription" bean="${termCommand}"/></span>
    </div>
</div>
