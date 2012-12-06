package querycart

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.Compound
import bardqueryapi.IQueryService
import bardqueryapi.QueryService
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartCompoundService)
@Unroll
class CartCompoundServiceUnitSpec extends Specification {

    IQueryService queryService
    @Shared CompoundAdapter compoundAdapter = new CompoundAdapter(new Compound(name: "name1", cid: 1L, numActiveAssay: 0, numAssay: 0))

    void setup() {
        queryService = Mock(QueryService)
        service.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test createCartCompoundFromCID #label"() {
        when:
        CartCompound returnedCartCompound = service.createCartCompoundFromCID(cid)

        then:
        1 * queryService.findCompoundsByCIDs([cid]) >> {[compoundAdapters: compoundAdapters]}
        assert returnedCartCompound?.name == expectedCartCompoundName
        assert returnedCartCompound?.externalId == expectedCartCompoundId

        where:
        label                      | cid  | compoundAdapters  | expectedCartCompoundName | expectedCartCompoundId
        "found a compoundAdapter"  | 123L | [compoundAdapter] | 'name1'                  | 1L
        "no compoundAdapter found" | 123L | []                | null                     | null
    }
}
