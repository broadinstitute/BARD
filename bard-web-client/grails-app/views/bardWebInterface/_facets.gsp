<%@ page import="bardqueryapi.FacetFormType; bard.core.Value; bardqueryapi.SearchFilter" %>
<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <g:form name="${formName.toString()}" id="${formName.toString()}">
                <g:submitButton name="applyFilters" value="Apply Filters" id="${formName}_Button"
                                class="btn btn-small"/>
                <input type="button" class="btn btn-small" id="${formName}_ResetButton" value="Clear All Filters"
                       name="resetFilters">

                <h2>${sidebarTitle ?: 'Filters'}</h2>
                <g:hiddenField name="searchString" value="${params?.searchString}"/>

                <g:set var="childIndex" value="${0}"/>
            %{--Display the filters already selected in previous search/filter session (but not part of the facets section) --}%
                <g:each in="${appliedFilters?.appliedFiltersDisplayedOutsideFacetsGrouped?.keySet() ?: []}"
                        var="groupName">
                    <fieldset>
                        <h3>${groupName}</h3>
                        <g:each in="${appliedFilters.appliedFiltersDisplayedOutsideFacetsGrouped[groupName]}"
                                var="filter">
                            <label class="checkbox">
                                <g:checkBox name="filters[${childIndex}].filterValue" value="${filter.filterValue}"
                                            checked="true"/> ${filter.filterValue}
                            </label>
                            <g:hiddenField name="filters[${childIndex}].filterName" value="${filter.filterName}"/>
                            <g:set var="childIndex" value="${childIndex + 1}"/>
                        </g:each>
                    </fieldset>
                </g:each>
            %{--Display all the facets that came from the search--}%
                <g:each in="${facets}" var="parentFacet">

                    <fieldset>
                        <h3>${parentFacet.id.replaceAll("_", " ").toLowerCase().capitalize()}</h3>
                        <g:set var="childSize" value="${parentFacet.children.size()}"/>
                        <g:each in="${parentFacet.children}" var="childFacet" status="counter">
                            <g:if test="${counter == 4 && childSize > 5}">
                                <div id="${parentFacet.id}_${formName.toString()}" style="display: none;">
                            </g:if>
                            <g:if test="${childFacet.id}">
                                <label class="checkbox">
                                    <g:set var="checked"
                                           value="${appliedFilters?.searchFilters?.find { SearchFilter filter -> ((filter.filterName.trim().replace('"', '').equalsIgnoreCase(parentFacet.id.trim())) && (filter.filterValue.trim().replace('"', '').equalsIgnoreCase(childFacet.id)))}}"/>
                                    <g:checkBox name="filters[${childIndex}].filterValue" value="${childFacet.id}"
                                                checked="${checked}" class="${formName}_Chk"/> ${childFacet.id}${childFacet.value>=0 ? " (${childFacet.value})" : ""}
                                </label>
                                <g:hiddenField name="filters[${childIndex}].filterName" value="${parentFacet.id}"/>
                                <g:set var="childIndex" value="${childIndex + 1}"/>
                            </g:if>
                            <g:if test="${counter >= 4 && childSize > 5 && counter == childSize - 1}">
                                </div>
                            </g:if>
                        </g:each>
                        <g:if test="${childSize > 5}">
                            <a href="#" class='facetDiv' div_id="${parentFacet.id}_${formName.toString()}">More</a>
                        </g:if>

                    %{--Add all the filters that were selected in the preceding search but didn't come back in the facets--}%
                        <g:each in="${appliedFilters?.appliedFiltersNotInFacetsGrouped?.get(parentFacet.id) ?: []}"
                                var="filter">
                            <label class="checkbox">
                                <g:checkBox name="filters[${childIndex}].filterValue" value="${filter.filterValue}"
                                            checked="true" class="${formName}_Chk"/> ${filter.filterValue} (0)
                            </label>
                            <g:hiddenField name="filters[${childIndex}].filterName" value="${filter.filterName}"/>
                            <g:set var="childIndex" value="${childIndex + 1}"/>
                        </g:each>
                    </fieldset>
                </g:each>
                <g:hiddenField name="formName" value="${formName.toString()}"/>
            </g:form>
        </div>
    </g:if>
</div>