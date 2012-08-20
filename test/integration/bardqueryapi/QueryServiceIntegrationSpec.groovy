package bardqueryapi

import bard.core.adapter.CompoundAdapter
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.*

@Unroll
class QueryServiceIntegrationSpec extends IntegrationSpec {

    QueryService queryService

    void setup() {
    }


    void "test autoComplete #label"() {

        when:
        final List<String> response = queryService.autoComplete(term)

        then:
        assert response
        assert response.size() == expectedResponseSize

        where:
        label                       | term  | expectedResponseSize
        "Partial match of a String" | "Bro" | 10
    }

    /**
     */
    void "test handleAutoComplete #label"() {

        when:
        final List<String> response = queryService.handleAutoComplete(term)

        then:
        assert response
        assert response.size() == expectedResponseSize

        where:
        label                       | term  | expectedResponseSize
        "Partial match of a String" | "Bro" | 10
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

    void "test Show Experiment"() {
        when: "Client enters an experiment ID and the showExperiment method is called"
        Experiment experiment = queryService.showExperiment(experimentId)
        then: "The Experiment is found"
        assert experiment
        assert experimentId == experiment.id
        assert experimentName == experiment.name

        where:
        label                  | experimentId     | experimentName
        "Return an Experiment" | new Integer(346) | "HIV Nucleocapsid"

    }

    void "test Show Project"() {
        given:
        final Integer projectId = new Integer(1772)
        when: "Client enters a project ID and the showProject method is called"
        Project project = queryService.showProject(projectId)
        then: "The Project is found"
        assert project
        assert projectId == project.id
        assert project.name
        assert project.type == AssayValues.AssayType.Other
        assert project.role == AssayValues.AssayRole.Primary
        assert project.category == AssayValues.AssayCategory.Unknown
        assert project.description
    }

    void "test Show Assay"() {
        given:
        Integer assayId = new Integer(644)
        when: "Client enters a assay ID and the showAssay method is called"
        Assay assay = queryService.showAssay(assayId)
        then: "The Assay document is found"
        assert assay
        assert assayId == assay.id
        assert assay.protocol
        assert assay.comments
        assert assay.type == AssayValues.AssayType.Confirmatory
        assert assay.role == AssayValues.AssayRole.Primary
        assert assay.category == AssayValues.AssayCategory.MLSCN
        assert assay.description
    }
    /**
     */
    void "test Structure Search #label"() {
        when:
        final List<CompoundAdapter> compoundAdapters = queryService.structureSearch(smiles, structureSearchParamsType,top,skip)
        then:
        assert compoundAdapters
        assert numberOfCompounds == compoundAdapters.size()
        where:
        label                       | structureSearchParamsType                 | smiles                                        | skip | top | numberOfCompounds
        "Super structure search"    | StructureSearchParams.Type.Superstructure | "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O"             | 0    | 10  | 3
        "Similarity Search"         | StructureSearchParams.Type.Similarity     | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Exact match Search"        | StructureSearchParams.Type.Exact          | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Sub structure Search"      | StructureSearchParams.Type.Substructure   | "CN(C)CCC1=CNC2=C1C=C(CS(=O)(=O)N3CCCC3)C=C2" | 0    | 10  | 1
        "Default (to Substructure)" | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 0    | 10  | 10
        "Skip 10, top 10"           | StructureSearchParams.Type.Substructure   | "n1cccc2ccccc12"                              | 10   | 10  | 10

    }

}
