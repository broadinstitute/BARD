<section id="target-info">
    <div class="page-header">
        <h1>Targets (${targets.size()})</h1>
    </div>

    <g:each in="${targets}" var="target">
        <dl>
            <dt>Name: ${target.name}</dt>
            <dd><g:textBlock>Description: ${target.description}</g:textBlock></dd>
            <dd>Status: ${target.status}</dd>
            <dd>Gene ID: ${target.geneId}</dd>
            <dd>Taxonomic Identifier: <a href="http://www.uniprot.org/taxonomy/${target.taxId}" target="_blank">
                <img src="${resource(dir: 'images', file: 'uniprot_logo.gif')}" alt="Taxonomy"
                     title="Taxonomy"/></a>
                <a href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?lvl=0&id=${target.taxId}"
                   target="_blank">
                    <img src="${resource(dir: 'images', file: 'NCBI.GIF')}" alt="NCBI" title="NCBI"/>
                </a></dd>
            <dd>Accession #: <a href="http://www.uniprot.org/uniprot/${target.acc}" target="_blank">
                <img src="${resource(dir: 'images', file: 'uniprot_logo.gif')}" alt="Accession"
                     title="Accession Number"/>
            </a></dd>
        </dl>
    </g:each>
</section>

