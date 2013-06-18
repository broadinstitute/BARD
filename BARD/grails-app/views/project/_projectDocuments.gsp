<%@ page import="bard.db.registration.DocumentKind; bard.db.enums.DocumentType" %>
<section id="documents-header">
    <div class="page-header">
        <h3>4. Documents</h3>
    </div>
</section>
<section id="documents-description-header">
    <div class="page-header">
        <h4>4.1 Descriptions</h4>
    </div>

    <div class="row-fluid">
        <div id="descriptionMsg"></div><br/>
        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION, label: 'Add New Description']"/>
        <g:each in="${project.descriptions}" var="description">
            <div class="borderlist">
                <br/>
                Document Name: <a href="#" data-type="text"
                                  data-pk="${description.id}"
                                  data-toggle="manual"
                                  class="documents ${description.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${description.documentType.id}"
                                  data-name="${DocumentKind.ProjectDocument}"
                                  data-version="${description.version}"
                                  data-owningEntityId="${project.id}"
                                  data-inputclass="input-xxlarge"
                                  data-document-name="${description.documentContent}"
                                  data-server-response-id="descriptionMsg"
                                  id="${description.id}_Name">
                <g:fieldValue bean="${description}" field="documentName"/>
            </a> <i class="icon-pencil documentPencil" data-id="${description.id}_Name" title="Click to edit name"></i>
                <br/>
                <br/>

                <div class="controls">
                    <g:textArea class="span10 ${description.id} richtext" id="${description.id}"
                                name="${description.id}"
                                data-documentType="${description.documentType.id}"
                                data-documentKind="${DocumentKind.ProjectDocument}"
                                data-version="${description.version}"
                                data-owningEntityId="${project.id}"
                                data-toggle="manual"
                                data-server-response-id="descriptionMsg"
                                data-document-name="${description.documentName}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: description.documentContent]"/>
                    </g:textArea>
                    <g:render template="deleteDocumentForm" model="[document: description]"/>
                </div>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-protocol-header">
    <div class="page-header">
        <h4>4.2 Protocols</h4>
    </div>

    <div class="row-fluid">
        <div id="protocolMsg"></div>

        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, label: 'Add New Protocol']"/>
        <g:each in="${project.protocols}" var="protocol">
            <div class="borderlist">
                <br/>
                Document Name: <a href="#" data-type="text"
                                  data-pk="${protocol.id}"
                                  class="documents ${protocol.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${protocol.documentType.id}"
                                  data-name="${DocumentKind.ProjectDocument}"
                                  data-version="${protocol.version}"
                                  data-owningEntityId="${protocol.id}"
                                  data-inputclass="input-xxlarge"
                                  data-toggle="manual"
                                  data-document-name="${protocol.documentContent}"
                                  data-server-response-id="protocolMsg"
                                  id="${protocol.id}_Name">
                <g:fieldValue bean="${protocol}" field="documentName"/>

            </a>
                <i class="icon-pencil documentPencil" data-id="${protocol.id}_Name" title="Click to edit name"></i>
                <br/>
                <br/>

                <div class="controls">
                    <g:textArea class="span10 richtext ${protocol.id}" id="${protocol.id}"
                                name="${protocol.id}"
                                data-documentType="${protocol.documentType.id}"
                                data-documentKind="${DocumentKind.ProjectDocument}"
                                data-version="${protocol.version}"
                                data-owningEntityId="${project.id}"
                                data-server-response-id="protocolMsg"
                                data-document-name="${protocol.documentName}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: protocol.documentContent]"/>
                    </g:textArea>
                    <g:render template="deleteDocumentForm" model="[document: protocol]"/>
                </div>
            </div>
        </g:each>

    %{--<g:render template="addDocumentLink"--}%
    %{--model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, label: 'Add New Protocol']"/>--}%
    %{--<g:each in="${project.protocols}" var="protocol">--}%
    %{--<div class="borderlist">--}%
    %{--<a href="#" data-type="wysihtml5"--}%
    %{--data-pk="${protocol.id}" data-rows="60"--}%
    %{--data-inputclass="document-wysihtml5"--}%
    %{--class="documents ${protocol.id}"--}%
    %{--data-url="/BARD/document/editDocument"--}%
    %{--data-documentType="${protocol.documentType.id}"--}%
    %{--data-name="${DocumentKind.ProjectDocument}"--}%
    %{--data-version="${protocol.version}"--}%
    %{--data-owningEntityId="${project.id}"--}%
    %{--data-document-name="${protocol.documentName}"--}%
    %{--id="${protocol.id}">--}%
    %{--<g:render template="../document/docsWithLineBreaks"--}%
    %{--model="[documentContent: protocol.documentContent]"/>--}%
    %{--</a>--}%
    %{--<g:render template="deleteDocumentForm" model="[document: protocol]"/>--}%
    %{--</div>--}%
    %{--</g:each>--}%
    </div>
</section>
<section id="documents-comment-header">
    <div class="page-header">
        <h4>4.3 Comments</h4>
    </div>

    <div class="row-fluid">
        <div id="commentsMsg"></div>
        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_COMMENTS, label: 'Add New Comment']"/>

        <g:each in="${project.comments}" var="comment">
            <div class="borderlist">
                <br/>
                Document Name: <a href="#" data-type="text"
                                  data-pk="${comment.id}"
                                  data-server-response-id="commentsMsg"
                                  class="documents ${comment.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${comment.documentType.id}"
                                  data-name="${DocumentKind.ProjectDocument}"
                                  data-version="${comment.version}"
                                  data-owningEntityId="${comment.id}"
                                  data-inputclass="input-xxlarge"
                                  data-toggle="manual"
                                  data-document-name="${comment.documentContent}"
                                  id="${comment.id}_Name">
                <g:fieldValue bean="${comment}" field="documentName"/>

            </a>
                <i class="icon-pencil documentPencil" data-id="${comment.id}_Name" title="Click to edit name"></i>
                <br/>
                <br/>

                <div class="controls">
                    <g:textArea class="span10 richtext ${comment.id}" id="${comment.id}"
                                name="${comment.id}"
                                data-server-response-id="commentsMsg"
                                data-documentType="${comment.documentType.id}"
                                data-documentKind="${DocumentKind.ProjectDocument}"
                                data-version="${comment.version}"
                                data-owningEntityId="${project.id}"
                                data-document-name="${comment.documentName}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: comment.documentContent]"/>
                    </g:textArea>
                    <g:render template="deleteDocumentForm" model="[document: comment]"/>
                </div>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-publication-header">
    <div class="page-header">
        <h4>4.4 Publications</h4>
    </div>

    <div class="row-fluid">
        <div id="publicationMsg"></div>
        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_PUBLICATION, label: 'Add New Publication']"/>

        <g:each in="${project.publications}" var="publication">
            <div class="borderlist">
                Publication Name: <a href="#" data-type="text"
                                     data-pk="${publication.id}"
                                     data-server-response-id="publicationMsg"
                                     class="documents ${publication.id}"
                                     data-url="/BARD/document/editDocumentName"
                                     data-documentType="${publication.documentType.id}"
                                     data-name="${DocumentKind.ProjectDocument}"
                                     data-version="${publication.version}"
                                     data-toggle="manual"
                                     data-owningEntityId="${project.id}"
                                     data-inputclass="input-xxlarge"
                                     data-document-name="${publication.documentContent}"
                                     id="${publication.id}_Name">
                <g:fieldValue bean="${publication}" field="documentName"/>

            </a> <i class="icon-pencil documentPencil" data-id="${publication.id}_Name"
                    title="Click to edit name"></i>
                <br/>
                Publication URL:<a href="${publication.documentContent}" data-type="url"
                                   data-pk="${publication.id}"
                                   target="publicationURL"
                                   data-server-response-id="publicationMsg"
                                   class="documents ${publication.id}"
                                   data-toggle="manual"
                                   data-url="/BARD/document/editDocument"
                                   data-documentType="${publication.documentType.id}"
                                   data-name="${DocumentKind.ProjectDocument}"
                                   data-version="${publication.version}"
                                   data-owningEntityId="${project.id}"
                                   data-inputclass="input-xxlarge"
                                   data-document-name="${publication.documentName}"
                                   id="${publication.id}">
                <g:fieldValue bean="${publication}" field="documentContent"/>
            </a>
                <i class="icon-pencil documentPencil" data-id="${publication.id}" title="Click to edit URL"></i>
                <g:render template="deleteDocumentForm" model="[document: publication]"/>
            </div>

        </g:each>
    </div>
</section>
<section id="documents-urls-header">
    <div class="page-header">
        <h4>4.5 External URLS</h4>
    </div>

    <div class="row-fluid">
        <div id="externalURLMsg"></div>
        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_EXTERNAL_URL, label: 'Add New URL']"/>
        <g:each in="${project.externalURLs}" var="externalURL">
            <div class="borderlist">
                External Name: <a href="${externalURL.documentContent}" data-type="text"
                                  data-pk="${externalURL.id}"
                                  data-server-response-id="externalURLMsg"
                                  class="documents ${externalURL.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${externalURL.documentType.id}"
                                  data-name="${DocumentKind.ProjectDocument}"
                                  data-version="${externalURL.version}"
                                  data-toggle="manual"
                                  data-owningEntityId="${project.id}"
                                  data-inputclass="input-xxlarge"
                                  data-document-name="${externalURL.documentContent}"
                                  id="${externalURL.id}_Name">
                <g:fieldValue bean="${externalURL}" field="documentName"/>

            </a> <i class="icon-pencil documentPencil" data-id="${externalURL.id}_Name" title="Click to edit name"></i>
                <br/>
                Document URL: <a href="${externalURL.documentContent}"
                                 target="externalUrl"
                                 data-type="url"
                                 data-server-response-id="externalURLMsg"
                                 data-toggle="manual"
                                 data-pk="${externalURL.id}"
                                 class="documents ${externalURL.id}"
                                 data-url="/BARD/document/editDocument"
                                 data-documentType="${externalURL.documentType.id}"
                                 data-name="${DocumentKind.ProjectDocument}"
                                 data-version="${externalURL.version}"
                                 data-owningEntityId="${project.id}"
                                 data-inputclass="input-xxlarge"
                                 data-toggle="manual"
                                 data-document-name="${externalURL.documentName}" id="${externalURL.id}">
                <g:fieldValue bean="${externalURL}" field="documentContent"/>
            </a>
                <i class="icon-pencil documentPencil" data-id="${externalURL.id}" title="Click to edit name"></i>
                <g:render template="deleteDocumentForm" model="[document: externalURL]"/>
            </div>
        </g:each>
    </div>
</section>
<section id="documents-other-header">
    <div class="page-header">
        <h4>4.6 Others</h4>
    </div>

    <div class="row-fluid">
        <div id="otherMsg"></div>
        <br/>
        <g:render template="addDocumentLink"
                  model="[projectId: project.id, documentType: DocumentType.DOCUMENT_TYPE_OTHER, label: 'Add Other']"/>
        <g:each in="${project.otherDocuments}" var="otherDocument">
            <div class="borderlist">
                <br/>
                Document Name: <a href="#" data-type="text"
                                  data-pk="${otherDocument.id}"
                                  data-server-response-id="otherMsg"
                                  class="documents ${otherDocument.id}"
                                  data-url="/BARD/document/editDocumentName"
                                  data-documentType="${otherDocument.documentType.id}"
                                  data-name="${DocumentKind.ProjectDocument}"
                                  data-toggle="manual"
                                  data-version="${otherDocument.version}"
                                  data-owningEntityId="${otherDocument.id}"
                                  data-inputclass="input-xxlarge"
                                  data-document-name="${otherDocument.documentContent}"
                                  id="${otherDocument.id}_Name">
                <g:fieldValue bean="${otherDocument}" field="documentName"/>
            </a>
                <i class="icon-pencil documentPencil" data-id="${otherDocument.id}_Name" title="Click to edit name"></i>

                <br/>
                <br/>

                <div class="controls">
                    <g:textArea class="span10 richtext ${otherDocument.id}" id="${otherDocument.id}"
                                name="${otherDocument.id}"
                                data-documentType="${otherDocument.documentType.id}"
                                data-server-response-id="otherMsg"
                                data-documentKind="${DocumentKind.ProjectDocument}"
                                data-version="${otherDocument.version}"
                                data-owningEntityId="${project.id}"
                                data-document-name="${otherDocument.documentName}">
                        <g:render template="../document/docsWithLineBreaks"
                                  model="[documentContent: otherDocument.documentContent]"/>
                    </g:textArea>
                    <g:render template="deleteDocumentForm" model="[document: otherDocument]"/>
                </div>
            </div>
        </g:each>
    </div>
</section>