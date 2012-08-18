package bardqueryapi


import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONArray
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
class BardWebInterfaceControllerUnitSpec extends Specification {

    QueryService queryService

    final private static String compoundDocumentJson = '''{"probeId":"null","sids":[4243156,24368917],"smiles":"C-C","cid":"3237916"}'''


    final private static ESAssay esAssay = new ESAssay(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            assayNumber: 'assayNumber',
            assayName: 'assayName'
    )



    final private static ESXCompound esxCompound = new ESXCompound(
            _index: 'index',
            _type: 'type',
            _id: 'id',
            cid: '1234567890'
    )


    void setup() {
        queryService = Mock(QueryService)
        controller.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        queryService.showCompound(_) >> { compoundJson }

        "/bardWebInterface/showCompound" == view
        872 == model.compoundId
        def responseCompoundJSON = model.compoundJson
        assert responseCompoundJSON == expectedCompoundJson


        where:
        label                                | cid              | compoundJson                         | expectedCompoundJson
        "compound not found - error message" | new Integer(872) | new JSONObject()                     | [:]
        "Return a compound"                  | new Integer(872) | new JSONObject(compoundDocumentJson) | [probeId: "null", sids: [4243156, 24368917], smiles: "C-C", cid: "3237916"]
    }

    void "test search #label"() {
        when:
        request.method = 'GET'
        controller.params.searchString = searchTerm
        controller.search()

        then:
        queryService.search(searchTerm) >> { resultJson }

        assert "/bardWebInterface/homePage" == view
        assert model.totalCompounds == expectedTotalCompounds
        assert model.assays.size == expectedAssays
        assert model.compounds.size == expectedCompounds
        assert model.experiments == []
        assert model.projects == []

        where:
        label                                | searchTerm | resultJson                                                                                                          | expectedTotalCompounds | expectedAssays | expectedCompounds
        "nothing was found"                  | '644'      | [totalCompounds: 0, assays: [], compounds: [], experiments: [], projects: []]                                       | 0                      | 0              | 0
        "An Assay and a compound were found" | '644'      | [totalCompounds: 1, assays: [esAssay], compounds: ['CC'], xcompounds: [esxCompound], experiments: [], projects: []] | 1                      | 1              | 1
    }


    void "test autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        controller.autoCompleteAssayNames()
        then:
        queryService.autoComplete(_) >> { expectedList }
        controller.response.json.toString() == new JSONArray(expectedList.toString()).toString()

        where:
        label                | searchString | expectedList
        "Return two strings" | "Bro"        | ["Broad Institute MLPCN Platelet Activation"]

    }
}
