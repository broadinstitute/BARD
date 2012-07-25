<g:if test="${totalCompounds > 0}">
    <h3><a href="#">Compounds (${totalCompounds})</a></h3>
</g:if>
<g:else>
    <h3><a href="#">Compounds (${compounds.size()})</a></h3>
</g:else>
<g:set var="numberOfHeadersToDisplay" value="${0}" />
<div>
    <div class="content">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Structure</th>
                <th>CID</th>
                <g:each in="${compoundHeaderInfo}" var="currentEntry" status="i">
                %{--${currentEntry.value?.replaceAll(']', '')}--}%
                %{--<g:each in=" ${currentEntry.value}" var="assayNumber">--}%
                %{--${assayNumber} &nbsp;--}%
                %{--We want to display a max of five targets. Each target Name should have an accession number associated with it
                 and a list of assay numbers should be in brackets underneath it. The assay numbers should be limited to 3
                 --}%
                    <g:if  test="i < 5">
                    <g:set var="numberOfHeadersToDisplay" value="${numberOfHeadersToDisplay + 1}" />
                    <th>${currentEntry.key.targetName} ${currentEntry.key.accessionNumber} <br/>
                        (
                        ${currentEntry.value.toString().replaceAll(/[\[\], ]+/, ' ')}
                        )
                        </th>
                        </g:if>
                </g:each>
            </tr>
            </thead>
            <tbody>
            <g:each var="compound" in="${compounds}">

                <tr>
                    <td>
                        <img src="${createLink(controller:'chemAxon', action:'generateStructureImage', params:[smiles: compound.smiles, width: 150, height: 120])}" />
                    </td>
                    <td>
                        ${compound.cid}
                    </td>
                    <g:each var="i" in="${ (0..<numberOfHeadersToDisplay) }">
                        <td></td>
                    </g:each>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>