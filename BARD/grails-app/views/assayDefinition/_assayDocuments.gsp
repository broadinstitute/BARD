<%@ page import="bard.db.registration.DocumentKind; bard.db.enums.DocumentType; bard.db.registration.AssayDocument" %>
<section id="documents-header">
    <div class="page-header">
        <h3>5. Documents</h3>
    </div>
</section>
<section id="documents-description-header">
    <div class="page-header">
        <h4>5.1 Descriptions</h4>
    </div>

    <div class="row-fluid">
        <g:render template="addDocumentLink"
                  model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION, label: 'Add New Description']"/>
        <g:each in="${assay.descriptions}" var="description">
            <div class="borderlist">
                <a href="#" data-type="wysihtml5" title="Click to edit content"
                   data-pk="${description.id}" data-rows="60" data-inputclass="document-wysihtml5"
                   class="documents ${description.id}"
                   data-url="/BARD/document/editDocument"
                   data-documentType="${description.documentType.id}"
                   data-name="${DocumentKind.AssayDocument}"  data-version="${description.version}"
                   data-owningEntityId="${assay.id}"
                   data-document-name="${description.documentName}" id="${description.id}">
                    <g:render template="../document/docsWithLineBreaks"
                              model="[documentContent: description.documentContent]"/>
                </a>
                <g:render template="deleteDocumentForm" model="[document: description]"/>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-protocol-header">
    <div class="page-header">
        <h4>5.2 Protocols</h4>
    </div>

    <div class="row-fluid">
        <g:render template="addDocumentLink"
                  model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, label: 'Add New Protocol']"/>
        <g:each in="${assay.protocols}" var="protocol">
            <div class="borderlist">
                <a href="#" data-type="wysihtml5" title="Click to edit content"
                   data-pk="${protocol.id}" data-rows="60" data-inputclass="document-wysihtml5"
                   class="documents ${protocol.id}"
                   data-url="/BARD/document/editDocument"
                   data-documentType="${protocol.documentType.id}"
                   data-name="${DocumentKind.AssayDocument}" data-version="${protocol.version}"
                   data-owningEntityId="${assay.id}"
                   data-document-name="${protocol.documentName}" id="${protocol.id}">
                    <g:render template="../document/docsWithLineBreaks"
                              model="[documentContent: protocol.documentContent]"/>
                </a>
                <g:render template="deleteDocumentForm" model="[document: protocol]"/>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-comment-header">
    <div class="page-header">
        <h4>5.3 Comments</h4>
    </div>

    <div class="row-fluid">
        <g:render template="addDocumentLink"
                  model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_COMMENTS, label: 'Add New Comment']"/>

        <g:each in="${assay.comments}" var="comment">
            <div class="borderlist">
                <a href="#" data-type="wysihtml5"  title="Click to edit content"
                   data-pk="${comment.id}" data-rows="60" data-inputclass="document-wysihtml5"
                   class="documents ${comment.id}"
                   data-url="/BARD/document/editDocument"
                   data-documentType="${comment.documentType.id}"
                   data-name="${DocumentKind.AssayDocument}"  data-version="${comment.version}"
                   data-owningEntityId="${assay.id}"
                   data-document-name="${comment.documentName}" id="${comment.id}">
                    <g:render template="../document/docsWithLineBreaks"
                              model="[documentContent: comment.documentContent]"/>
                </a>
                <g:render template="deleteDocumentForm" model="[document: comment]"/>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-publication-header">
    <div class="page-header">
        <h4>5.4 Publications</h4>
    </div>

    <div class="row-fluid">
        <div class="row-fluid">
            <g:render template="addDocumentLink"
                      model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_PUBLICATION, label: 'Add New Publication']"/>
            <g:each in="${assay.publications}" var="publication">
                <div class="borderlist">
                    Publication Name: <a href="#" data-type="text"
                                         title="Click to edit name"
                                         data-pk="${publication.id}"
                                         class="documents ${publication.id}"
                                         data-url="/BARD/document/editDocumentName"
                                         data-documentType="${publication.documentType.id}"
                                         data-name="${DocumentKind.AssayDocument}"
                                         data-version="${publication.version}"
                                         data-owningEntityId="${assay.id}"
                                         data-inputclass="input-xxlarge"
                                         data-document-name="${publication.documentContent}"
                                         id="${publication.id}_Name">
                    <g:fieldValue bean="${publication}" field="documentName"/>
                </a>
                    <br/>
                    Publication URL:<a href="#" data-type="url" title="Click to edit URL"
                                       data-pk="${publication.id}"
                                       class="documents ${publication.id}"
                                       data-url="/BARD/document/editDocument"
                                       data-documentType="${publication.documentType.id}"
                                       data-name="${DocumentKind.AssayDocument}"
                                       data-version="${publication.version}"
                                       data-owningEntityId="${assay.id}"
                                       data-inputclass="input-xxlarge"
                                       data-document-name="${publication.documentName}"
                                       id="${publication.id}">
                    <g:fieldValue bean="${publication}" field="documentContent"/>
                </a>
                    <a href="${publication.documentContent}" target="publication" title="Click on globe icon to view URL"><i class="icon-globe"></i></a>
                    <g:render template="deleteDocumentForm" model="[document: publication]"/>
                </div>
            </g:each>
        </div>
    </div>
</section>
<section id="documents-urls-header">
    <div class="page-header">
        <h4>5.5 External URLS</h4>
    </div>

    <div class="row-fluid">
        <g:render template="addDocumentLink"
                  model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_EXTERNAL_URL, label: 'Add New URL']"/>

        <g:each in="${assay.externalURLs}" var="externalURL">
            <div class="borderlist">
                External Name: <a href="#" data-type="text" title="Click to edit name"
                                  data-pk="${externalURL.id}"
                                  class="documents ${externalURL.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${externalURL.documentType.id}"
                                  data-name="${DocumentKind.AssayDocument}"
                                  data-version="${externalURL.version}"
                                  data-owningEntityId="${assay.id}"
                                  data-inputclass="input-xxlarge"
                                  data-document-name="${externalURL.documentContent}"
                                  id="${externalURL.id}_Name">
                <g:fieldValue bean="${externalURL}" field="documentName"/>
            </a> <br/>
                Document URL: <a href="#" data-type="url" title="Click to edit URL"
                                 data-pk="${externalURL.id}"
                                 class="documents ${externalURL.id}"
                                 data-url="/BARD/document/editDocument"
                                 data-documentType="${externalURL.documentType.id}"
                                 data-name="${DocumentKind.AssayDocument}"
                                 data-version="${externalURL.version}"
                                 data-owningEntityId="${assay.id}"
                                 data-inputclass="input-xxlarge"
                                 data-document-name="${externalURL.documentName}" id="${externalURL.id}">
                <g:fieldValue bean="${externalURL}" field="documentContent"/>
            </a>
                <a href="${externalURL.documentContent}" target="externalUrl" title="Click on globe icon to view URL"><i class="icon-globe"></i></a>
                <g:render template="deleteDocumentForm" model="[document: externalURL]"/>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-other-header">
    <div class="page-header">
        <h4>5.6 Others</h4>
    </div>

    <div class="row-fluid">
        <g:render template="addDocumentLink"
                  model="[assayId: assay.id, documentType: DocumentType.DOCUMENT_TYPE_OTHER, label: 'Add Other']"/>
        <g:each in="${assay.otherDocuments}" var="otherDocument">
            <div class="borderlist">
                <a href="#" data-type="text"  title="Click to edit text"
                   data-pk="${otherDocument.id}"
                   class="documents ${otherDocument.id}"
                   data-url="/BARD/document/editDocument"
                   data-documentType="${otherDocument.documentType.id}"
                   data-name="${DocumentKind.AssayDocument}"  data-version="${otherDocument.version}"
                   data-owningEntityId="${assay.id}"
                   data-inputclass="input-xxlarge"
                   data-document-name="${otherDocument.documentName}" id="${otherDocument.id}">
                    <g:fieldValue bean="${otherDocument}" field="documentContent"/>
                </a>
                <g:render template="deleteDocumentForm" model="[document: otherDocument]"/>
            </div>
        </g:each>
    </div>
</section>