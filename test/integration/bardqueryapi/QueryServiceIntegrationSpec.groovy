package bardqueryapi

import bard.core.AssayValues
import bard.core.StructureSearchParams
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll
import bard.core.EntityNamedSources
import bard.core.DataSource
import bard.core.Assay

@Unroll
class QueryServiceIntegrationSpec extends IntegrationSpec {

    QueryService queryService

    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }

    void "test autoComplete #label"() {

        when:
        final List<String> response = queryService.autoComplete(term)

        then:
        assert response
        assert response.size() == expectedResponseSize

        where:
        label                       | term  | expectedResponseSize
        "Partial match of a String" | "Dna" | 10
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
        Long[] sids = compoundAdapter.pubChemSIDs
        assert expectedSIDs.size() == sids.length
        assert expectedSIDs == sids
        where:
        label                       | cid                 | expectedSIDs                                                                                        | expectedSmiles
        "Return a Compound Adapter" | new Integer(658342) | [5274057, 47984903, 51638425, 113532087, 124777946, 970329, 6320599, 35591597, 76362856, 112834159] | "C(CN1CCCCC1)N1C(N=CC2=CC=CS2)=NC2=CC=CC=C12"
    }



    void "test Show Project"() {
        given:
        final Integer projectId = new Integer(1772)
        when: "Client enters a project ID and the showProject method is called"
        ProjectAdapter projectAdapter = queryService.showProject(projectId)
        then: "The Project is found"
        assert projectAdapter
        assert projectAdapter.project
        assert projectId == projectAdapter.project.id
        assert projectAdapter.name
        assert projectAdapter.project.description
    }

    void "test Show Assay"() {
        given:
        Integer assayId = new Integer(644)
        when: "Client enters a assay ID and the showAssay method is called"
        AssayAdapter assayAdapter = queryService.showAssay(assayId)
        then: "The Assay document is found"
        assert assayAdapter
        assert assayId == assayAdapter.assay.id
        assert assayAdapter.assay.protocol
        assert assayAdapter.assay.comments
        assert assayAdapter.assay.type == AssayValues.AssayType.Confirmatory
        assert assayAdapter.assay.role == AssayValues.AssayRole.Primary
        assert assayAdapter.assay.category == AssayValues.AssayCategory.MLSCN
        assert assayAdapter.assay.description
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
//        and:
//        assert compoundAdapterMap.facets
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
        "biological process"      | "biological process" | 0    | 10  | 10                | []

    }

    void "test find Compounds By CIDs #label"() {
        when: ""
        final Map compoundAdapterMap = queryService.findCompoundsByCIDs(cids)
        then:
        assert compoundAdapterMap
        final List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
        Collection<Value> facets = compoundAdapterMap.facets
        assert compoundAdapters != null
        assert compoundAdapterMap
        assert cids.size() == compoundAdapters.size()
        //      assert facets != null
//        assert !facets.isEmpty()
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
        label                     | searchString         | skip | top | numberOfAssays | filters
        "dna repair"              | "dna repair"         | 0    | 10  | 10             | []
        "dna repair with filters" | "dna repair"         | 0    | 10  | 10             | [new SearchFilter("gobp_term", "DNA repair"), new SearchFilter("gobp_term", "response to UV-C")]
        "dna repair skip and top" | "dna repair"         | 10   | 10  | 10             | []
        "biological process"      | "biological process" | 0    | 10  | 10             | []

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
        assert projectAdapters.size() == numberOfProjects
        assert projectAdapterMap.facets
        assert projectAdapterMap.nHits >= numberOfProjects
        where:
        label                             | searchString         | skip | top | numberOfProjects | filters
        "dna repair"                      | "dna repair"         | 0    | 10  | 10               | []
        "dna repair with filters"         | "dna repair"         | 0    | 10  | 8                | [new SearchFilter("num_expt", "6")]
        "dna repair skip and top"         | "dna repair"         | 10   | 10  | 10               | []
        "biological process"              | "biological process" | 0    | 10  | 10               | []
        "biological process with filters" | "biological process" | 0    | 10  | 10               | [new SearchFilter("num_expt", "6")]

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
        "Single PID"                        | [1772]
        "Search with a list of project ids" | [1772, 805, 1074]
    }
}
