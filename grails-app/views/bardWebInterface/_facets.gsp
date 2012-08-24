<div class="span3">
    <g:if test="${metaData}">
        <div class="facets">
            <h5>FILTERS</h5>
            <g:each in="${metaData.facets}" var="facet">
                <ul class="unstyled">
                    <h6>${facet.facetName}</h6>
                    <ul>
                        <g:each in="${facet.counts.entrySet()}" var="entry">
                            <li>${entry.key} (${entry.value})</li>
                        </g:each>
                    </ul>
                </ul>
            </g:each>
            </div>
    </g:if>
</div>
