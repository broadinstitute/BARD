%{--<div class="accordion-group">--}%
    %{--<div class="accordion-heading">--}%
        %{--<a href="#publication-info" class="accordion-toggle" data-toggle="collapse"--}%
           %{--data-target="#publication-info" data-parent="#accordionParent"><i--}%
                %{--class="icon-chevron-right"></i>--}%
            %{--<g:if test="${documents.isEmpty()}">--}%
                %{--Publications(0)--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
                %{--Publications(${documents.size()})--}%
            %{--</g:else>--}%
        %{--</a>--}%
    %{--</div>--}%

    %{--<div id="publication-info" class="accordion-body collapse">--}%
        %{--<div class="accordion-inner">--}%
            %{--<g:each in="${documents}" var="document">--}%
                %{--<dl>--}%
                    %{--TODO: Commenting out until NCGC Fixes it--}%
                    %{--<dt>Title: ${document?.title}</dt>--}%
                    %{--<dd><g:textBlock>Abstract: ${document.abs}</g:textBlock></dd>--}%
                    %{--<dd>DOI: ${document.doi}</dd>--}%
                    %{--<dd>PubMed Identifier: <a--}%
                    %{--href="http://www.ncbi.nlm.nih.gov/m/pubmed/${document.pubmedId}">${document.pubmedId}</a>--}%
                    %{--</dd>--}%
                %{--</dl>--}%
            %{--</g:each>--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%

