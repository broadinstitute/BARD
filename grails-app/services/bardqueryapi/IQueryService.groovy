package bardqueryapi

import bard.core.StructureSearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter

public interface IQueryService {


 Map findPromiscuityScoreForCID(final Long cid);
    //========================================================== Free Text Searches ================================
    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return
     */
    Map findCompoundsByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    /**
     * We can use a trick to get more than 10 records
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findAssaysByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findProjectsByTextSearch(final String searchString, final Integer top, final Integer skip, final List<SearchFilter> searchFilters);

    //====================================== Structure Searches ========================================
    /**
     * @param smiles
     * @param structureSearchParamsType
     * @param top
     * @param skip
     * @return of compounds
     */
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final List<SearchFilter> searchFilters, final int top, final int skip);

    //===================== Find Resources given a list of IDs ================================
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @return list
     */
    Map findCompoundsByCIDs(final List<Long> compoundIds, List<SearchFilter> filters);

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @return list
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters);

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @return list
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters);
    //=============== Show Resources Given a Single ID ================

    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return CompoundAdapter
     */
    CompoundAdapter showCompound(final Long compoundId);

    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return AssayAdapter
     */
    Map showAssay(final Long assayId);
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return ProjectAdapter
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
     * Extract filters from the search string if any
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString);

}