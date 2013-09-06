package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class JChemBinFormatUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     * {@link JChemBinFormat#getExportOptions()}
     */
    void "test getExportOptions #label"() {
        JChemBinFormat jChemBinFormat = new JChemBinFormat(transparencyBackground: transparencyBackground)

        when:
        final String options = jChemBinFormat.exportOptions
        then: "The expected hashCode is returned"
        assert options
        assert options == expectedOptions
        where:
        label                                | transparencyBackground | expectedOptions
        "With transparencyBackground==true"  | true                   | "w100,h100,H_hetero,chiral_all,ez,transbg"
        "With transparencyBackground==false" | false                  | "w100,h100,H_hetero,chiral_all,ez"

    }
    /**
     * {@link JChemBinFormat#getExportOptions()}
     */
    void "test toString #label"() {
        JChemBinFormat jChemBinFormat = new JChemBinFormat(transparencyBackground: transparencyBackground)

        when:
        final String options = jChemBinFormat.toString()
        then: "The expected hashCode is returned"
        assert options
        assert options == expectedToString
        where:
        label                                | transparencyBackground | expectedToString
        "With transparencyBackground==true"  | true                   | "png:w100,h100,H_hetero,chiral_all,ez,transbg"
        "With transparencyBackground==false" | false                  | "png:w100,h100,H_hetero,chiral_all,ez"

    }
}

