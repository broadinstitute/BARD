<table class="table table-striped table-bordered">
    <thead>
        <th>Biology</th>
        <th>Term Type</th>
        <th>Term Value</th>
    </thead>
    <tbody>
        <g:each in="${biology}" var="biologyEntry">
            <tr>
                <td>${biologyEntry?.biology?.toLowerCase()}</td>
                <td>${biologyEntry.dictLabel}</td>
                <td>
                    <g:if test="${biologyEntry.dictLabel == "UniProt accession number"}">
                        <a href='http://www.uniprot.org/uniprot/?query=${biologyEntry.extId}' target="_blank">
                            ${biologyEntry.name}
                        </a>
                    </g:if>
                    <g:elseif test="${biologyEntry.dictLabel == "GenBank ID"}">
                        <a href="http://www.ncbi.nlm.nih.gov/protein/${biologyEntry.extId}" target="_blank">
                            ${biologyEntry.name}
                        </a>
                    </g:elseif>
                    <g:elseif test="${biologyEntry.dictLabel == "GO biological process term"}">
                        <a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=${biologyEntry.extId}' target="_blank">
                            ${biologyEntry.name}
                        </a>
                    </g:elseif>
                    <g:else>
                        ${biologyEntry.name}
                    </g:else>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>