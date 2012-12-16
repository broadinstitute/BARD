<div class="accordion-group">
    <div class="accordion-heading">
        <a href="#publication-header" id="publication-header" class="accordion-toggle" data-toggle="collapse"
           data-target="#publication-info"><i
                class="icon-chevron-right"></i> Publications(${documents.size()})</a>

        <div id="publication-info" class="accordion-body collapse">
            <div class="accordion-inner">
                <g:each in="${documents}" var="document">
                    <dl>
                        <dt>Title: ${document.title}</dt>
                        <dd><g:textBlock>Abstract: ${document.abs}</g:textBlock></dd>
                        <dd>DOI: ${document.doi}</dd>
                        <dd>PubMed Identifier: <a
                                href="http://www.ncbi.nlm.nih.gov/m/pubmed/${document.pubmedId}">${document.pubmedId}</a>
                        </dd>
                    </dl>
                </g:each>
            </div>
        </div>
    </div>
</div>
