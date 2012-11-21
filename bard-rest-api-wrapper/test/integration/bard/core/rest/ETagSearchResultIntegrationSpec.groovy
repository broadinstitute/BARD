package bard.core.rest

import bard.core.rest.helper.RESTTestHelper
import spock.lang.Unroll
import sun.security.acl.PrincipalImpl

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class ETagSearchResultIntegrationSpec extends AbstractRESTServiceSpec {


    void "test build #label"() {
        given:
        ETagSearchResult eTagSearchResult = new ETagSearchResult(this.restCompoundService, new PrincipalImpl("Some User"))
        when:
        final ETagSearchResult searchResult = eTagSearchResult.build()
        then:
        assert !searchResult.searchResults.isEmpty()
    }

}