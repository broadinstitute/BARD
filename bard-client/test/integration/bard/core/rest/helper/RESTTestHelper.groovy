package bard.core.rest.helper

import bard.core.Entity
import bard.core.Value
import bard.core.interfaces.SearchResult

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/30/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class RESTTestHelper {
    /**
     * Assert that facets exists
     * @param searchResults
     */
    public void assertFacets(final SearchResult<? extends Entity> searchResults) {
        assertFacets(searchResults.facets)
    }
    /**
     * Assert that facets exists
     * @param serviceIterator
     */
    public void assertFacets(final Collection<Value> facets) {
        assert facets
        for (Value facet : facets) {
            assert facet.getChildren()
        }
    }
    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param searchResults
     */
    public void assertFacetIdsAreUnique(final SearchResult<? extends Entity> searchResults) {
        final Collection<Value> facets = searchResults.facets
        assertFacetIdsAreUnique(facets)
    }
    /**
     * Assert that there are no duplicate keys in facets (We skip blank and nulll keys those are tested #assertFacetIdsAreNonBlank)
     * @param serviceIterator
     */
    public void assertFacetIdsAreUnique(final Collection<Value> facets) {
        //we keep the parentFacet keys here to check for duplicates
        final Set<String> facetKeys = new HashSet<String>()
        for (Value parentFacet : facets) {
            final String parentFacetId = parentFacet.getId()
            if (parentFacetId) {
                assert !facetKeys.contains(parentFacetId)
                facetKeys.add(parentFacetId)
            }
            for (final Value childFacet : parentFacet.getChildren()) {
                final String childFacetId = childFacet.getId()
                if (childFacetId) {
                    String uniqueChildId = parentFacetId + "-" + childFacetId //Uniqueness should only be checked between facets with the same parent
                    assert !facetKeys.contains(uniqueChildId)
                    facetKeys.add(uniqueChildId)
                }
            }
        }
    }
}
