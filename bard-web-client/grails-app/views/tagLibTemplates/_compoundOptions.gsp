%{--<%@ page import="bardqueryapi.CompoundOptions" %>--}%
%{--<r:require module="compoundOptions"/>--}%
%{-- structureSearchString is of the form StructureSearchType:smiles or cid
examples Similarity:1234
Similarity:CCC

--}%
<button data-detail-id="sid_${sid}" class="popover-link btn btn-small" data-original-title=""
        data-html="true" data-trigger="click">
    <g:if test="${smiles}">
        <img alt="${smiles}" title="Click to Copy SMILES"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: imageWidth, height: imageHeight])}"/>
    </g:if>
    <g:else>
        <img alt="SID: ${sid}" title="SID: ${sid}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: cid, width: imageWidth, height: imageHeight])}"/>
    </g:else>
</button>


<div class='popover-content-wrapper' id="sid_${sid}" style="display: none;">
    <div class="center-aligned">
        <g:if test="${smiles}">
            Smiles : ${smiles} <br/>
            <a href="#" class="analogs" data-structure-search-params="Similarity:${smiles}">Search For Analogs</a>
        </g:if>
        <g:else>
            <a href="#" class="analogs" data-structure-search-params="Similarity:${cid}">Search For Analogs</a>
        </g:else>
    </div>
</div>

