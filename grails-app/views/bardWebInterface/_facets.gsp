<%@ page import="bard.core.Value; bardqueryapi.SearchFilter" %>
<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <g:form name="${formName.toString()}" controller="bardWebInterface" id="${formName.toString()}"
                    action="applyFilters">
                <g:submitButton name="applyFilters" value="Apply Filters" id="${formName}_Button"
                                class="btn btn-small"/>
                <h2>Filters</h2>
                <g:hiddenField name="searchString" value="${params?.searchString}"/>

                <g:set var="childIndex" value="${0}"/>
                %{--Display the filters already selected in previous search/filter session (but not part of the facets section) --}%
                <g:each in="${appliedFilters?.appliedFiltersDisplayedOutsideFacetsGrouped?.keySet() ?: []}" var="groupName">
                    <fieldset>
                        <h3>${groupName}</h3>
                        <g:each in="${appliedFilters.appliedFiltersDisplayedOutsideFacetsGrouped[groupName]}" var="filter">
                            <label class="checkbox">
                                <g:checkBox name="filters[${childIndex}].filterValue" value="${filter.filterValue}"
                                            checked="true"/> ${filter.filterValue}
                            </label>
                            <g:hiddenField name="filters[${childIndex}].filterName" value="${filter.filterName}"/>
                            <g:set var="childIndex" value="${childIndex + 1}"/>
                        </g:each>
                    </fieldset>
                </g:each>
                <g:each in="${facets}" var="parentFacet">
                    %{--<g:if test="${parentFacet.children}">--}%
                        <fieldset>
                            <h3>${parentFacet.id.replaceAll("_", " ").toLowerCase().capitalize()}</h3>
                            <g:each in="${parentFacet.children}" var="childFacet">
                                <g:if test="${childFacet.id}">
                                    <label class="checkbox">
                                        <g:checkBox name="filters[${childIndex}].filterValue" value="${childFacet.id}"
                                                    checked="${appliedFilters?.searchFilters?.find { SearchFilter filter -> ((filter.filterName.trim().equalsIgnoreCase(parentFacet.id.trim())) && (filter.filterValue.trim().equalsIgnoreCase(childFacet.id)))}}"/> ${childFacet.id} (${childFacet.value})
                                    </label>
                                    <g:hiddenField name="filters[${childIndex}].filterName" value="${parentFacet.id}"/>
                                    <g:set var="childIndex" value="${childIndex + 1}"/>
                                </g:if>
                            </g:each>
                            %{--Add all the filters that were selected in the preceding search but didn't come back in the facets--}%
                            <g:each in="${appliedFilters?.appliedFiltersNotInFacetsGrouped?.get(parentFacet.id) ?: []}" var="filter">
                                <label class="checkbox">
                                    <g:checkBox name="filters[${childIndex}].filterValue" value="${filter.filterValue}"
                                                checked="true"/> ${filter.filterValue} (0)
                                </label>
                                <g:hiddenField name="filters[${childIndex}].filterName" value="${filter.filterName}"/>
                                <g:set var="childIndex" value="${childIndex + 1}"/>
                            </g:each>
                        </fieldset>
                    %{--</g:if>--}%
                </g:each>
                <g:hiddenField name="formName" value="${formName.toString()}"/>
            </g:form>
        </div>
    </g:if>
</div>