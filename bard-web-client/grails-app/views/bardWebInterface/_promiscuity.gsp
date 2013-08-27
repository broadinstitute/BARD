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