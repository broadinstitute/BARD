<g:if test="${totalCompounds > 0}">
    <h3><a href="#">Compounds (${totalCompounds})</a></h3>
</g:if>
<g:else>
    <h3><a href="#">Compounds (${compounds.size()})</a></h3>
</g:else>
<g:set var="numberOfAssaysToDisplay" value="${0}" />
<div>
    <div class="content">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Structure</th>
                <th>CID</th>
                <g:each var="assay" in="${assays}" status="i">
                   <g:if  test="i < 3">
                   <g:set var="numberOfAssaysToDisplay" value="${numberOfAssaysToDisplay + 1}" />
                  <th>${assay.assayNumber}</th>
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
                    <g:each var="i" in="${ (0..<numberOfAssaysToDisplay) }">
                        <td></td>
                    </g:each>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>