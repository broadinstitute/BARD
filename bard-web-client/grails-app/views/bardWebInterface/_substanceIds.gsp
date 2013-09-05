<div class="span12">
    <h3>PubChem Substances</h3>
    <ul class="horizontal-block-list">
        <g:each in="${sids.sort()}" var="sid">
            <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${sid}">
                <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
                ${sid}</a>
            </li>
        </g:each>
    </ul>
</div>

