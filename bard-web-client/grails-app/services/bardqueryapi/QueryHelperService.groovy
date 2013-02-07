package bardqueryapi

import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.AbstractAssay
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.MetaData
import bard.core.rest.spring.util.NameDescription

import java.util.regex.Matcher
import java.util.regex.Pattern
import bard.core.rest.spring.experiment.PriorityElement
import bard.core.rest.spring.experiment.ResponseClassEnum
import bard.core.rest.spring.experiment.ResultData
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ConcentrationResponseSeries

class QueryHelperService {

    /**
     * TODO: Put in properties file
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
    final static String PROBE = "PROB"

    Map extractMapFromResultData(ResultData resultData, NormalizeAxis normalizeAxis) {
        if (resultData.hasPriorityElements()) {
            boolean hasPlot = false
            final PriorityElement priorityElement = resultData.priorityElements.get(0)

            final boolean hasChildElements = priorityElement.hasChildElements()
            final Map priorityMap = this.extractPriorityDisplayDescription(priorityElement)
            if (resultData.responseClassEnum == ResponseClassEnum.CR_SER) {
                hasPlot = true
            }
            if (normalizeAxis == NormalizeAxis.Y_NORM_AXIS) {
                if (resultData.hasConcentrationResponseSeries()) {
                    final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.getConcentrationResponseSeries()
                    if (concentrationResponseSeries) {
                        final List<Double> sorterdActivities = concentrationResponseSeries.sorterdActivities()
                        if (sorterdActivities) {
                            priorityMap.put("yNormMin", sorterdActivities.get(0))
                            priorityMap.put("yNormMax", sorterdActivities.last())
                        }
                    }

                }
            } else {
                priorityMap.put("yNormMin", null)
                priorityMap.put("yNormMax", null)

            }
            priorityMap.put("hasPlot", hasPlot)
            priorityMap.put("hasChildElements", hasChildElements)
            return priorityMap
        }
        return [priorityDisplay: '', priorityDescription: '', dictionaryId: null, hasPlot: false, hasChildElements: false, yNormMin: null, yNormMax: null]

    }

    Map extractExperimentDetails(List<Activity> activities, NormalizeAxis normalizeAxis = NormalizeAxis.Y_NORM_AXIS, ActivityOutcome activityOutcome = ActivityOutcome.ALL) {
        Double yNormMin = 0
        Double yNormMax = 0
        boolean hasPlot = false
        boolean hasChildElements = false
        String priorityDisplay = ''
        String dictionaryId
        for (Activity activity : activities) {

            final ResultData resultData = activity.resultData

            if (resultData) {
                Map priorityMap = extractMapFromResultData(resultData, normalizeAxis)
                if (priorityMap.yNormMin) {
                    if (priorityMap.yNormMin < yNormMin) {
                        yNormMin = priorityMap.yNormMin
                    }
                }
                if (priorityMap.yNormMax) {
                    if (priorityMap.yNormMax > yNormMax) {
                        yNormMax = priorityMap.yNormMax
                    }
                }
                if (priorityMap.hasPlot) {
                    hasPlot = true
                }
                if (priorityMap.hasChildElements) {
                    hasChildElements = true
                }
                if (priorityMap.dictionaryId) {
                    dictionaryId = priorityMap.dictionaryId
                }
                if (priorityMap.priorityDisplay) {
                    priorityDisplay = priorityMap.priorityDisplay
                }

            }
        }
        return [priorityDisplay: priorityDisplay, dictionaryId: dictionaryId,
                hasPlot: hasPlot, hasChildElements: hasChildElements,
                yNormMin: yNormMin, yNormMax: yNormMax]
    }

    Map extractPriorityDisplayDescription(PriorityElement priorityElement) {
        String priorityDisplay = priorityElement.getDictionaryLabel()
        String priorityDescription = priorityElement.getDictionaryDescription()
        Long dictionaryId = null
        if (priorityDescription) {
            dictionaryId = priorityElement.getDictElemId()
        }
        return [priorityDisplay: priorityDisplay, priorityDescription: priorityDescription, dictionaryId: dictionaryId]
    }

    //filters that starts with a number or '[' to denote ranges
    final static Pattern FILTER_NUMBER_RANGES = Pattern.compile("^(\\d+.*|-\\d+.*)");

    public void matchMLPProbe(final String term,final List<Map<String, String>> autoSuggestTerms){
        if (term.toUpperCase().contains(PROBE)) {
            final String label = "${term} as <strong> ML Probe</strong>"
            final String value = "ML_Probes"
            autoSuggestTerms.add([label: label, value: value])
        }
    }
    /**
     *
     * @param term
     * @param autoSuggestResponseFromJDO
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term, final Map<String, List<String>> autoSuggestResponseFromJDO) {
        final List<Map<String, String>> autoSuggestTerms = []
        if (term) {
           matchMLPProbe(term,autoSuggestTerms)
        }


        for (String key : autoSuggestResponseFromJDO.keySet()) {
            if (AUTO_SUGGEST_FILTERS.containsKey(key)) {
                final List<String> terms = autoSuggestResponseFromJDO.get(key)
                List<Map<String, String>> terms1 = this.getAutoSuggestTerms(AUTO_SUGGEST_FILTERS, terms, key)
                autoSuggestTerms.addAll(terms1)
            }
        }
        //we insert what the user has typed back into the map
        autoSuggestTerms.add(0, [label: term, value: term])
        //if the term is ML_Pro
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
        final MetaData metaData = compoundResult.metaData
        for (Compound compound : compoundResult.compounds) {
            Double score = null
            NameDescription nameDescription = null
            if (metaData) {
                score = metaData.getScore(compound.id.toString())
                nameDescription = metaData.getMatchingField(compound.id.toString())
            }
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound, score, nameDescription)
            compoundAdapters.add(compoundAdapter)
        }
        return compoundAdapters
    }
    /**
     * convert a list Assay's to a list of AssayAdapter's
     * @param assays {@link AssayResult}
     * @return list of {@link AssayAdapter}'s
     */
    public List<AssayAdapter> assaysToAdapters(final AssayResult assayResult) {
        final List<Assay> assays = assayResult.assays
        final MetaData metaData = assayResult.metaData
        if (assays) {
            return assaysToAdapters(assays, metaData)
        }
        return []
    }
    /**
     * convert a list Assay's to a list of AssayAdapter's
     * @param assays {@link Assay}
     * @param metaData {@link MetaData}
     * @return list of {@link AssayAdapter}'s
     */
    public List<AssayAdapter> assaysToAdapters(final List<AbstractAssay> assays, final MetaData metaData) {
        final List<AssayAdapter> assayAdapters = []
        for (AbstractAssay assay : assays) {
            Double score = null
            NameDescription nameDescription = null
            if (metaData) {
                score = metaData.getScore(assay.id.toString())
                nameDescription = metaData.getMatchingField(assay.id.toString())
            }
            final AssayAdapter assayAdapter = new AssayAdapter(assay, score, nameDescription)
            assayAdapters.add(assayAdapter)
        }
        return assayAdapters
    }

    public List<ProjectAdapter> projectsToAdapters(final ProjectResult projectResult) {
        final List<ProjectAdapter> projectAdapters = []
        final MetaData metaData = projectResult.metaData
        for (Project project : projectResult.projects) {
            Double score = null
            NameDescription nameDescription = null
            if (metaData) {
                score = metaData.getScore(project.id.toString())
                nameDescription = metaData.getMatchingField(project.id.toString())
            }
            final ProjectAdapter projectAdapter = new ProjectAdapter(project, score, nameDescription)
            projectAdapters.add(projectAdapter)
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
}