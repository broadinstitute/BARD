package bard.core.rest

import bard.core.Compound
import bard.core.StructureSearchParams
import bard.core.rest.helper.RESTTestHelper
import spock.lang.Unroll

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class StructureSearchResultIntegrationSpec extends AbstractRESTServiceSpec {

   // RESTCompoundService restCompoundService

    void "test build #label"() {
        given:
        StructureSearchParams params = new StructureSearchParams(smiles, structureType)
        params.skip = skip
        params.top = top
        StructureSearchResult structureSearchResult = new StructureSearchResult(this.restCompoundService, params)
        when:
        final StructureSearchResult<Compound> searchResult = structureSearchResult.build()
        then:
        assert searchResult.searchResults.size() == expectedSize
        assert searchResult.getETag()
        where:
        label                                | skip | top  | expectedSize | structureType                           | smiles
        "With Skip and Top"                  | 0    | 5    | 5            | StructureSearchParams.Type.Substructure | "n1cccc2ccccc12"
        "Skip and Top both null"             | null | null | 1            | StructureSearchParams.Type.Exact        | "n1cccc2ccccc12"
        "Skip and Top both , does not exist" | null | null | 0            | StructureSearchParams.Type.Exact        | "c"
    }

}