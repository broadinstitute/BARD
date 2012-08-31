<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <h5>FILTERS</h5>
            <g:each in="${facets}" var="facet">
                <ul class="unstyled">
                    <h6>${facet.id}</h6>
                    <ul>
                        <g:each in="${facet.children}" var="entry">
                            <li>${entry.id} (${entry.value})</li>
                        </g:each>
                    </ul>
                </ul>
            </g:each>
        </div>
    </g:if>
</div>