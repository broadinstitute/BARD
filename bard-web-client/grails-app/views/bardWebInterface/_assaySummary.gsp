<dl class="dl-horizontal dl-horizontal-wide">
    <g:if test="${assayAdapter.type}">
        <dt>Assay Type:</dt>
        <dd>${assayAdapter.type}</dd>
    </g:if>
    <g:each in="${assayAdapter?.annotations}" var="annotation">
        <g:if test="${annotation.key == 'assay format'}">
            <dt>Assay Format:</dt>
            <dd>${annotation.display}</dd>
        </g:if>
        <g:if test="${annotation.key == 'detection method type'}">
            <dt>Detection Method:</dt>
            <dd>${annotation.display}</dd>
        </g:if>
        <g:if test="${annotation.key == 'detection instrument name'}">
            <dt>Detection Instrument:</dt>
            <dd>${annotation.display}</dd>
        </g:if>
        <g:if test="${annotation.key == 'assay footprint'}">
            <dt>Assay Footprint:</dt>
            <dd>${annotation.display}</dd>
        </g:if>
    </g:each>
    <g:if test="${assayAdapter.source}">
        <dt>Assay Designer:</dt>
        <dd>${assayAdapter.source}</dd>
    </g:if>
    <g:if test="${assayAdapter.aid}">
        <dt>PubChem AID:</dt>
        <dd><a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${assayAdapter.aid}" target="_blank">
            <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
            ${assayAdapter.aid}
        </a>
        </dd>
    </g:if>
</dl>
