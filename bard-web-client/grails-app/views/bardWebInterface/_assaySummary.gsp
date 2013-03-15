<dl class="dl-horizontal dl-horizontal-wide">
    <g:if test="${assayAdapter.assayTypeString}">
        <dt>Assay Type:</dt>
        <dd>${assayAdapter.assayTypeString}</dd>
    </g:if>
    <g:if test="${assayAdapter.designedBy}">
        <dt>Designed By:</dt>
        <dd>${assayAdapter.designedBy}</dd>
    </g:if>
    <g:each in="${assayAdapter?.annotations[0]?.contexts?.collectMany { it.comps }?.unique{ it.key + it.display }?.sort{it.key}}" var="annotation">
        <g:if test="${annotation.key == 'assay type'}">
            <dt>Assay Type:</dt>
            <dd>${annotation.display}</dd>
        </g:if>
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
    <g:if test="${assayAdapter?.source}">
        <dt>Assay Designer:</dt>
        <dd>${assayAdapter.source}</dd>
    </g:if>
    <g:if test="${assayAdapter?.aid}">
        <dt>PubChem AID:</dt>
        <dd><a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${assayAdapter.aid}" target="_blank">
            <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
            ${assayAdapter.aid}
        </a>
        </dd>
    </g:if>
</dl>
