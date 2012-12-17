%{-- structureSearchString is of the form StructureSearchType:smiles or cid
examples Similarity:1234
Similarity:CCC

--}%
<div>
    <div class="btn-group">
        <g:if test="${smiles}">
            <img alt="${smiles}"
                 src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: imageWidth, height: imageHeight])}"/>
        </g:if>
        <g:else>
            <img alt="SID: ${sid}" title="SID: ${sid}"
                 src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: cid, width: imageWidth, height: imageHeight])}"/>
        </g:else>
        <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">Info <span class="caret"></span></button>
        <ul class="dropdown-menu">
            <g:if test="${smiles}">
                <li>Smiles : ${smiles} </li>
            </g:if>
            <li><a href="#" data-detail-id="sid_${sid}"  class="analogs" data-structure-search-params="Similarity:${cid}">Search For Analogs</a> </li>
        </ul>
    </div>
</div>

