package bard.core.rest;

import bard.core.SuggestParams;


import static org.junit.Assert.assertTrue
import bard.core.rest.AbstractRESTServiceSpec;

/**
 * @author Rajarshi Guha
 */
public class RESTServicesSuggestIntegrationSpec extends AbstractRESTServiceSpec {


    SuggestParams constructSuggestParams() {
        return new SuggestParams("dna", 10);
    }
    void assertSuggestions(Map<String, List<String>> cs ){
        assertTrue("Should have 1 or more fields", !cs.isEmpty());
        for (String key : cs.keySet()) {
            List<String> suggestions = cs.get(key);
            assertTrue(key + " should have more than zero suggestions", !suggestions.isEmpty());
        }

    }
    /**
     * Copied from RESTTestServices#testServices10
     */
    void testCompoundSuggestions() {
        given:
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> cs = restCompoundService.suggest(suggestParams);
        then:
        assertSuggestions(cs);
     }

    void testAssaySuggestions() {
        given:
        //construct Search Params
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> assays = restAssayService.suggest(suggestParams);
        then:
        assertSuggestions(assays);
    }
    void testProjectSuggestions() {
        given:
        //construct Search Params
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> ps = restProjectService.suggest(suggestParams);
        then:
        assertSuggestions(ps);
    }


}
