package bard.core.rest.spring.experiment

import bard.core.interfaces.ExperimentRole
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentSearchUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String EXPERIMENT_SEARCH_JSON = '''
    {
       "bardExptId": 1472,
       "bardAssayId": 1472,
       "capAssayId": 1472,
       "capExptId": 1472,
       "pubchemAid": 1460,
       "category": 2,
       "type": 2,
       "summary": 0,
       "assays": 0,
       "classification": 0,
       "substances": 271676,
       "compounds": 267415,
       "name": "qHTS for Inhibitors of Tau Fibril Formation, Thioflavin T Binding",
       "description": "University of New Mexico Assay Overview",
       "source": "NCGC",
       "grantNo": "TR001",
       "deposited": "deposited",
       "updated": null,
       "hasProbe": false,
       "projectIdList":
       [
           17
       ],
       "resourcePath": "/experiments/1472"
   }
'''




    void "test serialization to ExperimentSearch"() {
        when:
        final ExperimentSearch experimentSearch = objectMapper.readValue(EXPERIMENT_SEARCH_JSON, ExperimentSearch.class)
        then:
        assert experimentSearch
        assert experimentSearch.bardExptId == 1472
        assert experimentSearch.bardAssayId == 1472
        assert experimentSearch.capAssayId== 1472
        assert experimentSearch.capExptId == 1472
        assert experimentSearch.resourcePath == "/experiments/1472"
        assert experimentSearch.projectIdList.size() == 1
        assert !experimentSearch.hasProbe
        assert !experimentSearch.updated
        assert experimentSearch.deposited == "deposited"
        assert experimentSearch.grantNo == "TR001"
        assert experimentSearch.source == "NCGC"
        assert experimentSearch.description == "University of New Mexico Assay Overview"
        assert experimentSearch.name == "qHTS for Inhibitors of Tau Fibril Formation, Thioflavin T Binding"
        assert experimentSearch.compounds == 267415
        assert experimentSearch.substances == 271676
        assert experimentSearch.pubchemAid == 1460
        assert experimentSearch.category == 2
        assert experimentSearch.type == 2
        assert experimentSearch.summary == 0
        assert experimentSearch.assays == 0
        assert experimentSearch.bardAssayId == 1472
        assert experimentSearch.classification == 0
        assert experimentSearch.getBardExptId() == 1472
        assert experimentSearch.getRole() == ExperimentRole.Primary
    }

    void "test groupByProjectIds"() {
        when:
        final ExperimentSearch experimentSearch = objectMapper.readValue(EXPERIMENT_SEARCH_JSON, ExperimentSearch.class)
        final ExperimentSearch experimentSearch2 = objectMapper.readValue(EXPERIMENT_SEARCH_JSON, ExperimentSearch.class)

        def grouping = ExperimentAbstract.groupByProjectIds([experimentSearch, experimentSearch2])

        then:
        assert grouping.keySet().size() == 1
        assert grouping[17L]
        assert grouping[17L].size() == 2
        assert grouping[17L].get(0) == experimentSearch
        assert grouping[17L].get(1) == experimentSearch2

    }
}

