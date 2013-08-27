package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.PromiscuityScaffold
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.Promiscuity

@Unroll
class PromiscuityUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String PROMISCUITY = '''
    {
        "hscafs":
        [
           {
               "wTested": 17064,
                "sActive": 51,
                "wActive": 1876,
                "aTested": 479,
                "sTested": 51,
                "pScore": 2445,
                "scafid": 46705,
                "aActive": 222,
                "inDrug": true,
                "smiles": "O=C1C=CC(=N)c2ccccc12"
            }
        ],
        "cid": 752424
     }
     '''

    void "test serialization to Promiscuity"() {
        when:
        final Promiscuity promiscuity = objectMapper.readValue(PROMISCUITY, Promiscuity.class)
        then:
        assert promiscuity.cid == 752424
        final List<PromiscuityScaffold> scaffolds = promiscuity.getPromiscuityScaffolds()
        assert scaffolds
        PromiscuityScaffold promiscuityScaffold = scaffolds.get(0)
        assert 17064 == promiscuityScaffold.testedWells
        assert 51 == promiscuityScaffold.activeSubstances
        assert 1876 == promiscuityScaffold.activeWells
        assert 479 == promiscuityScaffold.testedAssays
        assert 51 == promiscuityScaffold.testedSubstances
        assert 2445 == promiscuityScaffold.promiscuityScore
        assert 46705 == promiscuityScaffold.scaffoldId
        assert 222 == promiscuityScaffold.activeAssays
        assert promiscuityScaffold.inDrug
        assert "O=C1C=CC(=N)c2ccccc12" == promiscuityScaffold.smiles
    }


}

