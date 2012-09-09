<%@ page import="bard.core.Value; bardqueryapi.SearchFilter" %>
<%
    //Since the _facet.gsp is shared by all three assays/compounds/project pages, we need the common code to reside in the GSP instead of in the controller (or service)

    //Includes all the applied search-filters (selected previously) that were also returned with the new filtering faceting.
    List<SearchFilter> appliedFiltersAlreadyInFacets = searchFilters.findAll { SearchFilter filter ->
        Value parent = facets.find {Value parent -> parent.id == filter.filterName}
        return parent?.children.find { Value child -> child.id == filter.filterValue}
    }

    //Groups all the applied search-filters in facets into a parent-facet/children-facets map. We use this group to display the applied search filters WITHIN the facet groups
    //If the facet-group exists but the applied-filter's corresponding facet didn't come back after the filtering, we still want to display the filter in its appropriate (facet) group, if we can.
    Map appliedFiltersNotInFacetsGrouped = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets) ?
        (searchFilters - appliedFiltersAlreadyInFacets).groupBy { SearchFilter filter -> filter.filterName} : [:]

    //Includes all the applied filters we know would not have any facet group since no facet in this group came back after the filtering was applied.
    //We need to group these filters, rebuild their groups (parent) and display them next to the facets
    List<SearchFilter> appliedFiltersDisplayedOutsideFacets = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets)?.findAll { SearchFilter filter ->
        //filter.filterName is not in any of the parents' ids
        return !(facets.find { Value parent -> parent.id == filter.filterName})
    }

    //Group all the applied filters so we can use the keys as group (parent) names.
    Map appliedFiltersDisplayedOutsideFacetsGrouped = appliedFiltersDisplayedOutsideFacets ?
        appliedFiltersDisplayedOutsideFacets.groupBy { SearchFilter filter -> filter.filterName} : [:]
%>
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
                <g:each in="${appliedFiltersDisplayedOutsideFacetsGrouped.keySet()}" var="groupName">
                    <fieldset>
                        <p>test</p>

                        <h3>${groupName}</h3>
                        <g:each in="${appliedFiltersDisplayedOutsideFacetsGrouped[groupName]}" var="filter">
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
                                                    checked="${searchFilters?.find { SearchFilter filter -> filter.filterValue == childFacet.id}}"/> ${childFacet.id} (${childFacet.value})
                                    </label>
                                    <g:hiddenField name="filters[${childIndex}].filterName" value="${parentFacet.id}"/>
                                    <g:set var="childIndex" value="${childIndex + 1}"/>
                                </g:if>
                            </g:each>
                            %{--Add all the filters that were selected in the preceding search but didn't come back in the facets--}%
                            <g:each in="${appliedFiltersNotInFacetsGrouped[parentFacet.id]}" var="filter">
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