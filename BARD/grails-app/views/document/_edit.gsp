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
<div class="well well-small">
    <div>
        <g:form action="delete" controller="document" params="${[type:document.owner.class.simpleName]}" id="${document.id}" onsubmit="return confirm('Are you sure you wish to delete the document?');">
            <g:link  action="edit" controller="document" class="btn" params="${[type:document.owner.class.simpleName]}" id="${document.id}" elementId="document-${document.id}">Edit</g:link>
            <input type="submit" value="Delete" class="btn" >
        </g:form>
    </div>

    <div>
        <dl class="dl-horizontal">
            <dt><g:message code="document.name.label" default="Name:"/></dt>
            <dd><g:fieldValue bean="${document}" field="documentName"/></dd>

            <dt><g:message code="document.content.label" default="Content:"/></dt>
            <dd>
                <g:if test="${document.documentType in [DocumentType.DOCUMENT_TYPE_EXTERNAL_URL,DocumentType.DOCUMENT_TYPE_PUBLICATION]}">
                    <a href="${document.documentContent}">
                        <g:fieldValue bean="${document}" field="documentContent"/>
                    </a>
                </g:if>
                <g:else>
                    <g:renderWithBreaks text="${document.documentContent}"/>
                </g:else>
            </dd>
        </dl>
    </div>
</div>
