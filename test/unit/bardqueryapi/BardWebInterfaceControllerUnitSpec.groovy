package bardqueryapi

import bard.core.Compound
import bard.core.DataSource
import bard.core.LongValue
import bard.core.adapter.CompoundAdapter
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONArray

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
        queryService.showCompound(_) >> { compoundAdapter }

        "/bardWebInterface/showCompound" == view
        assert model.compound
        expectedCID == model.compound.pubChemCID
        expectedSIDs == model.compound.pubChemSIDs


        where:
        label               | cid              | compoundAdapter                            | expectedCID | expectedSIDs
        "Return a compound" | new Integer(872) | buildCompoundAdapter(872, [1, 2, 3], "CC") | 872         | [1, 2, 3]
    }

//    void "test search #label"() {
//        when:
//        request.method = 'GET'
//        controller.params.searchString = searchTerm
//        controller.search()
//
//        then:
//        queryService.search(searchTerm) >> { resultJson }
//
//        assert "/bardWebInterface/homePage" == view
//        assert model.totalCompounds == expectedTotalCompounds
//        assert model.assays.size == expectedAssays
//        assert model.compounds.size == expectedCompounds
//        assert model.experiments == []
//        assert model.projects == []
//
//        where:
//        label                                | searchTerm | resultJson                                                                                                          | expectedTotalCompounds | expectedAssays | expectedCompounds
//        "nothing was found"                  | '644'      | [totalCompounds: 0, assays: [], compounds: [], experiments: [], projects: []]                                       | 0                      | 0              | 0
//        "An Assay and a compound were found" | '644'      | [totalCompounds: 1, assays: [esAssay], compounds: ['CC'], xcompounds: [esxCompound], experiments: [], projects: []] | 1                      | 1              | 1
//    }


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

    CompoundAdapter buildCompoundAdapter(Long cid, List<Long> sids, String smiles) {
        Compound compound = new Compound()
        DataSource source = new DataSource("stuff", "v1")
        compound.setId(cid);
        for (Long sid : sids) {
            compound.add(new LongValue(source, Compound.PubChemSIDValue, sid));
        }
        // redundant
        compound.add(new LongValue(source, Compound.PubChemCIDValue, cid));
        // MolecularData md = Mock()
        //compound.add(new MolecularValue(source, Compound.MolecularValue, md));
        return new CompoundAdapter(compound)
    }
}
