package elasticsearchplugin

import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ElasticSearchService)
class ElasticSearchServiceUnitSpec extends Specification {

    QueryExecutorService queryExecutorService

    final static jsonAssayResponseString = '''{"hits":
        {"hits": [
            {"_index": "assays", "_type": "compound", "_id": 644, "_source": {"cids": [1234, 5678]}},
            {"_index": "assays", "_type": "assay", "_id": 644, "_source":
                {"aid": 644, "title": "Kinetic mechanism", "targets": [
                    {"resourcePath": "/bard/rest/v1/targets/accession/O75116", "taxId": 9606, "acc": "O75116"}]
                }
            }]
        }
    }'''


    final static jsonXCompound = '''
            {"_index":"compound","_type":"xcompound","_id":"cJrTWNb_TZupO_xDgE2NTw", "_score":0.13223398, "_source" :
                {smiles: "CC(=O)CCC1=CC=C(C=C1)OC(=O)C2=CN=CC=C2",url: null, apids:[411,425,430,431,432,445,588358],
                 sids:[[1127384,3461229,5526257,7972531,9181059,16623828,44484393]], probeId:null,cid: 881181}
                 }
            }'''



    final static jsonXCompound2 = '''
            {"_index":"compound","_type":"xcompound","_id":"cJrTWNb_TZupO_xDgE2NTw", "_source" :
                {smiles: "C",url: null, apids:[411,412,413],
                 sids:[[1127384]], probeId:null,cid: 881181}
                 }
            }'''

    final static jsonXCompound3 = '''
            {"_index":"compound","_type":"xcompound","_id":"cJrTWNb_TZupO_xDgE2NTw", "_source" :
                {smiles: "C",url: null, apids:[412,413,414],
                 sids:[[1127384]], probeId:null,cid: 881181}
                 }
            }'''


    void setup() {
        queryExecutorService = Mock(QueryExecutorService)
        service.queryExecutorService = queryExecutorService
        service.elasticSearchBaseUrl = 'http://mockMe'
        service.assayIndexName = 'assays'
        service.assayIndexTypeName = 'assay'
        service.compoundIndexName = 'compounds'
        service.compoundIndexTypeName = 'compound'
    }

    void tearDown() {
        // Tear down logic here
    }



    void "try xcompound constructor"(){
        when:
        def jSONObject = new JSONObject(jsonXCompound)
        assert  jSONObject

        then:
        ESXCompound eSXCompound = new ESXCompound(jSONObject)
        assert   eSXCompound
        assert eSXCompound.smiles == "CC(=O)CCC1=CC=C(C=C1)OC(=O)C2=CN=CC=C2"
        assert eSXCompound.cid == "881181"
        assert eSXCompound.probeId == "null"
        assert eSXCompound.url == "null"
        assert eSXCompound.apids.size() > 1
        assert eSXCompound.sids.size() > 1

//        where:    // still to be parameterized
//        eSXCompound.smiles == "CC(=O)CCC1=CC=C(C=C1)OC(=O)C2=CN=CC=C2"

    }

    void "try merging lists"(){
        when:
        def jSONObject1 = new JSONObject(jsonXCompound2)
        def jSONObject2 = new JSONObject(jsonXCompound3)
        def listOfESXCompound = new ArrayList()
        listOfESXCompound  <<  jSONObject1 <<   jSONObject2

        then:
        List<Integer> aids = ESXCompound.combinedApids(listOfESXCompound)
        assert aids.size()==4
        aids.containsAll([411,412,413,414])
        List<Integer> nullTest = ESXCompound.mergeLists( new ArrayList< List< Integer>> ())
        assert nullTest.size()==0

    }


    void "test search #label"() {

        when:
        Map<String, List> response = service.search(searchTerm, 999999)
        println()

        then:
        queryExecutorService.postRequest(_, _) >> { assayJson }

        assert response.assays.size() == expectedAssayCount
        assert response.compounds.size() == expectedCompoundCount

        where:
        label     | searchTerm | assayJson                               | expectedAssayCount | expectedCompoundCount
        "for AID" | "644"      | new JSONObject(jsonAssayResponseString) | 1                  | 2
        "for AID" | "644"      | new JSONObject()                        | 0                  | 0
    }

    void "test getAssayDocument #label"() {

        when:
        JSONObject response = service.getAssayDocument(assayId)
        println()

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.keySet().size() == expectedKeySetSize

        where:
        label     | assayId        | assayJson                          | expectedKeySetSize
        "for AID" | 644 as Integer | new JSONObject('{"key": "value"}') | 1
        "for AID" | 644 as Integer | new JSONObject()                   | 0
    }

    void "test getAssayDocumentWithCID #label"() {

        when:
        JSONObject response = service.getAssayDocument(assayId)
        println()

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.keySet().size() == expectedKeySetSize

        where:
        label     | assayId        | assayJson                          | expectedKeySetSize
        "for AID" | 174 as Integer | new JSONObject('{"key": "value"}') | 1
        "for AID" | 174 as Integer | new JSONObject()                   | 0
    }

    void "test query String Query #label"() {
        when:
        final JSONObject response = service.searchQueryStringQuery(url, queryStringDSL)

        then:
        queryExecutorService.postRequest(_, _) >> { queryStringDSL }
        assert response.keySet().size() == expectedKeySetSize

        where:
        label                  | url                     | queryStringDSL           | expectedKeySetSize
        "Non Empty JSONObject" | "http://localhost:9200" | new JSONObject([a: "b"]) | 1
        "Empty JSONObject"     | "http://localhost:9200" | new JSONObject()         | 0
    }
}
