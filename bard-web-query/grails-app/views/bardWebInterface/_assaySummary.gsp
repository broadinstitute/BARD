<dl class="dl-horizontal dl-horizontal-wide">
    <g:if test="${assay.getValue('assay type')}">
        <dt>Assay Type:</dt>
        <dd>${assay.getValue('assay type').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('assay format')}">
        <dt>Assay Format:</dt>
        <dd>${assay.getValue('assay format').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('detection method type')}">
        <dt>Detection Method:</dt>
        <dd>${assay.getValue('detection method type').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('detection instrument name')}">
        <dt>Detection Instrument:</dt>
        <dd>${assay.getValue('detection instrument name').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('assay footprint')}">
        <dt>Assay Footprint:</dt>
        <dd>${assay.getValue('assay footprint').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('AssaySource')}">
        <dt>Assay Designer:</dt>
        <dd>${assay.getValue('AssaySource').value}</dd>
    </g:if>
    <g:if test="${assay.getValue('AssayPubChemAID')}">
        <dt>PubChem AID:</dt>
        <dd><a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${assay.getValue('AssayPubChemAID').value}" target="_blank">
            ${assay.getValue('AssayPubChemAID').value}
        </a>
        </dd>
    </g:if>
</dl>
