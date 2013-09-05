<section id="documents-header">
    <h3>6. Documents</h3>

    <section id="documents-description-header">
        <h4>6.1 Descriptions</h4>

        <div class="row-fluid">
            <div class="borderlist">
                <g:if test="${assayAdapter?.description}">
                    <g:textBlock>${assayAdapter?.description}</g:textBlock>
                </g:if>
                <g:else>
                    None
                </g:else>
            </div>
        </div>
    </section>

    <section id="documents-protocol-header">
        <h4>6.2 Protocols</h4>

        <div class="row-fluid">
            <g:if test="${assayAdapter?.protocol}">
                <g:textBlock>${assayAdapter?.protocol}</g:textBlock>
            </g:if>
            <g:else>
                None
            </g:else>
        </div>
    </section>

    <section id="documents-comment-header">
        <h4>6.3 Comments</h4>

        <div class="row-fluid">
            <g:if test="${assayAdapter?.comments}">
                <g:textBlock>${assayAdapter?.comments}</g:textBlock>
            </g:if>
            <g:else>
                None
            </g:else>
        </div>
    </section>

    <section id="documents-publication-header">
        <h4>6.4 Publications</h4>

        <ol>
            <g:each in="${assayAdapter?.annotations?.findPublications()}" var="publication">
                <li><a href="${publication.urlValue}" target="_blank">${publication.displayString}</a></li>
            </g:each>
        </ol>
    </section>

    <section id="documents-urls-header">
        <h4>6.5 External URLS</h4>

        <ul>
            <g:each in="${assayAdapter?.annotations?.findExternalUrls()}" var="externalUrl">
                <li><a href="${externalUrl.urlValue}" target="_blank">${externalUrl.urlValue}</a></li>
            </g:each>
        </ul>

    </section>
</section>
