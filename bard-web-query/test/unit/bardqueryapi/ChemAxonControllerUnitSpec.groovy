package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import bard.core.adapter.CompoundAdapter
import spock.lang.Shared
import bard.core.Compound
import bard.core.DataSource
import bard.core.LongValue

import bard.core.MolecularValue
import bard.core.interfaces.MolecularData
import bard.core.impl.MolecularDataJChemImpl

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ChemAxonController)
@Unroll
class ChemAxonControllerUnitSpec extends Specification {
    ChemAxonService chemAxonService
    QueryService queryService
    @Shared
    CompoundAdapter compoundAdapter = buildCompoundAdapter(1234, 'C1=CC2=C(C=C1)C=CC=C2')

    void setup() {
        chemAxonService = Mock(ChemAxonService)
        controller.chemAxonService = this.chemAxonService
        queryService = Mock(QueryService)
        controller.queryService = queryService
    }

    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test generateStructureImageFromSmiles #label"() {
        when:
//        controller.params.smiles = smiles
//        controller.params.width = width
//        controller.params.height = height
        controller.generateStructureImageFromSmiles(smiles, width, height)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        chemAxonService.generateStructurePNG(_, _, _) >> {bytesArra}
        assert returnedImage.size() == expectedByteArraySize

        where:
        label                          | smiles                  | width          | height         | bytesArra           | expectedByteArraySize
        "get back a byte array"        | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 3
        "default image size"           | 'C1=CC2=C(C=C1)C=CC=C2' | null           | null           | [1, 2, 3] as byte[] | 3
        "default image size"           | 'C1=CC2=C(C=C1)C=CC=C2' | 0 as Integer   | 0 as Integer   | [1, 2, 3] as byte[] | 3
        "get back an empty byte array" | 'C1=CC2=C(C=C1)C=CC=C2' | 200 as Integer | 200 as Integer | [] as byte[]        | 0
        "smiles is null"               | ''                      | 200 as Integer | 200 as Integer | [1, 2, 3] as byte[] | 0
    }

    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test generateStructureImage from CID #label"() {
        when:
//        controller.params.cid = cid
//        controller.params.width = width
//        controller.params.height = height
        controller.generateStructureImageFromCID(cid, width, height)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        queryService.showCompound(_) >> {compoundAdptr}
        chemAxonService.generateStructurePNG(_, _, _) >> {bytesArra}
        assert returnedImage.size() == expectedByteArraySize

        where:
        label                          | smiles                  | width          | height         | bytesArra           | cid          | expectedByteArraySize | compoundAdptr
        "get back a byte array"        | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 1234 as Long | 3                     | compoundAdapter
        "no compoundAdapters"          | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 1234 as Long | 0                     | null
        "get back an empty byte array" | 'C1=CC2=C(C=C1)C=CC=C2' | 200 as Integer | 200 as Integer | [] as byte[]        | 1234 as Long | 0                     | compoundAdapter
        "cid is null"                  | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | null         | 0                     | compoundAdapter
    }

    CompoundAdapter buildCompoundAdapter(final Long cid, final String smiles) {
        final Compound compound = new Compound()
        final DataSource source = new DataSource("stuff", "v1")
        compound.setId(cid);
        for (Long sid : [1, 2, 3]) {
            compound.addValue(new LongValue(source, Compound.PubChemSIDValue, sid));
        }
        // redundant
        compound.addValue(new LongValue(source, Compound.PubChemCIDValue, cid));
        MolecularData md = new MolecularDataJChemImpl();
        md.setMolecule(smiles);
        compound.addValue(new MolecularValue(source, Compound.MolecularValue, md));
        return compoundAdapter = new CompoundAdapter(compound)
    }

    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test index() #label"() {
        when:
        controller.params.smiles = smiles
        controller.index()

        then:
        assert controller.session.smiles == smiles

        where:
        label                  | smiles
        "with a simple smiles" | 'C1=CC2=C(C=C1)C=CC=C2'
    }

    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test marvinSketch()"() {
        when:
        controller.marvinSketch()

        then:
        assert controller.response.contentAsString == ''
    }
}
