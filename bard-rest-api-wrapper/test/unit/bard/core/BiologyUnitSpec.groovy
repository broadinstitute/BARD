package bard.core

import bard.core.interfaces.BiologyType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BiologyUnitSpec extends Specification {
    @Shared BiologyType defaultBiologyTpe = BiologyType.Protein
    @Shared BiologyType cellLineBiologyTpe = BiologyType.CellLine
    @Shared String biologyName = "biology"

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test Constructors #label"() {
        when:
        Biology currentBiology = biology
        then:
        assert currentBiology.name == expectedName
        assert currentBiology.type == expectedBiologyType
        where:
        label         | biology                  | expectedName | expectedBiologyType
        "No arg ctor" | new Biology()            | null         | defaultBiologyTpe
        "1 arg ctor"  | new Biology(biologyName) | biologyName  | defaultBiologyTpe
    }

    void "test setType"() {
        given:
        Biology biology = new Biology(biologyName)
        when:
        biology.setType(cellLineBiologyTpe)
        then:
        assert biology.name == biologyName
        assert biology.type == cellLineBiologyTpe

    }

}

