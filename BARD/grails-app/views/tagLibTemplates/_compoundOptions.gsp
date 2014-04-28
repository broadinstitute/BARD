%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

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

