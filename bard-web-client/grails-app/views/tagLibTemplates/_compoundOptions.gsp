<%@ page import="querycart.QueryItemType" %>
<div class="compound-info">
    <g:if test="${smiles}">
        <img alt="${smiles}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: imageWidth, height: imageHeight])}"
        style="min-width: ${imageWidth}px; min-height: ${imageHeight}px"/>
    </g:if>
    <g:else>
        <img alt="SID: ${sid}" title="SID: ${sid}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: cid, width: imageWidth, height: imageHeight])}"/>
    </g:else>
    <div class="compound-info-dropdown">
        <span class="btn-group">
            <button class="btn btn-mini dropdown-toggle" data-toggle="dropdown"><i class="icon-info-sign"></i> <span
                    class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <g:if test="${smiles}">
                %{--Use tooltip to display the SMILES string in case the it is larger than 30 character.--}%
                    <g:if test="${smiles.size() <= 30}">
                        <li>Smiles : ${smiles}</li>
                    </g:if>
                    <g:else>
                        <li title="${smiles}" rel="tooltip">Smiles : ${smiles.substring(0, 29)} ... More</li>
                    </g:else>
                </g:if>
                <li><a href="#" data-detail-id="sid_${sid}" class="analogs"
                       data-structure-search-params="Similarity:${cid}">Search For Analogs</a>
                    &nbsp; &nbsp;Threshold (0-100%) : <input type="number" min="0" max="100" step="1" name="cutoff"
                                                             value="90" size="4" id="cutoff"/>
                </li>
                <li>
                    <g:link controller="molSpreadSheet" action="showExperimentDetails"
                            params="[cid: cid, transpose: true]">Show Experimental Details</g:link>
                </li>
                <g:if test="${smiles && name && numActive && numAssays}">
                    <li>
                        <a href="javascript:void(0);" name="saveToCartLink" class="addToCartLink"
                           data-cart-name="${name}"
                           data-cart-id="${cid}"
                           data-cart-type="${QueryItemType.Compound}"
                           data-cart-smiles="${smiles}"
                           data-cart-numActive="${numActive}"
                           data-cart-numAssays="${numAssays}">Save to Cart For Analysis</a>
                    </li>
                </g:if>
                <li>
                    <g:link controller="bardWebInterface" action="showCompoundBioActivitySummary"
                            id="${cid}">Bio-activity Summary</g:link>
                </li>
            </ul>
        </span>
    </div>
</div>

