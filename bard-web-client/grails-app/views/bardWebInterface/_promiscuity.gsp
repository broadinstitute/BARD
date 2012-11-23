<ul class="thumbnails">
    <g:each in="${scaffolds}" var="scaffold" status="i">
        <li>
            <div class="thumbnail">
                <img alt="${scaffold.scafsmi}" title="Scaffold Id: ${scaffold.scafid}"
                     src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: scaffold.scafsmi, width: 50, height: 50])}"/>
                <div class="warning-level warning-level-${scaffold.warningLevel}">
                    ${scaffold.pScore}
                </div>
                <p><button data-detail-id="scaffold_${scaffold.scafid}" class="popover-link btn btn-small" data-original-title="Promiscuity Analysis for Scaffold ${scaffold.scafid}" data-html="true">
                    Show details</button>
                </p>
                <div class='popover-content-wrapper' id='scaffold_${scaffold.scafid}' style="display: none;">
                    <div class="center-aligned">
                        <img alt="${scaffold.scafsmi}" title="Scaffold Id: ${scaffold.scafid}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: scaffold.scafsmi, width: 200, height: 200])}"/>
                    </div>
                    <ul>
                        <li>Promiscuity Score: ${scaffold.pScore}</li>
                        <li>Warning Level: ${scaffold.warningLevel?.description}</li>
                        <li>Occurs in ${scaffold.cTested} compounds, of which ${scaffold.cActive} tested active</li>
                        <li>Active in ${scaffold.aActive} assays out of ${scaffold.aTested}</li>
                        <li>Occurs in ${scaffold.sTested} substances, of which ${scaffold.sActive} tested active</li>
                        <li>This scaffold is ${scaffold.inDrug ? '<b>present in one or more</b>' : 'not present in any'} known drugs</li>
                    </ul>
                    <p><small>Jeremy J. Yang, Oleg Ursu, Cristian Bologa, Ramona F. Curpan, Liliana Halip, Christopher A. Lipinski, Larry A. Sklar and Tudor I. Oprea,
                    Mining for promiscuous compounds and patterns: Avoiding HTS bad apples and false trails.</small></p>
                </div>
            </div>
        </li>
    </g:each>
</ul>
