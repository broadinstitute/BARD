<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <g:form name="${formName}" controller="bardWebInterface" action="applyFilters">
                <g:submitButton name="Apply Filters" class="btn"/>
                <h2>Filters</h2>
                <g:hiddenField name="searchString" value="${params?.searchString}"/>
                <g:set var="childIndex" value="${0}"/>
                <g:each in="${facets}" var="facet">
                    <fieldset>
                        <h3>${facet.id}</h3>
                        <g:each in="${facet.children}" var="entry">
                            <label class="checkbox">
                                <g:checkBox name="filters[${childIndex}].filterValue" value="${entry.id}" checked=""/> ${entry.id} (${entry.value})
                            </label>
                            <g:hiddenField name="filters[${childIndex}].filterName" value="${facet.id}"/>
                            <g:set var="childIndex" value="${childIndex+1}"/>
                        </g:each>
                    </fieldset>
                </g:each>
            </g:form>
        </div>
    </g:if>
</div>