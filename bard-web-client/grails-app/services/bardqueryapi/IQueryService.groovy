package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.util.StructureSearchParams
import bard.core.rest.spring.compounds.CompoundSummary

public interface IQueryService {

    Map getProjectSteps(final Long pid);
    /**
     *
     * @param cid
     * @return {@link bard.core.rest.spring.compounds.CompoundSummary}
     */
    public CompoundSummary getSummaryForCompound(final Long cid);


    List<Long> findSubstancesByCid(Long cid);
    /**
     *
     * @param cid
     * return Map
     * Success would return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return [status: HHTTP Error Code, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
     */
    Map findPromiscuityScoreForCID(final Long cid);
    /**
     *
     * @param cid
     * return Map
     * Success would return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return [status: HHTTP Error Code, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
     */
    Map findPromiscuityForCID(final Long cid);

    //========================================================== Free Text Searches ================================
    /**
     * Find Compounds by Text search
     *
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map of results
     */
    Map findCompoundsByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);
    /**
     *
     * @param cids
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map of results
     */
    Map searchCompoundsByCids(final List<Long> cids, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

        /**
     *
     * @param capAssayIds
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map of results
     */
    Map findAssaysByCapIds(final List<Long> capAssayIds, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    /**
     * @param capProjectIds
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map of results
     */
    Map findProjectsByCapIds(final List<Long> capProjectIds, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    /**
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map of results
     */
    Map findAssaysByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    /**
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map
     */
    Map findProjectsByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    //====================================== Structure Searches ========================================
    /**
     * @param cid
     * @param structureSearchParamsType {@link StructureSearchParams}
     * @param top
     * @param skip
     * @param nhits - The number of hits if we already have it
     * @return Map
     */
    Map structureSearch(Integer cid, StructureSearchParams.Type structureSearchParamsType, Double threshold, List<SearchFilter> searchFilters, Integer top, Integer skip, Integer nhits);

    Map showProbeList()
    /**
     * @param smiles
     * @param structureSearchParamsType {@link StructureSearchParams}
     * @param top
     * @param skip
     * @return Map
     */
    Map structureSearch(String smiles, StructureSearchParams.Type structureSearchParamsType, List<SearchFilter> searchFilters, Double threshold, Integer top, Integer skip, Integer nhits);

    //===================== Find Resources given a list of IDs ================================
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @param filters {@link SearchFilter}'s
     * @return Map
     */
    Map findCompoundsByCIDs(final List<Long> compoundIds, List<SearchFilter> filters);

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @param filters {@link SearchFilter}'s
     * @return map
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters);
    /**
     * Used for Show Experiment Page. Perhaps we should move this to the Query Service
     * @param experimentId
     * @param top
     * @param skip
     * @return Map of data to use to display an experiment
     */
    Map findExperimentDataById(Long experimentId, Integer top, Integer skip, NormalizeAxis normalizeAxis, ActivityOutcome activityOutcome);

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @param filters {@link SearchFilter}'s
     * @return Map
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters);
    //=============== Show Resources Given a Single ID ================

    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return {@link CompoundAdapter}
     */
    CompoundAdapter showCompound(final Long compoundId);

    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return Map
     */
    Map showAssay(final Long assayId);
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return Map
     */
    Map showProject(final Long projectId);

    //==============================================Auto Complete ======
    /**
     *
     * @param term
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term);
    /**
     *
     * Extract filters from the search string if any
     * @param searchFilters {@link SearchFilter}'s
     * @param searchString
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString);

    public QueryHelperService getQueryHelperService()
}