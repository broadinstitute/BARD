<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <g:form name="${formName.toString()}" controller="bardWebInterface" id="${formName.toString()}" action="applyFilters">
                <g:submitButton name="applyFilters" value="Apply Filters" id="${formName}_Button" class="btn btn-small"/>
                <h2>Filters</h2>
                <g:hiddenField name="searchString" value="${params?.searchString}"/>

                <g:set var="childIndex" value="${0}"/>
                <g:each in="${facets}" var="facet">
                    <g:if test="${facet.children}">
                        <fieldset>
                            <h3>${facet.id.replaceAll("_", " ").toLowerCase().capitalize()}</h3>
                            <g:each in="${facet.children}" var="entry">
                                <g:if test="${entry.id}">
                                    <label class="checkbox">
                                        <g:checkBox name="filters[${childIndex}].filterValue" value="${entry.id}" checked=""/> ${entry.id} (${entry.value})
                                    </label>
                                    <g:hiddenField name="filters[${childIndex}].filterName" value="${facet.id}"/>
                                    <g:set var="childIndex" value="${childIndex+1}"/>
                                </g:if>
                            </g:each>
                        </fieldset>
                    </g:if>
                </g:each>
                <g:hiddenField name="formName" value="${formName.toString()}"/>
            </g:form>
        </div>
    </g:if>
</div>