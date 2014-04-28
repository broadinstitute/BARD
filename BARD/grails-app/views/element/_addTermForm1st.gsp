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
<h3>Propose New Term (page 1 of 2)</h3>

<div class="control-group">
    <label>
        <h4>1. Select a parent for your new term:</h4>
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
