<g:if test="${totalCompounds > 0}">
    <h3><a href="#">Compounds (${totalCompounds})</a></h3>

</g:if>
<g:else>
    <h3><a href="#">Compounds (${compounds.size()})</a></h3>
</g:else>

<div>
    <div class="content">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Structure</th>
                <th>CID</th>
            </tr>
            </thead>
            <tbody>
            <g:each var="compound" in="${compounds}">

                <tr>
                    <td>
                        <img src="${createLink(controller:'chemAxon', action:'generateStructureImage', params:[smiles: compound.smiles])}" width="150" height="120" />
                    </td>
                    <td>
                        ${compound.cid}
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>