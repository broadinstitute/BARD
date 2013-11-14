package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.Compound
import chemaxon.formats.MolFormatException
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.image.BufferedImage

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ChemAxonService)
@Unroll
class ChemAxonServiceUnitSpec extends Specification {
    final static String MOL_STRING = '-0.7145   -1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0'

    final static SDF_STRING = '10 11  0  0  0  0            999 V2000'

    void setup() {
    }

    /**
     * {@link ChemAxonController#generateStructureImageFromCID}
     */
    void "test converting SMILES structure to another format #label"() {
        when:
        String newFormatString = service.convertSmilesToAnotherFormat(smiles, format)

        then:
        if (newFormatString) {
            assert newFormatString.contains(expectedResponseString)
        } else {
            assert newFormatString == expectedResponseString
        }

        where:
        label                                        | smiles                  | format   | expectedResponseString
        "a good smiles string to MOL format"         | 'C1=CC2=C(C=C1)C=CC=C2' | 'mol'    | MOL_STRING
        "a good smiles string back to smiles format" | 'C1=CC2=C(C=C1)C=CC=C2' | 'smiles' | 'C1=CC2=C(C=C1)C=CC=C2'
        "a good smiles string to SDF format"         | 'C1=CC2=C(C=C1)C=CC=C2' | 'sdf'    | SDF_STRING
        "a null smiles string"                       | null                    | 'mol'    | null
        "a null format string"                       | 'C1=CC2=C(C=C1)C=CC=C2' | ' '      | null
    }


    void "test converting SMILES structure to another format with exception #label"() {
        when:
        service.convertSmilesToAnotherFormat(smiles, format)

        then:
        thrown(MolFormatException)

        where:
        label                 | smiles       | format
        "a bad smiles string" | 'bad smiles' | 'mol'
    }
}
