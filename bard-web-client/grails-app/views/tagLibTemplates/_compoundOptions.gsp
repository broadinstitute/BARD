%{-- structureSearchString is of the form StructureSearchType:smiles or cid
examples Similarity:1234
Similarity:CCC

--}%
<div class="compound-info">
    <g:if test="${smiles}">
        <img alt="${smiles}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: imageWidth, height: imageHeight])}"/>
    </g:if>
    <g:else>
        <img alt="SID: ${sid}" title="SID: ${sid}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: cid, width: imageWidth, height: imageHeight])}"/>
    </g:else>
    <div class="compound-info-dropdown">
        <span class="btn-group">
            <button class="btn btn-mini dropdown-toggle" data-toggle="dropdown"><i class="icon-info-sign"></i> <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <g:if test="${smiles}">
                    <li>Smiles : ${smiles}</li>
                </g:if>
                <li><a href="#" data-detail-id="sid_${sid}" class="analogs"
                       data-structure-search-params="Similarity:${cid}">Search For Analogs</a></li>
            </ul>
        </span>
    </div>
</div>

