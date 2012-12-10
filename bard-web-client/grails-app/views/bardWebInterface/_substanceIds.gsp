<div class="span12">
    <h3>PubChem Substances</h3>
    <ul class="horizontal-block-list">
        <g:each in="${sids.sort()}" var="sid">
            <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${sid}">${sid}</a>
            </li>
        </g:each>
    </ul>
</div>

