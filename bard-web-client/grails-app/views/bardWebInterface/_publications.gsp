<section id="publication-info">
    <div class="page-header">
        <h3>Publications(${documents.size()})</h3>
    </div>
    <g:each in="${documents}" var="document">
        <dl>
            <dt>Title: ${document?.title}</dt>
            <dd><g:textBlock>Abstract: ${document.abs}</g:textBlock></dd>
            <dd>DOI: ${document.doi}</dd>
            <dd>PubMed Identifier: <a
                    href="http://www.ncbi.nlm.nih.gov/m/pubmed/${document.pubmedId}">${document.pubmedId}</a>
            </dd>
        </dl>
    </g:each>
</section>