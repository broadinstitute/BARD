package bardqueryapi

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class QueryServiceHelperIntegrationSpec extends IntegrationSpec {

    QueryHelperService queryHelperService

    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }
    /**
     * {@link QueryHelperService#findFiltersInSearchBox(List, String)}
     */
    void "test find Filters In SearchBox"() {
        given:
        List<SearchFilter> searchFilters = []
        String searchString = "gobp_term:DNA Repair"
        when:
        this.queryHelperService.findFiltersInSearchBox(searchFilters, searchString)
        then:
        assert !searchFilters.isEmpty()

    }
    void "test get Auto Suggest Term with null values"(){
        when:
        final List<Map<String, String>> terms = this.queryHelperService.getAutoSuggestTerms(null, [null, null], null)
        then:
        assert terms.isEmpty()
    }

}
