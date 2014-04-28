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

<%@ page import="bard.db.registration.DocumentKind; bard.db.enums.DocumentType" %>
<section id="documents-header">
<div class="page-header">
<h3 class="sect">Documents</h3>

<section id="documents-description-header">
    <h4 class="subsect">Descriptions</h4>
    <div class="row-fluid">
        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION, label: 'Add New Description']"/>
        </g:if>
        <g:each in="${owningEntity.descriptions}" var="description">
            <div class="borderlist" id="document-${description.id}">
                <br/>

                <div id="descriptionMsg_${description.id}"></div><br/>
                <b>Document Name:</b>
                <span><g:fieldValue bean="${description}" field="documentName"/></span>
                <br/>
                <b>Document Content:</b>
                <br/>
                <g:render template="../document/docsWithLineBreaks" model="[documentContent: description.documentContent]"/>
                <g:if test="${editable == 'canedit'}">
                    <g:render template="/document/deleteDocumentForm" model="[document: description]"/>
                </g:if>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-protocol-header">
    <h4 class="subsect">Protocols</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, label: 'Add New Protocol']"/>
        </g:if>
        <g:each in="${owningEntity.protocols}" var="protocol">
            <div class="borderlist" id="document-${protocol.id}">
                <div id="protocolMsg_${protocol.id}"></div><br/>
                <b>Document Name:</b>
                <span><g:fieldValue bean="${protocol}" field="documentName"/></span>
                <div class="controls">
                    <b>Document Content:</b>
                    <br/>
                    <span>
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: protocol.documentContent]"/>
                    </span>
                    <g:if test="${editable == 'canedit'}">
                        <g:render template="/document/deleteDocumentForm" model="[document: protocol]"/>
                    </g:if>
                </div>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-comment-header">
    <h4 class="subsect">Comments</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_COMMENTS, label: 'Add New Comment']"/>
        </g:if>
        <g:each in="${owningEntity.comments}" var="comment">
            <div class="borderlist" id="document-${comment.id}">
                <br/>

                <div id="commentsMsg_${comment.id}"></div>
                <br/>
                <b>Document Name:</b> <span><g:fieldValue bean="${comment}" field="documentName"/></span>
                <br/>
                <br/>
                <div class="controls">
                    <b>Document Content</b>
                    <br/>
                    <span >
                        <g:render template="../document/docsWithLineBreaks" model="[documentContent: comment.documentContent]"/>
                    </span>
                    <g:if test="${editable == 'canedit'}">
                        <g:render template="/document/deleteDocumentForm" model="[document: comment]"/>
                    </g:if>
                </div>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-publication-header">
    <h4 class="subsect">Publications</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_PUBLICATION, label: 'Add New Publication']"/>
        </g:if>
        <g:each in="${owningEntity.publications}" var="publication">
            <div class="borderlist"><br/>

                <div id="publicationMsg_${publication}"></div><br/>
                <br/>
                <b>Publication Name:</b> <span><g:fieldValue bean="${publication}" field="documentName"/></span>
                <br/>
                 <b>Publication URL:</b>
                <a href="${publication.documentContent}">${publication.documentContent}</a>
                <g:if test="${editable == 'canedit'}">
                    <g:render template="/document/deleteDocumentForm" model="[document: publication]"/>
                </g:if>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-urls-header">
    <h4 class="subsect">External URLS</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_EXTERNAL_URL, label: 'Add New URL']"/>
        </g:if>
        <g:each in="${owningEntity.externalURLs}" var="externalURL">
            <div class="borderlist">
                <br/>

                <div id="externalURLMsg_${externalURL.id}"></div><br/>
                <b>External Name:</b> <span data-type="text"
                                            data-pk="${externalURL.id}"
                                            data-server-response-id="externalURLMsg_${externalURL.id}"
                                            class="documents ${externalURL.id}"
                                            data-url="${request.contextPath}/document/editDocumentName"
                                            data-documentType="${externalURL.documentType.id}"
                                            data-documentKind="${documentKind}"
                                            data-version="${externalURL.version}"
                                            data-toggle="manual"
                                            data-owningEntityId="${owningEntity.id}"
                                            data-inputclass="input-xxlarge"
                                            data-document-name="${externalURL.documentContent}"
                                            id="${externalURL.id}_Name">
                <g:fieldValue bean="${externalURL}" field="documentName"/>
            </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${externalURL.id}_Name"
                   title="Click to edit name">
                </a>
                <br/>
                <b>Document URL:</b>
                <g:if test="${externalURL.documentContent}">
                    <a href="${externalURL.documentContent}"
                       target="externalUrl"
                       data-type="url"
                       data-server-response-id="externalURLMsg_${externalURL.id}"
                       data-toggle="manual"
                       data-pk="${externalURL.id}"
                       class="documents ${externalURL.id}"
                       data-url="${request.contextPath}/document/editDocument"
                       data-documentType="${externalURL.documentType.id}"
                       data-documentKind="${documentKind}"
                       data-version="${externalURL.version}"
                       data-owningEntityId="${owningEntity.id}"
                       data-inputclass="input-xxlarge"
                       data-toggle="manual"
                       data-document-name="${externalURL.documentName}" id="${externalURL.id}_URL">
                        <g:fieldValue bean="${externalURL}" field="documentContent"/>
                    </a>
                </g:if>
                <g:else>
                    <span
                            target="externalUrl"
                            data-type="url"
                            data-server-response-id="externalURLMsg_${externalURL.id}"
                            data-toggle="manual"
                            data-pk="${externalURL.id}"
                            class="documents ${externalURL.id}"
                            data-url="${request.contextPath}/document/editDocument"
                            data-documentType="${externalURL.documentType.id}"
                            data-documentKind="${documentKind}"
                            data-version="${externalURL.version}"
                            data-owningEntityId="${owningEntity.id}"
                            data-inputclass="input-xxlarge"
                            data-toggle="manual"
                            data-document-name="${externalURL.documentName}" id="${externalURL.id}_URL">
                        <g:fieldValue bean="${externalURL}" field="documentContent"/>
                    </span>
                </g:else>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${externalURL.id}_URL"
                   title="Click to edit name">
                </a>

                <g:if test="${editable == 'canedit'}">
                    <g:render template="/document/deleteDocumentForm" model="[document: externalURL]"/>
                </g:if>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-other-header">
    <h4 class="subsect">Others</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_OTHER, label: 'Add Other']"/>
        </g:if>
        <g:each in="${owningEntity.otherDocuments}" var="otherDocument">
            <div class="borderlist" id="document-${otherDocument.id}">
                <br/>

                <div id="otherMsg_${otherDocument.id}"></div>
                <br/>
                <b>Document Name:</b>
                <span><g:fieldValue bean="${otherDocument}" field="documentName"/></span>
                <br/>
                <br/>

                <div class="controls">
                    <b>Document Content</b>
                    <br/>
                    <span>
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: otherDocument.documentContent]"/>
                    </span>
                    <g:if test="${editable == 'canedit'}">
                        <g:render template="/document/deleteDocumentForm" model="[document: otherDocument]"/>
                    </g:if>
                </div>
            </div>
        </g:each>
    </div>
</section>
</div>
</section>
