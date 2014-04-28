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
        <g:textArea id="documentName" name="documentName" value="${document.documentName}" required="" class="span8 input-xxlarge"/>
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
