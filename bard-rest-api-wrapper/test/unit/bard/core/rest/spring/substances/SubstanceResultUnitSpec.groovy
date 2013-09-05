package bard.core.rest.spring.substances

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubstanceResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String SUBSTANCE_RESULT = '''
    {
       "collection":
       [
           {
               "sid": 842121,
               "cid": 6603008,
               "depRegId": "MLS000076160",
               "sourceName": "MLSMR",
               "url": null,
               "patentIds": null,
               "smiles": "CCOCCCNCC(=O)NC1=CC=C(C=C1)OC(F)(F)F.Cl",
               "deposited": "2005-06-04",
               "updated": "2012-03-01",
               "resourcePath": "/substances/842121"
           },
           {
               "sid": 842122,
               "cid": 6602571,
               "depRegId": "MLS000033655",
               "sourceName": "MLSMR",
               "url": null,
               "patentIds": null,
               "smiles": "COCCN1C(=NN=N1)CN2CCC(CC2)CC3=CC=CC=C3.Cl",
               "deposited": "2005-06-04",
               "updated": "2012-03-01",
               "resourcePath": "/substances/842122"
           }
       ],
       "link": "/substances?skip=10&top=10&expand=true&filter=MLSMR"
    }
   '''

    void "test serialization to SubstanceResult"() {
        when:
        final SubstanceResult substanceResult = objectMapper.readValue(SUBSTANCE_RESULT, SubstanceResult.class)
        then:
        assert substanceResult
        assert substanceResult.substances
        assert substanceResult.substances.size() == 2
        Substance substance = substanceResult.substances.get(0)
        assert substance.getSid() == 842121
        assert substance.getId() == substance.getSid()
        assert substance.getCid() == 6603008
        assert substance.getDepRegId() == "MLS000076160"
        assert substance.getSourceName() == "MLSMR"
        assert !substance.getUrl()
        assert !substance.getPatentIds()
        assert substance.getSmiles() == "CCOCCCNCC(=O)NC1=CC=C(C=C1)OC(F)(F)F.Cl"
        assert substance.getDeposited() == "2005-06-04"
        assert substance.getUpdated() == "2012-03-01"
        assert substance.getResourcePath() == "/substances/842121"
        assert substanceResult.link == "/substances?skip=10&top=10&expand=true&filter=MLSMR"
    }
}

