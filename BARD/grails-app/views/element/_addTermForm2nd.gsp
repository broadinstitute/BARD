%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%@ page import="bard.db.dictionary.*" %>
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': termCommand?.errors?.allErrors]"/>
<h3>Propose New Term (page 2 of 2)</h3>
<div class="control-group ${hasErrors(bean: termCommand, field: 'parentLabel', 'error')} required">
    <label class="control-label" for="parentLabel">
        <strong><g:message code="termCommand.parentLabel.label"/></strong>
    </label>

    <div class="controls">
        <g:textField id="parentLabel" name="parentLabel" maxlength="${bard.db.dictionary.Element.LABEL_MAX_SIZE}" value="${termCommand?.parentLabel}" class="lowercase" onblur="trimText(this)" disabled="true"/>
        <span class="help-inline"><g:fieldError field="parentLabel" bean="${termCommand}"/></span>
    </div>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'parentDescription', 'error')} required">
    <label class="control-label" for="parentDescription">
        <strong><g:message code="termCommand.parentDescription.label"/></strong>
    </label>

    <div class="controls">
        <g:textField id="parentDescription" name="parentDescription" maxlength="${bard.db.dictionary.Element.LABEL_MAX_SIZE}" value="${termCommand?.parentDescription}" class="lowercase" onblur="trimText(this)" disabled="true"/>
        <span class="help-inline"><g:fieldError field="parentDescription" bean="${termCommand}"/></span>
    </div>
</div>
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
        <g:textField id="termLabelId" name="label" maxlength="${bard.db.dictionary.Element.LABEL_MAX_SIZE}" value="${termCommand?.label}" class="lowercase" onblur="trimText(this)" disabled="${termCommand?.success}"/>
        <span class="help-inline"><g:fieldError field="label" bean="${termCommand}"/></span>
    </div>
</div>
<div class="control-group ${hasErrors(bean: termCommand, field: 'description', 'error')} required">
    <label class="control-label" for="description">
        <strong><g:message code="termCommand.description.label"/></strong>
        <span class="required-indicator">*</span>
    </label>

    <div class="controls">
        <g:textArea id="termDescriptionId" name="description" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}" value="${termCommand?.description}" disabled="${termCommand?.success}"/>
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
        <g:textField id="abbrvId" name="abbreviation" maxlength="${bard.db.dictionary.Element.ABBREVIATION_MAX_SIZE}" value="${termCommand?.abbreviation}" onblur="trimText(this)" disabled="${termCommand?.success}"/>
        <span class="help-inline"><g:fieldError field="abbreviation" bean="${termCommand}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: termCommand, field: 'synonyms', 'error')}">
    <label class="control-label" for="synonyms">
        <strong><g:message code="termCommand.synonyms.label"/></strong>
    </label>

    <div class="controls">
        <g:textArea id="synonymsId" name="synonyms" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.SYNONYMS_MAX_SIZE}" value="${termCommand?.synonyms}" class="lowercase" onblur="trimText(this)" disabled="${termCommand?.success}"/>
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
        <g:textArea id="curationNotesId" name="curationNotes" cols="40" rows="5" maxlength="${bard.db.dictionary.Element.DESCRIPTION_MAX_SIZE}" value="${termCommand?.curationNotes}" onblur="trimText(this)" disabled="${termCommand?.success}"/>
        <span class="help-inline"><g:fieldError field="curationNotes" bean="${termCommand}"/></span>
    </div>
</div>
