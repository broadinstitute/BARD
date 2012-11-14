package jdo;


import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.*

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import bard.core.interfaces.SearchResult

/**
 * User: jasiedu
 * Date: 8/31/12
 * Time: 8:26 AM
 */

public class RESTServicesFilterIntegrationSpec extends AbstractRESTServiceSpec {


    SearchParams constructSearchParamsWithFilters() {
        //given the following filters
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["gobp_term", "DNA repair"] as String[])
        filters.add(["gobp_term", "response to UV-C"] as String[])
        //construct Search Params
        return new SearchParams("dna repair", filters);

    }

    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithCompoundService() {
        given:
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["tpsa", "55.1"] as String[])

        //construct Search Params
        final SearchParams searchParams = new SearchParams("dna repair", filters);

        when:
        SearchResult<Compound> compoundServiceResults = this.restCompoundService.search(searchParams);
        then:
        final List<Compound> compounds = compoundServiceResults.searchResults
        assert compounds, "CompoundService SearchResults must not be null"
        assert !compounds.isEmpty(), "CompoundService SearchResults must not be empty"
        assert compoundServiceResults.getCount(), "CompoundService SearchResults must have at least one element"

        for (Compound compound : compounds) {
            CompoundAdapter compoundAdapter = new CompoundAdapter(compound);
            assertTrue("Compound Adapter must have a name", compoundAdapter.getName() != null);
            assertTrue("Compound Adapter must have a Search highlight", compoundAdapter.getSearchHighlight() != null);
        }

        final Collection<Value> facets = compoundServiceResults.getFacets();
        assertTrue("List of Facets is not null", facets != null);
        assertTrue("List of Facets is not empty", !facets.isEmpty());

    }

    /**
     * We test the Collection Filters on Compounds
     * <p/>
     * Given a smiles string "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"
     * <p/>
     * When we do a superstructure search with skip=0,top = 10 and a threshold of 0.9
     * <p/>
     * then we expect to get back at least 3 structures with a facet of COLLECTION=Japan(2)
     * <p/>
     * <p/>
     * When we apply the facet to the search, we expect to get back 2 structures
     */
    void testFiltersWithCompoundServiceStructureSearch() {
        given:
        final String smiles = "Cc1ccc2nccc2c1";

        StructureSearchParams structureSearchParams =
            new StructureSearchParams(smiles, StructureSearchParams.Type.Superstructure);
        structureSearchParams.setSkip(new Long(0)).setTop(new Long(10));

        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["COLLECTION", "Japan"] as String[])
        StructureSearchParams structureSearchParams2 =
            new StructureSearchParams(smiles, StructureSearchParams.Type.Superstructure);
        structureSearchParams2.setSkip(new Long(0)).setTop(new Long(10));
        structureSearchParams2.setThreshold(0.9);
        when:
        SearchResult<Compound> searchResults = this.restCompoundService.search(structureSearchParams);

        and:
        SearchResult<Compound> searchResults2 = this.restCompoundService.search(structureSearchParams2);

        then:
        Collection<Value> facets = searchResults.getFacets();
        assertTrue("That there should be facets", facets != null);
        assertFalse("The Facets are not empty", facets.isEmpty());
        long numberOfHitsWithNoFilters = searchResults.getCount();
        assertTrue("We expect one or more hits", numberOfHitsWithNoFilters > 0);

        //So now lets apply filters with COLLECTION=Japan
        //we expect that the number of results returned is 2
        long numberOfHitsWithFilters = 0;
        final List<Compound> compounds = searchResults2.searchResults
        for (Compound c : compounds) {
            CompoundAdapter ca = new CompoundAdapter(c);
            Collection<Value> annots = ca.getAnnotations();
            for (Value anno : annots) {
                String key = anno.getId();
                String value = (String) anno.getValue();
                if (key.toLowerCase().equals("collection") && value.trim().toLowerCase().equals("japan"))
                    numberOfHitsWithFilters++;
            }
        }
        assertTrue("We expect the number of hits with no filters to be greater than the number of hits with filters", numberOfHitsWithNoFilters > numberOfHitsWithFilters);
    }

    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithAssayServiceParenthesisInFilterValue() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method
        //There are at least 7 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["target_name", "\"DNA (cytosine-5)-methyltransferase 1\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
//        SearchResult<Assay> assayServiceSearchResultsWithNoFilters = this.restAssayService.search(searchParamsWithNoFilters);
//        and:
        final SearchResult<Assay> assayServiceSearchResultsWithFilters = this.restAssayService.search(searchParamsWithFilters);

        then:

        final long countWithFilters = assayServiceSearchResultsWithFilters.getCount();
        assertTrue("Count with Filters Should be greater than zero but we found " + countWithFilters, countWithFilters > 0);

    }

    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithAssayService() {
        given:
        //construct Search Params
        final SearchParams searchParams = constructSearchParamsWithFilters();

        when:
        SearchResult<Assay> assayServiceSearchResults = this.restAssayService.search(searchParams);

        then:
        final List<Assay> assays = assayServiceSearchResults.searchResults
        assertTrue("AssayService SearchResults from 'DNA repair query' must not be null", assayServiceSearchResults != null);
        assertTrue("AssayService SearchResults from 'DNA repair query' must not be empty", assays.size() > 0);
        assertTrue("AssayService SearchResults from 'DNA repair query' must have at least one element", assayServiceSearchResults.getCount() >= 1);
        for (Assay assay : assays) {
            AssayAdapter assayAdapter = new AssayAdapter(assay);
            assertTrue("Assay Adapter from 'DNA repair query' must have a name", assayAdapter.getName() != null);
            assertTrue("Assay Adapter from 'DNA repair query' must have a Search highlight", assayAdapter.getSearchHighlight() != null);
        }
        final Collection<Value> facets = assayServiceSearchResults.getFacets();
        assertTrue("List of Facets from 'DNA repair query' is not null", facets != null);
        assertTrue("List of Facets from 'DNA repair query' is not empty", !facets.isEmpty());

    }

    /**
     * Test filter application
     * We do a search without filters, then we do one with known filters and compare the results
     * They should not return the same number of results
     */
    void testApplyFiltersWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method
        //There are at least 7 of them
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"spectrophotometry method\""] as String[])


        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);
        when:
        SearchResult<Assay> assayServiceSearchResultsWithNoFilters = this.restAssayService.search(searchParamsWithNoFilters);
        and:
        final SearchResult<Assay> assayServiceSearchResultsWithFilters = this.restAssayService.search(searchParamsWithFilters);
        then:
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.getCount();
        final long countWithFilters = assayServiceSearchResultsWithFilters.getCount();
        long counter = countWithNoFilters - countWithFilters;
        assertTrue("There should be difference in the number of hits, between a query with no filters and one with filters", counter > 0);
    }

    /**
     * Test filter application
     * We expect an OR when we use one or more filters from the same category
     * <p/>
     * Here we use "spectrophotometry method"  and  "image-based" both from the
     * detection method category
     */
    void testApplyMultipleFiltersWithinTheSameCategoryWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsWithNoFilters = new SearchParams("\"dna repair\"");
        searchParamsWithNoFilters.setSkip(new Long(0));
        searchParamsWithNoFilters.setTop(new Long(10));

        //now apply filters with detection method type=spectrophotometry method or detection method type= image-based
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection_method_type", "\"spectrophotometry method\""] as String[])
        filters.add(["detection_method_type", "\"image-based\""] as String[])

        final SearchParams searchParamsWithFilters = new SearchParams("\"dna repair\"");
        searchParamsWithFilters.setSkip(new Long(0));
        searchParamsWithFilters.setTop(new Long(10));
        searchParamsWithFilters.setFilters(filters);

        when:
        SearchResult<Assay> assayServiceSearchResultsWithNoFilters = this.restAssayService.search(searchParamsWithNoFilters);
        and:
        final SearchResult<Assay> assayServiceSearchResultsWithFilters = this.restAssayService.search(searchParamsWithFilters);
        then:
        final long countWithNoFilters = assayServiceSearchResultsWithNoFilters.getCount();
        final long countWithFilters = assayServiceSearchResultsWithFilters.getCount();
        long counter = countWithNoFilters - countWithFilters;
        assertTrue("We expect the difference to be greater than zero", counter > 0);
    }

    /**
     * typing in zinc, and choosing the auto-suggest option:
     * zinc ion binding as GO molecular function term
     * <p/>
     * Then choose the top for filters under detection method:
     * homogeneous time-resolved fluorescence (1)
     * fluorescence polarization (2)
     * fluorescence intensity (8)
     * bioluminescence (9)
     * These filters sum to 20 items, but after applying them the system has 120 items
     * Bug https://www.pivotaltracker.com/story/show/36709723
     *
     * TODO: Uncomment when NCGC releases version 8
     */
//    void testMultipleFilters() {
//        given:
//        //Do a search with no filters
//        final SearchParams searchParamsWithNoFilters = new SearchParams("\"zinc ion binding\"");
//        searchParamsWithNoFilters.setSkip(new Long(0));
//        searchParamsWithNoFilters.setTop(new Long(10));
//
//        //now apply filters with detection method type=spectrophotometry method or detection method type= image-based
//        final List<String[]> filters = new ArrayList<String[]>();
//        filters.add(["detection_method_type", "\"homogeneous time-resolved fluorescence\""] as String[])
//        filters.add(["detection_method_type", "\"fluorescence polarization\""] as String[])
//        filters.add(["detection_method_type", "\"fluorescence intensity\""] as String[])
//        filters.add(["detection_method_type", "\"bioluminescence\""] as String[])
//
//        final SearchParams searchParamsWithFilters = new SearchParams("\"zinc ion binding\"");
//        searchParamsWithFilters.setSkip(new Long(0));
//        searchParamsWithFilters.setTop(new Long(10));
//        searchParamsWithFilters.setFilters(filters);
//
//        when:
//        SearchResult<Assay> assayServiceSearchResultsWithNoFilters = this.restAssayService.search(searchParamsWithNoFilters);
//        and:
//        final SearchResult<Assay> assayServiceSearchResultsWithFilters = this.restAssayService.search(searchParamsWithFilters);
//        then:
//        while (assayServiceSearchResultsWithNoFilters.hasNext()) {
//            assayServiceSearchResultsWithNoFilters.next();
//        }
//        Collection<Value> facets = assayServiceSearchResultsWithNoFilters.getFacets();
//        int countHomogenous = 0;
//        int countFluorPol = 0;
//        int countFluorIntensity = 0;
//        int countbioluminescence = 0;
//        for (Value value : facets) {
//            if (value.getId().equals("detection_method_type")) {
//
//                Value child = value.getChild("homogeneous time-resolved fluorescence");
//                countHomogenous = new Integer(child.getValue().toString());
//
//                child = value.getChild("fluorescence polarization");
//                countFluorPol = new Integer(child.getValue().toString());
//
//                child = value.getChild("fluorescence intensity");
//                countFluorIntensity = new Integer(child.getValue().toString());
//
//                child = value.getChild("bioluminescence");
//                countbioluminescence = new Integer(child.getValue().toString());
//            }
//        }
//        int sumOfFiltersReturnedIntiallyBySystem = countbioluminescence + countFluorIntensity + countFluorPol + countHomogenous;
//
//        while (assayServiceSearchResultsWithFilters.hasNext()) {
//            assayServiceSearchResultsWithFilters.next();
//        }
//        final long countWithFilters = assayServiceSearchResultsWithFilters.getCount();
//
//        assertTrue("The total number of hits after applying the filters," + countWithFilters + "  should not exceed the sum of " +
//                "the filters returned by the system, " + sumOfFiltersReturnedIntiallyBySystem + " prior to applying the filters", countWithFilters <= sumOfFiltersReturnedIntiallyBySystem);
//    }
//
    /**
     * Test filter application
     * We do a search without filters, then we do one with known filters and compare the results
     * They should not return the same number of results
     */
    void testApplyMultipleFiltersFromDifferentCategoriesWithAssayService() {
        given:
        //Do a search with no filters
        final SearchParams searchParamsFilters = new SearchParams("\"dna repair\"");
        searchParamsFilters.setSkip(new Long(0));
        searchParamsFilters.setTop(new Long(10));

        //now apply filters with detection_method_type="spectrophotometry method" and  detection_method_type=" image-based"
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["detection method type", "\"spectrophotometry method\""] as String[])
        filters.add(["assay component role", "\"target\""] as String[])

        searchParamsFilters.setFilters(filters);
        when:
        final SearchResult<Assay> assayServiceSearchResultsWithFilters = this.restAssayService.search(searchParamsFilters);
        then:
        final long countWithFilters = assayServiceSearchResultsWithFilters.getCount();
        assertTrue("Expect at least one filter", countWithFilters > 0);
    }

    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithProjectService() {
        given:
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["num_expt", "6"] as String[])

        //construct Search Params
        final SearchParams searchParams = new SearchParams("dna repair", filters);
        when:
        SearchResult<Project> projectServiceSearchResults = this.restProjectService.search(searchParams);
        then:
        List<Project> projects = projectServiceSearchResults.searchResults
        assertTrue("ProjectService SearchResults must not be null", projectServiceSearchResults != null);
        assertTrue("ProjectService SearchResults must not be empty", !projects.isEmpty());
        assertTrue("ProjectService SearchResults must have at least one element", projectServiceSearchResults.getCount() >= 1);
        for (Project project : projects) {
            ProjectAdapter projectAdapter = new ProjectAdapter(project);
            assertTrue("Project Adapter must have a name", projectAdapter.name != null);
            assertTrue("Project Adapter must have a Search highlight", projectAdapter.getSearchHighlight() != null);
        }
        final Collection<Value> facets = projectServiceSearchResults.getFacets();
        assertTrue("List of Facets is not null", facets != null);
        assertTrue("List of Facets is not empty", !facets.isEmpty());

    }

}
