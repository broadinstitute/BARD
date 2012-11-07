package querycart

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.IQueryService
import spock.lang.Shared
import bardqueryapi.QueryService
import bard.core.adapter.CompoundAdapter
import bard.core.Compound
import bard.core.DataSource
import bard.core.MolecularData
import bard.core.impl.MolecularDataJChemImpl
import bard.core.MolecularValue

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartCompoundService)
@Unroll
class CartCompoundServiceUnitSpec extends Specification {

    IQueryService queryService
    @Shared CompoundAdapter compoundAdapter = new CompoundAdapter(new Compound("name1"))

    void setup() {
        queryService = Mock(QueryService)
        service.queryService = this.queryService
        this.compoundAdapter.compound.setId(1L)
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
        label                      | cid  | compoundAdapters       | expectedCartCompoundName | expectedCartCompoundId
        "found a compoundAdapter"  | 123L | [this.compoundAdapter] | 'name1'                  | 1L
        "no compoundAdapter found" | 123L | []                     | null                     | null
    }
}
