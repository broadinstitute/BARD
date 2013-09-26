<%@ page import="bard.db.registration.DocumentKind; bard.db.enums.DocumentType" %>
<section id="documents-header">
<div class="page-header">
<h3 class="sect">Documents</h3>
<g:if test="${editable == 'canedit'}">
    %{--<h5>Use the 'save icon' to persist any changes you make on the Rich Text Editor</h5>--}%
</g:if>
<section id="documents-description-header">
    <h4 class="subsect">Descriptions</h4>

    <div class="row-fluid">

        <g:if test="${editable == 'canedit'}">
            <g:render template="/document/addDocumentLink"
                      model="[owningEntityId: owningEntity.id, documentKind: documentKind, documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION, label: 'Add New Description']"/>
        </g:if>
        <g:each in="${owningEntity.descriptions}" var="description">
            <div class="borderlist">
                <br/>

                <div id="descriptionMsg_${description.id}"></div><br/>
                <b>Document Name:</b>
                <span data-type="text"
                      data-pk="${description.id}"
                      data-toggle="manual"
                      class="documents ${description.id}"
                      data-url="/BARD/document/editDocumentName"
                      data-documentType="${description.documentType.id}"
                      data-documentKind="${documentKind}"
                      data-version="${description.version}"
                      data-owningEntityId="${owningEntity.id}"
                      data-inputclass="input-xxlarge"
                      data-document-name="${description.documentContent}"
                      data-server-response-id="descriptionMsg_${description.id}"
                      id="${description.id}_Name">
                    <g:fieldValue bean="${description}" field="documentName"/>
                </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${description.id}_Name"
                   title="Click to edit name">
                </a>
                <br/>
                <div class="controls">
                    <b>Document Content</b> <a href="#" class="icon-pencil documentPencil ${editable}"
                                               data-id="description_${description.id}"
                                               title="Click to edit description"></a> <br/>
                    <span data-type="textarea"
                          name="${description.id}"
                          data-pk="${description.id}"
                          data-toggle="manual"
                          class="textarea ${description.id}"
                          data-url="/BARD/document/editDocument"
                          data-documentType="${description.documentType.id}"
                          data-documentKind="${documentKind}"
                          data-version="${description.version}"
                          data-owningEntityId="${owningEntity.id}"
                          data-document-name="${description.documentName}"
                          data-server-response-id="descriptionMsg_${description.id}"
                          data-inputclass="input-xxlarge"
                          id="description_${description.id}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: description.documentContent]"/>
                    </span>

                    <g:if test="${editable == 'canedit'}">
                        <g:render template="/document/deleteDocumentForm" model="[document: description]"/>
                    </g:if>
                </div>
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
            <div class="borderlist">
                <div id="protocolMsg_${protocol.id}"></div><br/>
                <b>Document Name:</b>
                <span data-type="text"
                      data-pk="${protocol.id}"
                      class="documents ${protocol.id}"
                      data-url="/BARD/document/editDocumentName"
                      data-documentType="${protocol.documentType.id}"
                      data-documentKind="${documentKind}"
                      data-version="${protocol.version}"
                      data-owningEntityId="${owningEntity.id}"
                      data-inputclass="input-xxlarge"
                      data-toggle="manual"
                      data-document-name="${protocol.documentContent}"
                      data-server-response-id="protocolMsg_${protocol.id}"
                      id="${protocol.id}_Name">
                    <g:fieldValue bean="${protocol}" field="documentName"/>
                </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${protocol.id}_Name"
                   title="Click to edit name">
                </a>

                <div class="controls">
                    <b>Document Content</b> <a href="#" class="icon-pencil documentPencil ${editable}"
                                               data-id="protocol_${protocol.id}"
                                               title="Click to edit protocol"></a> <br/>
                    <span data-type="textarea"
                          name="${protocol.id}"
                          data-pk="${protocol.id}"
                          data-toggle="manual"
                          class="textarea ${protocol.id}"
                          data-url="/BARD/document/editDocument"
                          data-documentType="${protocol.documentType.id}"
                          data-documentKind="${documentKind}"
                          data-version="${protocol.version}"
                          data-owningEntityId="${owningEntity.id}"
                          data-document-name="${protocol.documentName}"
                          data-server-response-id="protocolMsg_${protocol.id}"
                          data-inputclass="input-xxlarge"
                          id="protocol_${protocol.id}">
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
            <div class="borderlist">
                <br/>

                <div id="commentsMsg_${comment.id}"></div>
                <br/>
                <b>Document Name:</b> <span data-type="text"
                                            data-pk="${comment.id}"
                                            data-server-response-id="commentsMsg_${comment.id}"
                                            class="documents ${comment.id}"
                                            data-url="/BARD/document/editDocumentName"
                                            data-documentType="${comment.documentType.id}"
                                            data-documentKind="${documentKind}"
                                            data-version="${comment.version}"
                                            data-owningEntityId="${owningEntity.id}"
                                            data-inputclass="input-xxlarge"
                                            data-toggle="manual"
                                            data-document-name="${comment.documentContent}"
                                            id="${comment.id}_Name">
                <g:fieldValue bean="${comment}" field="documentName"/>
            </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${comment.id}_Name"
                   title="Click to edit name">
                </a>

                <br/>
                <br/>

                <div class="controls">

                    <b>Document Content</b> <a href="#" class="icon-pencil documentPencil ${editable}"
                                               data-id="comment_${comment.id}"
                                               title="Click to edit comment"></a> <br/>
                    <span data-type="textarea"
                          name="${comment.id}"
                          data-pk="${comment.id}"
                          data-toggle="manual"
                          class="textarea ${comment.id}"
                          data-url="/BARD/document/editDocument"
                          data-documentType="${comment.documentType.id}"
                          data-documentKind="${documentKind}"
                          data-version="${comment.version}"
                          data-owningEntityId="${owningEntity.id}"
                          data-document-name="${comment.documentName}"
                          data-server-response-id="commentsMsg_${comment.id}"
                          data-inputclass="input-xxlarge"
                          id="comment_${comment.id}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: comment.documentContent]"/>
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
                <b>Publication Name:</b> <span data-type="text"
                                               data-pk="${publication.id}"
                                               data-server-response-id="publicationMsg_${publication}"
                                               class="documents ${publication.id}"
                                               data-url="/BARD/document/editDocumentName"
                                               data-documentType="${publication.documentType.id}"
                                               data-documentKind="${documentKind}"
                                               data-version="${publication.version}"
                                               data-toggle="manual"
                                               data-owningEntityId="${owningEntity.id}"
                                               data-inputclass="input-xxlarge"
                                               data-document-name="${publication.documentContent}"
                                               id="${publication.id}_Name">
                <g:fieldValue bean="${publication}" field="documentName"/>
            </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${publication.id}_Name"
                   title="Click to edit name">
                </a>
                <br/>
                <b>Publication URL:</b>
                <g:if test="${publication.documentContent}">
                    <a href="${publication.documentContent}"
                       data-type="url"
                       data-pk="${publication.id}"
                       target="publicationURL"
                       data-server-response-id="publicationMsg_${publication}"
                       class="documents ${publication.id}"
                       data-toggle="manual"
                       data-url="/BARD/document/editDocument"
                       data-documentType="${publication.documentType.id}"
                       data-documentKind="${documentKind}"
                       data-version="${publication.version}"
                       data-owningEntityId="${owningEntity.id}"
                       data-inputclass="input-xxlarge"
                       data-document-name="${publication.documentName}"
                       id="${publication.id}_URL">
                        <g:fieldValue bean="${publication}" field="documentContent"/>
                    </a>
                </g:if>
                <g:else>
                    <span data-type="url"
                          data-pk="${publication.id}"
                          target="publicationURL"
                          data-server-response-id="publicationMsg_${publication}"
                          class="documents ${publication.id}"
                          data-toggle="manual"
                          data-url="/BARD/document/editDocument"
                          data-documentType="${publication.documentType.id}"
                          data-documentKind="${documentKind}"
                          data-version="${publication.version}"
                          data-owningEntityId="${owningEntity.id}"
                          data-inputclass="input-xxlarge"
                          data-document-name="${publication.documentName}"
                          id="${publication.id}_URL">
                        <g:fieldValue bean="${publication}" field="documentContent"/>
                    </span>
                </g:else>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${publication.id}_URL"
                   title="Click to edit name">
                </a>
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
                                            data-url="/BARD/document/editDocumentName"
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
                       data-url="/BARD/document/editDocument"
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
                            data-url="/BARD/document/editDocument"
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
            <div class="borderlist">
                <br/>

                <div id="otherMsg_${otherDocument.id}"></div>
                <br/>
                <b>Document Name:</b>
                <span data-type="text"
                      data-pk="${otherDocument.id}"
                      data-server-response-id="otherMsg_${otherDocument.id}"
                      class="documents ${otherDocument.id}"
                      data-url="/BARD/document/editDocumentName"
                      data-documentType="${otherDocument.documentType.id}"
                      data-documentKind="${documentKind}"
                      data-toggle="manual"
                      data-version="${otherDocument.version}"
                      data-owningEntityId="${owningEntity.id}"
                      data-inputclass="input-xxlarge"
                      data-document-name="${otherDocument.documentContent}"
                      id="${otherDocument.id}_Name">
                    <g:fieldValue bean="${otherDocument}" field="documentName"/>
                </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="${otherDocument.id}_Name"
                   title="Click to edit name">
                </a>
                <br/>
                <br/>

                <div class="controls">
                    <b>Document Content</b> <a href="#" class="icon-pencil documentPencil ${editable}"
                                               data-id="other_${other.id}"
                                               title="Click to edit other"></a> <br/>
                    <span data-type="textarea"
                          name="${other.id}"
                          data-pk="${other.id}"
                          data-toggle="manual"
                          class="textarea ${other.id}"
                          data-url="/BARD/document/editDocument"
                          data-documentType="${other.documentType.id}"
                          data-documentKind="${documentKind}"
                          data-version="${other.version}"
                          data-owningEntityId="${owningEntity.id}"
                          data-document-name="${other.documentName}"
                          data-server-response-id="otherMsg_${other.id}"
                          data-inputclass="input-xxlarge"
                          id="other_${other.id}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: other.documentContent]"/>
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
