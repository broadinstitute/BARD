                    <dt><g:message code="document.content.label" default="Name:"/></dt>
                    <dd><g:fieldValue bean="${document}" field="documentName"/></dd>

                    <dt><g:message code="document.content.label" default="Content:"/></dt>
                    <dd>
                        <g:if test="${ document.documentType in [bard.db.model.IDocumentType.DOCUMENT_TYPE_EXTERNAL_URL, bard.db.model.IDocumentType.DOCUMENT_TYPE_PUBLICATION]}">
                            <a href="${document.documentContent}">
                                <g:fieldValue bean="${document}" field="documentContent"/>
                            </a>
                        </g:if>
                        <g:else>
                            <g:renderWithBreaks text="${document.documentContent}"/>
                        </g:else>
                    </dd>
                    <br/>
