package bardqueryapi

import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.AbstractAssay
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.FreeTextAssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import org.apache.commons.lang.time.StopWatch

import java.util.regex.Matcher
import java.util.regex.Pattern

class QueryHelperService {

    /**
     *  These are the terms that we would use for autosuggest
     *   Add more terms as we go along
     *  We should store this in a database so it becomes easy to manage
     */
    final static Map<String, String> AUTO_SUGGEST_FILTERS = [
            'gobp_term': 'GO Biological Process Term',
            'gocc_term': 'GO Cellular Component Term',
            'gomf_term': 'GO Molecular Function Term',
            'target_name': 'Target Name',
            'kegg_disease_cat': 'KEGG Disease Category',
            'kegg_disease_names': 'KEGG Disease Name',
            'assay_type': 'Assay Type',
            'iso_smiles': 'ISO SMILES',
            'iupac_name': 'IUPAC Name',
            'preferred_term': 'Preferred Term'
    ]

    //filters that starts with a number or '[' to denote ranges
    final static Pattern FILTER_NUMBER_RANGES = Pattern.compile("^(\\d+.*|-\\d+.*)");

    /**
     *
     * @param term
     * @param autoSuggestResponseFromJDO
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term, final Map<String, List<String>> autoSuggestResponseFromJDO) {

        final List<Map<String, String>> autoSuggestTerms = []

        for (String key : autoSuggestResponseFromJDO.keySet()) {
            if (AUTO_SUGGEST_FILTERS.containsKey(key)) {
                final List<String> terms = autoSuggestResponseFromJDO.get(key)
                List<Map<String, String>> terms1 = this.getAutoSuggestTerms(AUTO_SUGGEST_FILTERS, terms, key)
                autoSuggestTerms.addAll(terms1)
            }
        }
        //we insert what the user has typed back into the map
        autoSuggestTerms.add(0, [label: term, value: term])
        return autoSuggestTerms
    }
    /**
     * Apply the filters to the SearchParams
     * @param searchParams
     * @param searchFilters
     */
    void applySearchFiltersToSearchParams(final SearchParams searchParams, final List<SearchFilter> searchFilters) {
        if (searchFilters) {
            List<String[]> filters = []
            for (SearchFilter searchFilter : searchFilters) {
                //if the filter starts with a [, then do not quote
                String filterValue = searchFilter.filterValue
                Matcher matcher = FILTER_NUMBER_RANGES.matcher(filterValue);
                if (matcher.matches() || filterValue.startsWith("[")) {
                    filters.add([searchFilter.filterName, filterValue] as String[])
                }
                else {  //quote string
                    //if string is already quoted strip it
                    filterValue = filterValue.replaceAll("\"", "");
                    filters.add([searchFilter.filterName, "\"" + filterValue + "\""] as String[])
                }
            }
            searchParams.setFilters(filters)
        }
    }

    /**
     *
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return SearchParams
     */
    public SearchParams constructSearchParams(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters) {
        final SearchParams searchParams = new SearchParams(searchString)
        searchParams.setSkip(skip)
        searchParams.setTop(top);
        applySearchFiltersToSearchParams(searchParams, searchFilters)
        return searchParams

    }
    /**
     * @param searchFilters {@link SearchFilter}'s
     * return List<String[]>
     */
    public List<String[]> convertSearchFiltersToFilters(final List<SearchFilter> searchFilters) {
        List<String[]> filters = []
        for (SearchFilter searchFilter : searchFilters) {
            filters.add([searchFilter.filterName, searchFilter.filterValue] as String[])
        }
        return filters
    }
    //=========== Construct adapters ===================
    /**
     * Convert the list of compounds to the list of adapters
     * @param compounds {@link CompoundResult}'s
     * @return List of {@link CompoundAdapter}'s
     */
    final List<CompoundAdapter> compoundsToAdapters(final CompoundResult compoundResult) {
        final List<CompoundAdapter> compoundAdapters = []

        for (Compound compound : compoundResult.compounds) {
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
            compoundAdapters.add(compoundAdapter)
        }
        return compoundAdapters
    }
    /**
     * convert a list Assay's to a list of AssayAdapter's
     * @param assays {@link FreeTextAssayResult}
     * @return list of {@link AssayAdapter}'s
     */
    public List<AssayAdapter> assaysToAdapters(FreeTextAssayResult freeTextAssayResult) {
        final List<Assay> assays = freeTextAssayResult.assays
        if (assays) {
            return assaysToAdapters(assays)
        }
        return []
    }
    /**
     * convert a list Assay's to a list of AssayAdapter's
     * @param assays {@link Assay}
     * @return list of {@link AssayAdapter}'s
     */
    public List<AssayAdapter> assaysToAdapters(List<AbstractAssay> assays) {
        final List<AssayAdapter> assayAdapters = []
        for (AbstractAssay assay : assays) {
            assayAdapters.add(new AssayAdapter(assay))
        }
        return assayAdapters
    }

    public List<ProjectAdapter> projectsToAdapters(final ProjectResult projectResult) {
        final List<ProjectAdapter> projectAdapters = []
        for (Project project : projectResult.projects) {
            projectAdapters.add(new ProjectAdapter(project))
        }
        return projectAdapters
    }
    /**
     * Extract filters from the search string if any
     * @param searchFilters {@link SearchFilter}'s
     * @param searchString
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString) {
        final String filterName = findFilteredTerm(searchString)
        if (filterName) {
            final SearchFilter searchFilter = constructFilter(filterName, searchString)
            if (searchFilter) {
                searchFilters.add(searchFilter)
            }
        }
    }

    /**
     *
     * @param filtersMap
     * @param terms
     * @param currentAutoSuggestKey
     * @return Map of auto suggest terms
     */
    protected List<Map<String, String>> getAutoSuggestTerms(final Map<String, String> filtersMap,
                                                            final List<String> terms,
                                                            final String currentAutoSuggestKey) {

        final List<Map<String, String>> currentAutoSuggestTerms = []

        for (String term : terms) {
            final Map<String, String> termMap = constructSingleAutoSuggestTerm(filtersMap, currentAutoSuggestKey, term)
            if (termMap) {
                currentAutoSuggestTerms.add(termMap)
            }
        }
        return currentAutoSuggestTerms
    }

    /**
     * Return the part of the searchString that matched the filtered terms
     *
     * For example gobp_term:"Dna repair"
     * Is a filtered term and we should return  gobp_term
     * @param searchString
     * @return String
     */
    protected String findFilteredTerm(final String searchString) {
        if (searchString) {
            final Set<String> keys = AUTO_SUGGEST_FILTERS.keySet()
            //if the search string starts with anything in the FILTERS Map then we need to apply a filter to it
            final Collection<String> foundMatch = keys.findAll { key -> searchString.toLowerCase().startsWith(key) }
            if (foundMatch && !foundMatch.isEmpty()) {
                //just return the first item
                return foundMatch.iterator().next()
            }
        }
        return null
    }

    /**
     *
     *
     * @param filtersMap
     * @param currentAutoSuggestKey
     * @param term
     * @return the current map
     */
    protected Map<String, String> constructSingleAutoSuggestTerm(final Map<String, String> filtersMap, final String currentAutoSuggestKey, final String term) {
        if (currentAutoSuggestKey && term) {
            final String label = "${term} as <strong>" + filtersMap.get(currentAutoSuggestKey) + "</strong>"
            final String value = currentAutoSuggestKey + ":\"" + term + "\""
            return [label: label, value: value]
        }
        return [:]
    }

    /**
     * Add a filter name, value pair
     *
     * @param filterName
     * @param searchString
     */
    protected SearchFilter constructFilter(final String filterName, final String searchString) {
        if (filterName && searchString) {
            final String searchValue = stripCustomFiltersFromSearchString(searchString)
            if (searchValue) {
                return new SearchFilter(filterName: filterName, filterValue: searchValue)
            }
        }
        return null
    }
    /**
     *  Remove custom syntax from search string
     *  re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now become SearchString)
     * @param searchString
     * @return updated string
     */
    protected String stripCustomStringFromSearchString(final String searchString) {
        final String updatedSearchString = stripCustomFiltersFromSearchString(searchString) ?: searchString
        return updatedSearchString
    }
    /**
     * If this string has custom search paramaters, remove them
     * @param searchString
     * @return String
     */
    protected String stripCustomFiltersFromSearchString(final String searchString) {
        if (searchString) {
            final int firstIndexOfColon = searchString.trim().indexOf(":")
            if (firstIndexOfColon > -1) { //if we found any string that has a colon in it, we assume that it is a filter
                return searchString.substring(firstIndexOfColon + 1, searchString.length())
            }
        }
        return null
    }
    /**
     * Start the stop-watch that measure network traffic time for any of the JDO services.
     * @return StopWatch
     */
    public StopWatch startStopWatch() {
        StopWatch sw = new StopWatch()
        sw.start()
        return sw
    }

    /**
     * Stop the stop-watch and log the time.
     * @param sw
     */
    public void stopStopWatch(StopWatch sw, String loggingString) {
        sw.stop()
        Date now = new Date()
        Map loggingMap = [time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time, info: loggingString]
        log.info(loggingMap.toString())
    }
}