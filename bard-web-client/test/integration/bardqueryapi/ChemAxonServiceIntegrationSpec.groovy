package bardqueryapi

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class ChemAxonServiceIntegrationSpec extends IntegrationSpec {

    ChemAxonService chemAxonService

    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }
    /**
     * {@link ChemAxonService#generateImageBytes(String, JChemBinFormat)}
     */
    void "test generateImageBytes with valid smiles"() {
        given:
        JChemBinFormat jchemBinFormat = new JChemBinFormat(width: 10, height: 10, imageFormat: 'png', transparencyBackground: true);

        when:
        final byte[] bytes = chemAxonService.generateImageBytes("CC", jchemBinFormat)
        then:
        assert bytes.length
    }
    /**
     * {@link ChemAxonService#generateImageBytes(String, JChemBinFormat)}
     */
    void "test generateImageBytes with empty smiles"() {
        given:
        JChemBinFormat jchemBinFormat = new JChemBinFormat(width: 10, height: 10, imageFormat: 'png', transparencyBackground: true);

        when:
        chemAxonService.generateImageBytes("", jchemBinFormat)
        then:
        Exception ee = thrown()
        assert ee
    }
    /**
     * {@link ChemAxonService#generateStructurePNG(String, Integer, Integer)}
     */
    void "test generateStructurePNG"() {
        when:
        final byte[] bytes = chemAxonService.generateStructurePNG("CC", 10, 10)
        then:
        assert bytes.length
    }

}
