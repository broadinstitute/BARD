<div class="accordion-group">
    <div class="accordion-heading">
        <a href="#target-header" id="target-header" class="accordion-toggle" data-toggle="collapse"
           data-target="#target-bio-info"><i class="icon-chevron-right"></i> Targets (${targets.size()})
        </a>
        <div id="target-bio-info" class="accordion-body collapse">
            <div class="accordion-inner">
                <g:each in="${targets}" var="target">
                    <dl>
                        <dt>Name: ${target.name}</dt>
                        <dd><g:textBlock>Description: ${target.description}</g:textBlock></dd>
                        <dd>Status: ${target.status}</dd>
                        <dd>Gene ID: ${target.geneId}</dd>
                        <dd>Taxonomic Identifier: <a href="http://www.uniprot.org/taxonomy/${target.taxId}" target="_blank">${target.taxId}</a>
                            <a href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?lvl=0&id=${target.taxId}" target="_blank"> [NCBI]</a></dd>
                        <dd>Accession #: <a href="http://www.uniprot.org/uniprot/${target.acc}" target="_blank">${target.acc}</a></dd>
                    </dl>
                </g:each>
            </div>
        </div>
    </div>
</div>
