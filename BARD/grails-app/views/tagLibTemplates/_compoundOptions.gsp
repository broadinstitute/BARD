<%@ page import="querycart.QueryItemType" %>
<div class="compound-info">
    <g:if test="${smiles}">
        <img alt="${smiles}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: imageWidth, height: imageHeight])}"
             style="min-width: ${imageWidth}px; min-height: ${imageHeight}px"/>
    </g:if>
    <g:else>
        <img alt="CID: ${cid}" title="CID: ${cid}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: cid, width: imageWidth, height: imageHeight])}"/>
    </g:else>
    <g:if test="${name}">
        <g:if test="${name.size() <= 15}">
            <div class="text-center">${name}</div>
        </g:if>
        <g:else>
            <div title="${name}" rel="tooltip" class="text-center">${name.substring(0, 14)}...</div>
        </g:else>
    </g:if>
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
                                                             value="90" maxlength="4" size="4" id="cutoff"
                                                             style="width: auto;"/>
                </li>
                <li>
                    <g:link controller="molSpreadSheet" action="showExperimentDetails"
                            params="[cid: cid, transpose: true]">Show Experimental Details</g:link>
                </li>
                <g:if test="${smiles && name}">
                    <li>
                        <g:saveToCartButton id="${cid}"
                                            name="${bardqueryapi.JavaScriptUtility.cleanup(name)}"
                                            type="${QueryItemType.Compound}"
                                            smiles="${smiles}"/>
                    </li>
                </g:if>
                <li>
                    <g:link controller="bardWebInterface" action="showCompoundBioActivitySummary"
                            id="${cid}">Bio-activity Summary</g:link>
                </li>
                <li>
                    <g:link controller="bardWebInterface" action="bigSunburst"
                            id="${cid}">Linked Hierarchy Visualization</g:link>
                </li>
            </ul>
        </span>
    </div>
</div>

