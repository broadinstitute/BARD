<%@ page import="bard.db.dictionary.*" %>
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': termCommand?.errors?.allErrors]"/>
<h3>Propose New Term (page 2 of 2)</h3>
<div class="control-group">
    <label>
        <h4>2. Enter the name of your term and a definition for it. (both are required)</h4>
    </label>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'label', 'error')} required">
    <label class="control-label" for="label">
       <strong><g:message code="termCommand.label.label"/></strong>
        <span class="required-indicator">*</span>
    </label>

    <div class="controls">
        <g:textField id="termLabelId" name="label" maxlength="${bard.db.dictionary.Element.LABEL_MAX_SIZE}" value="${termCommand?.label}" class="lowercase" onblur="trimText(this)"/>
        <span class="help-inline"><g:fieldError field="label" bean="${termCommand}"/></span>
    </div>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'description', 'error')} required">
    <label class="control-label" for="description">
        <strong><g:message code="termCommand.description.label"/></strong>
        <span class="required-indicator">*</span>
    </label>

    <div class="controls">
        <g:textArea id="termDescriptionId" name="description" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}" value="${termCommand?.description}" />
        <span class="help-inline"><g:fieldError field="description" bean="${termCommand}"/></span>
    </div>
</div>
<div class="control-group">
    <label>
        <h4>3. Enter additional optional information about your term.</h4>
    </label>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'abbreviation', 'error')}">
    <label class="control-label" for="abbreviation">
        <strong><g:message code="termCommand.abbreviation.label"/> </strong>
    </label>

    <div class="controls">
        <g:textField id="abbrvId" name="abbreviation" maxlength="${bard.db.dictionary.Element.ABBREVIATION_MAX_SIZE}" value="${termCommand?.abbreviation}" onblur="trimText(this)"/>
        <span class="help-inline"><g:fieldError field="abbreviation" bean="${termCommand}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: termCommand, field: 'synonyms', 'error')}">
    <label class="control-label" for="synonyms">
        <strong><g:message code="termCommand.synonyms.label"/></strong>
    </label>

    <div class="controls">
        <g:textArea id="synonymsId" name="synonyms" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.SYNONYMS_MAX_SIZE}" value="${termCommand?.synonyms}" class="lowercase" onblur="trimText(this)"/>
        <span class="help-inline"><g:fieldError field="synonyms" bean="${termCommand}"/></span>
    </div>
</div>
<div class="control-group">
    <label>
        <h4>4. Explain why you need to add this term.</h4>
    </label>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'curationNotes', 'error')} required">
    <label class="control-label" for="curationNotes">
        <strong><g:message code="termCommand.curationNotes.label"/></strong>
        <span class="required-indicator">*</span>
    </label>

    <div class="controls">
        <g:textArea id="curationNotesId" name="curationNotes" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}" value="${termCommand?.curationNotes}" onblur="trimText(this)"/>
        <span class="help-inline"><g:fieldError field="curationNotes" bean="${termCommand}"/></span>
    </div>
</div>
