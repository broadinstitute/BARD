package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.util.StructureSearchParams
import grails.plugin.spock.IntegrationSpec
import molspreadsheet.SpreadSheetActivity
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class QueryServiceIntegrationSpec extends IntegrationSpec {

    QueryService queryService

    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }

    void "test findExperimentDataById #label"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map experimentDataMap = queryService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        assert totalActivities
        final List<Activity> activities = experimentDataMap.activities
        assert activities
        assert activities.size() == 10
        for (Activity activity : activities) {
            assert activity
            assert activity.cid
            assert activity.eid
            assert activity.sid
            assert activity.resultData
        }
        where:
        label                                              | experimentId   | top | skip
        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
    }

    void "test convertSpreadSheetActivityToCompoundInformation"() {

        when: "We call the findExperimentDataById method with the experimentId #experimentId"
        final Map experimentDataMap = queryService.findExperimentDataById(experimentId, top, skip)

        then: "We get back the expected map"
        assert experimentDataMap
        final Long totalActivities = experimentDataMap.total
        assert totalActivities
        final List<Activity> activities = experimentDataMap.activities
        assert activities

        where:
        label                                              | experimentId   | top | skip
        "An existing experiment with activities - skip 0"  | new Long(1326) | 10  | 0
        "An existing experiment with activities - skip 10" | new Long(1326) | 10  | 10
    }

    void "test findPromiscuityScoreForCID #label"() {
        when:
        final Map resultMap = queryService.findPromiscuityScoreForCID(cid)
        then:
        assert resultMap.promiscuityScore
        assert resultMap.promiscuityScore.scaffolds
        assert resultMap.status == promiscuityScoreMap.status
        assert resultMap.message == promiscuityScoreMap.message
        where:
        label       | cid   | promiscuityScoreMap
        "CID 38911" | 38911 | [status: 200, message: "Success"]
        "CID 2722"  | 2722  | [status: 200, message: "Success"]
    }

    void "test autoComplete #label"() {

        when:
        final List<String> response = queryService.autoComplete(term)

        then:
        assert response
        assert response.size() >= expectedResponseSize

        where:
        label                       | term  | expectedResponseSize
        "Partial match of a String" | "Dna" | 1
    }

    /**
     */
    void "test Show Compound #label"() {
        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = queryService.showCompound(cid)
        then: "The Compound is found"
        assert compoundAdapter
        assert compoundAdapter.compound
        assert cid == compoundAdapter.pubChemCID
        assert expectedSmiles == compoundAdapter.structureSMILES
        where:
        label                       | cid    | expectedSmiles
        "Return a Compound Adapter" | 658342 | "C(CN1CCCCC1)N2C(N=CC3=CC=CS3)=NC4=CC=CC=C24"
    }



    void "test Show Project"() {
        given:
        final Integer projectId = 129
        when: "Client enters a project ID and the showProject method is called"
        Map projectAdapterMap = queryService.showProject(projectId)
        then: "The ProjectSearchResult is found"
        assert projectAdapterMap
        ProjectAdapter projectAdapter = projectAdapterMap.projectAdapter
        assert projectAdapter
        assert projectAdapter.project
        assert projectId == projectAdapter.id
        assert projectAdapter.name
        assert projectAdapter.description
    }


    void "test Show Assay"() {
        given:

        Integer assayId = 644
        when: "Client enters a assay ID and the showAssay method is called"
        Map assayMap = queryService.showAssay(assayId)

        then: "The Assay document is found"
        assert assayMap
        AssayAdapter assayAdapter = assayMap.assayAdapter
        assert assayAdapter
        assert assayId == assayAdapter.id
        assert assayAdapter.protocol
        assert assayAdapter.comments
        assert assayAdapter.type == AssayType.Other
        assert assayAdapter.role == AssayRole.Primary
        assert assayAdapter.category == AssayCategory.MLPCN
        assert assayAdapter.description
    }
    /**
     * Do structure searches
     */
    void "test Structure Search with CIDS #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.structureSearch(cid, structureSearchParamsType, [], top, skip)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        assert compoundAdapters
        assert compoundAdapterMap.nHits > 0

        where:
        label                    | structureSearchParamsType                 | cid    | skip | top | numberOfCompounds
        "Super structure search" | StructureSearchParams.Type.Superstructure | 5358   | 0    | 10  | 3
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | 123606 | 0    | 10  | 1
        "Substructure"           | StructureSearchParams.Type.Substructure   | 237    | 0    | 10  | 10
        "salicylic acid exact"   | StructureSearchParams.Type.Exact          | 338    | 0    | 10  | 1
    }
    /**
     * Do structure searches
     */
    void "test Structure Search #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.structureSearch(smiles, structureSearchParamsType, [], top, skip)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        assert compoundAdapters
        assert numberOfCompounds == compoundAdapters.size()
        and:
        assert compoundAdapterMap.nHits > 0

        where:
        label                       | structureSearchParamsType                 | smiles                                        | skip | top | numberOfCompounds
        "Super structure search"    | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 0    | 10  | 3
        "Similarity Search"         | StructureSearchParams.Type.Similarity     | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Exact match Search"        | StructureSearchParams.Type.Exact          | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Sub structure Search"      | StructureSearchParams.Type.Substructure   | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Default (to Substructure)" | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 0    | 10  | 10
        "Skip 10, top 10"           | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 10   | 10  | 10
        "salicylic acid substruct"  | StructureSearchParams.Type.Substructure   | "OC(=O)C1=C(O)C=CC=C1"                        | 0    | 10  | 10
        "salicylic acid exact"      | StructureSearchParams.Type.Exact          | "OC(=O)C1=C(O)C=CC=C1"                        | 0    | 10  | 1
    }
    /**
     * Do structure searches
     */
    void "test SubStructure Search #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.structureSearch(smiles, structureSearchParamsType, [], top, skip)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        assert numberOfCompounds == compoundAdapters.size()




        where:
        label                            | structureSearchParamsType               | smiles                    | skip | top | numberOfCompounds
        "square planar"                  | StructureSearchParams.Type.Substructure | "F[Po@SP1](Cl)(Br)I"      | 0    | 2   | 0
        "mixture"                        | StructureSearchParams.Type.Substructure | "c1ccnc1.C1CCCCC1"        | 0    | 2   | 2
        "explicit hydrogens"             | StructureSearchParams.Type.Substructure | "CC[H]"                   | 0    | 2   | 2
        "aromatic"                       | StructureSearchParams.Type.Substructure | "c1ccccc1"                | 0    | 2   | 2
        "triple bond"                    | StructureSearchParams.Type.Substructure | "CC#CCl"                  | 0    | 2   | 1
        "double bond stereo 1"           | StructureSearchParams.Type.Substructure | "C\\C=C\\C"               | 0    | 2   | 2
        "double bond stereo 2"           | StructureSearchParams.Type.Substructure | "C\\C=C/C"                | 0    | 2   | 2
        "trigonal bipyramid 1"           | StructureSearchParams.Type.Substructure | "O=C[As@](F)(Cl)(Br)S"    | 0    | 2   | 0
        "trigonal bipyramid 2"           | StructureSearchParams.Type.Substructure | "s[As@@](F)(Cl)(Br)C=O"   | 0    | 2   | 0
        "octahedral 1"                   | StructureSearchParams.Type.Substructure | "n1cccc2ccccc12"          | 0    | 2   | 2
        "octahedral 2"                   | StructureSearchParams.Type.Substructure | "OC(=O)C1=C(O)C=CC=C1"    | 0    | 2   | 2
        "tetrahedral stereo with H 1"    | StructureSearchParams.Type.Substructure | "C[C@H](O)F"              | 0    | 2   | 0
        "tetrahedral stereo with H 2"    | StructureSearchParams.Type.Substructure | "C[C@@H](O)F"             | 0    | 2   | 0
        "allene stereo 1"                | StructureSearchParams.Type.Substructure | "OC=[C@]=CF"              | 0    | 2   | 0
        "allene stereo 2"                | StructureSearchParams.Type.Substructure | "OC([H])=[C@AL1]=C([H])F" | 0    | 2   | 0
        "tetrahedral stereo without H 1" | StructureSearchParams.Type.Substructure | "C[C@@](N)(O)F"           | 0    | 2   | 0
        "tetrahedral stereo without H 1" | StructureSearchParams.Type.Substructure | "C[C@](N)(O)F"            | 0    | 2   | 0
        "ions 2"                         | StructureSearchParams.Type.Substructure | "[Cl-][Ca++][Cl-]"        | 0    | 2   | 0
        "with H"                         | StructureSearchParams.Type.Substructure | "C[C@H](N)C=C"            | 0    | 2   | 2
        "without H"                      | StructureSearchParams.Type.Substructure | "CC[C@@](C)(N)C=C"        | 0    | 2   | 2


    }

    void "test find Compounds By Text Search String #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.findCompoundsByTextSearch(searchString, top, skip, filters)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        assert compoundAdapters

        assert compoundAdapterMap.nHits >= numberOfCompounds
        where:
        label                     | searchString         | skip | top | numberOfCompounds | filters
        "dna repair"              | "dna repair"         | 0    | 10  | 10                | []
        "dna repair with filters" | "dna repair"         | 0    | 10  | 1                 | [new SearchFilter("tpsa", "55.1")]
        "dna repair skip and top" | "dna repair"         | 10   | 10  | 10                | []
        "biological process"      | "biological process" | 0    | 10  | 4                 | []

    }

    void "test find Compounds By CIDs #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.findCompoundsByCIDs(cids)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        assert compoundAdapters != null
        assert compoundAdapterMap
        assert cids.size() == compoundAdapters.size()

        where:
        label                        | cids
        "Single CID"                 | [3235555]
        "Search with a list of CIDs" | [3235555, 3235556, 3235557, 3235558, 3235559, 3235560, 3235561, 3235562, 3235563, 3235564]
    }

    void "test find Assays By Text Search String #label"() {
        when: ""
        final Map assayAdapterMap = queryService.findAssaysByTextSearch(searchString, top, skip, filters)
        then:
        List<AssayAdapter> assayAdapters = assayAdapterMap.assayAdapters
        assert !assayAdapters.isEmpty()
        assert assayAdapters.size() >= 0
        assert assayAdapterMap.facets
        assert assayAdapterMap.nHits >= 0

        where:
        label                     | searchString             | skip | top | numberOfAssays | filters
        "dna repair"              | "\"dna repair\""         | 0    | 10  | 10             | []
        "dna repair with filters" | "\"dna repair\""         | 0    | 10  | 10             | [new SearchFilter("gobp_term", "DNA repair"), new SearchFilter("gobp_term", "response to UV-C")]
        "dna repair skip and top" | "\"dna repair\""         | 10   | 10  | 10             | []
        "biological process"      | "\"biological process\"" | 0    | 10  | 4              | []

    }


    void "test find Assays By APIDs #label"() {
        when: ""
        final Map assayAdapterMap = queryService.findAssaysByADIDs(apids)

        and:
        List<AssayAdapter> assayAdapters = assayAdapterMap.assayAdapters
        then:
        assert !assayAdapters.isEmpty()
        assert assayAdapterMap.facets.isEmpty()
        assert assayAdapters.size() == apids.size()
        where:
        label                         | apids
        "Single APID"                 | [644]
        "Search with a list of APIDs" | [644, 600, 666]
    }

    void "test find Projects By Text Search #label"() {
        when: ""
        Map projectAdapterMap = queryService.findProjectsByTextSearch(searchString, top, skip, filters)
        and:
        List<ProjectAdapter> projectAdapters = projectAdapterMap.projectAdapters
        then:
        assert !projectAdapters.isEmpty()
        assert projectAdapterMap.facets
        assert projectAdapterMap.nHits >= numberOfProjects
        where:
        label                             | searchString         | skip | top | numberOfProjects | filters
        "dna repair"                      | "\"dna repair\""     | 0    | 10  | 10               | []
        "dna repair skip and top"         | "\"dna repair\""     | 10   | 10  | 10               | []
        "biological process"              | "biological process" | 0    | 10  | 10               | []
        "biological process with filters" | "biological process" | 0    | 10  | 4                | [new SearchFilter("num_expt", "6")]

    }

    void "test find Projects By PIDs #label"() {
        when: ""
        final Map projectAdapterMap = queryService.findProjectsByPIDs(pids)

        and:
        final List<ProjectAdapter> projectAdapters = projectAdapterMap.projectAdapters
        then:
        assert projectAdapters
        assert projectAdapterMap.facets.isEmpty()
        assert projectAdapters.size() == pids.size()
        where:
        label                               | pids
        "Single PID"                        | [129]
        "Search with a list of project ids" | [129, 102, 100]
    }
}
