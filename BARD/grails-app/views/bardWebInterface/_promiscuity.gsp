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

<ul class="thumbnails">
    <g:each in="${scaffolds}" var="scaffold" status="i">
        <li>
            <div class="thumbnail">
                <img alt="${scaffold.smiles}" title="Scaffold Id: ${scaffold.scaffoldId}"
                     src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: scaffold.smiles, width: 50, height: 50])}"/>
                <div class="warning-level warning-level-${scaffold.warningLevel}">
                    ${scaffold.promiscuityScore}
                </div>
                <p><button data-detail-id="scaffold_${scaffold.scaffoldId}" class="popover-link btn btn-small" data-original-title="Promiscuity Analysis for Scaffold ${scaffold.scaffoldId}" data-html="true">
                    Show details</button>
                </p>
                <div class='popover-content-wrapper' id='scaffold_${scaffold.scaffoldId}' style="display: none;">
                    <div class="center-aligned">
                        <img alt="${scaffold.smiles}" title="Scaffold Id: ${scaffold.scaffoldId}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: scaffold.smiles, width: 200, height: 200])}"/>
                    </div>
                    <ul>
                        <li>Promiscuity Score: ${scaffold.promiscuityScore}</li>
                        <li>Warning Level: ${scaffold.warningLevel?.description}</li>
                        <li>Occurs in ${scaffold.testedSubstances} substances, of which ${scaffold.activeSubstances} tested active</li>
                        <li>Active in ${scaffold.activeAssays} assays out of ${scaffold.testedAssays}</li>
                        <li>Occurs in ${scaffold.testedWells} samples (wells), of which ${scaffold.activeWells} samples (wells) active</li>
                        <li>This scaffold is ${scaffold.inDrug ? '<b>present in one or more</b>' : 'not present in any'} known drugs</li>
                    </ul>
                    <p><small>Jeremy J. Yang, Oleg Ursu, Cristian Bologa, Ramona F. Curpan, Liliana Halip, Christopher A. Lipinski, Larry A. Sklar and Tudor I. Oprea,
                    Mining for promiscuous compounds and patterns: Avoiding HTS bad apples and false trails.</small></p>
                </div>
            </div>
        </li>
    </g:each>
</ul>
