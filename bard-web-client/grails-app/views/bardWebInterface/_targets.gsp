<section id="target-info">
    <div class="page-header">
        <h3>Targets</h3>
    </div>

    <g:each in="${targets}" var="target">
        <dl class="dl-horizontal dl-horizontal-wide">
            <dt>Name:</dt> <dd>${target.name}</dd>
            <dt>Description:</dt><dd><g:textBlock>${target.description}</g:textBlock>&nbsp;</dd>
            <dt>Status:</dt><dd>${target.status} &nbsp;</dd>
            <dt>Gene ID:</dt><dd>${target.geneId} &nbsp;</dd>
            <dt>Taxonomic Identifier:</dt><dd>${target.taxId}<a href="http://www.uniprot.org/taxonomy/${target.taxId}" target="taxId">
                <img src="${resource(dir: 'images', file: 'uniprot_logo.gif')}" alt="Taxonomy"
                     title="Taxonomy"/></a>
                <a href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?lvl=0&id=${target.taxId}"
                   target="ncbi">
                    <img src="${resource(dir: 'images', file: 'logoNcbi.jpg')}" alt="NCBI" title="NCBI"/>
                </a></dd>
            <dt>Accession #:</dt><dd>${target.acc}<a href="http://www.uniprot.org/uniprot/${target.acc}" target="uniprot">
                <img src="${resource(dir: 'images', file: 'uniprot_logo.gif')}" alt="Accession"
                     title="Accession Number"/>
            </a></dd>
        </dl>
    </g:each>
</section>

