package bard.core.rest.spring.substances

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubstanceUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String SUBSTANCE = '''
    {
       "sid": 6820697,
       "cid": 600,
       "depRegId": "6914582",
       "sourceName": "ChemDB",
       "url": "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582",
       "patentIds":[ "SSS"],
       "smiles": "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O",
       "deposited": "2005-09-16",
       "updated": "2005-09-16",
       "resourcePath": "/substances/6820697"
    }
   '''
    final String SUBSTANCES = '''
   [
       {
           "sid": 6820697,
           "cid": 600,
           "depRegId": "6914582",
           "sourceName": "ChemDB",
           "url": "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582",
           "patentIds": ["SSS"],
           "smiles": "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O",
           "deposited": "2005-09-16",
           "updated": "2005-09-16",
           "resourcePath": "/substances/6820697"
       },
       {
           "sid": 24438962,
           "cid": 600,
           "depRegId": "580",
           "sourceName": "ChemSpider",
           "url": "http://www.chemspider.com/Chemical-Structure.580.html",
           "patentIds": null,
           "smiles": "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O",
           "deposited": "2007-05-07",
           "updated": "2007-12-04",
           "resourcePath": "/substances/24438962"
       }
   ]
   '''

    void "test serialization to Substance"() {
        when:
        final Substance substance = objectMapper.readValue(SUBSTANCE, Substance.class)
        then:
        assert substance.getId() == 6820697
        assert substance.getSid() == substance.getId()
        assert substance.getCid() == 600
        assert substance.getDepRegId() == "6914582"
        assert substance.getSourceName() == "ChemDB"
        assert substance.getUrl() == "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582"
        assert substance.getPatentIds() == ["SSS"]
        assert substance.getSmiles() == "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O"
        assert substance.getDeposited() == "2005-09-16"
        assert substance.getUpdated() == "2005-09-16"
        assert substance.getResourcePath() == "/substances/6820697"
    }
    /**
     * Get all substances for cid 600
     * Example : http://bard.nih.gov/api/v10/substances/cid/600?expand=true
     */
    void "test serialization to Substances"() {
        when:
        final List<Substance> substances = (List<Substance>)objectMapper.readValue(SUBSTANCES, Substance[].class)
        then:
        assert substances
        assert substances.size() == 2
        Substance substance = substances.get(0)
        assert substance.getId() == 6820697
        assert substance.getSid() == substance.getId()
        assert substance.getCid() == 600
        assert substance.getDepRegId() == "6914582"
        assert substance.getSourceName() == "ChemDB"
        assert substance.getUrl() == "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582"
        assert substance.getPatentIds() == ["SSS"]
        assert substance.getSmiles() == "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O"
        assert substance.getDeposited() == "2005-09-16"
        assert substance.getUpdated() == "2005-09-16"
        assert substance.getResourcePath() == "/substances/6820697"
    }


}

