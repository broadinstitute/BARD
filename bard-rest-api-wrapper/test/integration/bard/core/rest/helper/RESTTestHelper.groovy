package bard.core.rest.helper

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.compounds.Compound

import bard.core.rest.spring.project.Project
import bard.core.rest.spring.util.Counts
import bard.core.rest.spring.util.Facet

import static org.junit.Assert.assertTrue
import bard.core.rest.spring.compounds.CompoundResult

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/30/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class RESTTestHelper {
    /**
     * Copied from RESTTestServices#testServices9
     */
    SearchParams constructSearchParamsWithFilters() {
        //given the following filters
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["gobp_term", "DNA repair"] as String[])
        filters.add(["gobp_term", "response to UV-C"] as String[])
        //construct Search Params
        return new SearchParams("dna repair", filters);

    }

    SuggestParams constructSuggestParams() {
        return new SuggestParams("dna", 10);
    }

    void assertSuggestions(Map<String, List<String>> cs) {
        assertTrue("Should have 1 or more fields", !cs.isEmpty());
        for (String key : cs.keySet()) {
            if (key != "query") {
                List<String> suggestions = cs.get(key);
                assertTrue(key + " should have more than zero suggestions", !suggestions.isEmpty());
            }
        }

    }
    /**
     *
     * @param assays
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertAssays(List<ExpandedAssay> assays) {
        for (ExpandedAssay assay : assays) {
            assertAssay(assay)
        }
    }
    /**
     *
     * @param assays
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertAssaySearches(List<Assay> assays) {
        for (Assay assay : assays) {
            assertAssaySearch(assay)
        }
    }
    /**
     *
     * @param assay
     * @param isFreeTextSearch
     */
    void assertAssay(final ExpandedAssay assay) {
        assert assay.aid
        assert assay.comments
        assert assay.name
        assert assay.description
        assert assay.category
        assert assay.type >= 0
        assert assay.experiments
        assert assay.projects
    }
    /**
     * @param assaySearch
     * @param isFreeTextSearch
     */
    void assertAssaySearch(final Assay assaySearch) {
        assert assaySearch.id
        assert assaySearch.name
        // assert assaySearch.description
        assert assaySearch.category >= 0
        assert assaySearch.type >= 0
        assert assaySearch.experiments != null
        assert assaySearch.projectIds != null
        //assert assaySearch.documents
    }
    /**
     *
     * @param compounds
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertCompounds(List<Compound> compoundTemplates) {
        for (Compound compoundTemplate : compoundTemplates) {
            assertCompound(compoundTemplate)
        }
    }

    void assertCompound(final Compound compoundTemplate) {
        assert compoundTemplate.cid
        assert compoundTemplate.smiles
        assert compoundTemplate.name
        assert compoundTemplate.mwt
        assert compoundTemplate.compoundClass
        assert compoundTemplate.exactMass
        assert compoundTemplate.numActiveAssay >= 0
        assert compoundTemplate.numAssay >= 0
        assert compoundTemplate.hbondDonor >= 0
        assert compoundTemplate.rotatable >= 0
        assert compoundTemplate.hbondAcceptor >= 0
        assert compoundTemplate.tpsa

    }
    /**
     * Assert that facets exists
     * @param searchResults
     */
    public void assertFacets(final List<Facet> facets) {
        assert facets
        for (Facet facet : facets) {
            assert facet
            assert facet.facetName
            assert facet.counts
            assert facet.counts.additionalProperties
        }
    }

    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param searchResults
     */
    public void assertFacetIdsAreUnique(final CompoundResult compoundResult) {
        final List<Facet> facets = compoundResult.facets
        assertFacetIdsAreUnique(facets)
    }
    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param facets
     */
    public void assertFacetIdsAreUnique(final List<Facet> facets) {
        //we keep the parentFacet keys here to check for duplicates
        final Set<String> facetKeys = new HashSet<String>()
        for (Facet parentFacet : facets) {
            final String parentFacetId = parentFacet.facetName
            if (parentFacetId) {
                assert !facetKeys.contains(parentFacetId)
                facetKeys.add(parentFacetId)
            }

            final Counts counts = parentFacet.counts
            for (final String childFacet : counts.getAdditionalProperties().keySet()) {
                if (childFacet) {
                    String uniqueChildId = parentFacetId + "-" + childFacet //Uniqueness should only be checked between facets with the same parent
                    assert !facetKeys.contains(uniqueChildId)
                    facetKeys.add(uniqueChildId)
                }
            }
        }
    }

    /**
     *
     * @param assays
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertProjects(final List<Project> projects, final boolean isStringSearch = false) {
        for (Project project : projects) {
            assertProject(project, isStringSearch)
        }
    }
    /**
     *
     * @param project
     * @param isStringSearch = true if this is a free text search
     */
    void assertProject(final Project project, boolean isStringSearch = false) {
        assert project.id != null
        assert project.name
        assert project.description
        assert project.eids
        assert project.aids
        assert project.experimentCount
//        if (isStringSearch) {
//            assert project.getAk_dict_label()
//            assert project.getAv_dict_label()
//        }
    }
}
