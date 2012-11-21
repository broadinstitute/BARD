package bard.core.rest

import bard.core.interfaces.EntityService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import jdo.JSONNodeTestHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

@Unroll
class RESTExperimentServiceUnitSpec extends Specification {
    RESTExperimentService restExperimentService
    RESTAssayService restAssayService
    @Shared DataSource dataSource = new DataSource("name")
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String EXPERIMENT_READOUTS = JSONNodeTestHelper.EXPERIMENT_READOUTS
    @Shared String EXPERIMENT_SINGLE_READOUT = JSONNodeTestHelper.EXPERIMENT_SINGLE_READOUT
    @Shared String EXPERIMENT_SEARCH_RESULTS = JSONNodeTestHelper.EXPERIMENT_SEARCH_RESULTS
    @Shared String TARGETS_NODE = JSONNodeTestHelper.TARGETS_NODE
    @Shared String ASSAY_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.ASSAY_EXPANDED_SEARCH_RESULTS
    @Shared String NO_COMPOUNDS_NODE = JSONNodeTestHelper.COMPOUND_SEARCH_RESULTS
    @Shared String EXPERIMENT_RESULTS = '''
{
   "collection":
   [
       {
           "exptDataId": "346.17432633",
           "eid": 2209,
           "cid": 2836861,
           "sid": 17432633,
           "bardExptId": 346,
           "runset": "default",
           "outcome": 2
        }
      ]
   }
   '''

    void setup() {
        this.restExperimentService = new RESTExperimentService("base")
        this.restAssayService = new RESTAssayService("base")
        this.restExperimentService.restAssayService = this.restAssayService
    }

    void tearDown() {
        // Tear down logic here
    }



    void "getEntitySearch #label"() {
        when:
        final Experiment resultExperiment = this.restExperimentService.getEntitySearch(experiment, node)

        then:
        assert resultExperiment
        where:
        label                    | node | experiment
        "Experiment is not null" | null | new Experiment()
        "Experiment is null"     | null | null
    }

    void "addCompound #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addCompound(experiment, node, dataSource)

        then:
        assert experiment.getValues().isEmpty() == isNodeEmpty
        assert experiment.getValues(dataSource).isEmpty() == isNodeEmpty

        where:
        label                   | node                                  | isNodeEmpty
        "With 'compounds' node" | mapper.readTree("{\"compounds\": 3}") | false
        "No 'compounds' node"   | mapper.readTree(NO_COMPOUNDS_NODE)    | true
    }

    void "addSubstances #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addSubstance(experiment, node, dataSource)

        then:
        assert experiment.getValues().isEmpty() == isNodeEmpty
        assert experiment.getValues(dataSource).isEmpty() == isNodeEmpty

        where:
        label                    | node                                   | isNodeEmpty
        "With 'substances' node" | mapper.readTree("{\"substances\": 3}") | false
        "No 'substances' node"   | mapper.readTree(NO_COMPOUNDS_NODE)     | true
    }

    void "addSingleAssayNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addSingleAssayNode(experiment, node, restAssayService)

        then:
        assert (experiment.getAssay() == null) == isNull
        assert (experiment.getAssay()?.getId() != 600) == isNull

        where:
        label               | node                                           | isNull
        "With 'assay' node" | mapper.readTree(ASSAY_EXPANDED_SEARCH_RESULTS) | false
        "No 'assay' node"   | null                                           | true

    }

    void "addAssayNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addAssayNode(experiment, node)

        then:
        assert (experiment.getAssay() == null) == isNull
        assert (experiment.getAssay()?.getId() != 346) == isNull

        where:
        label                  | node                                       | isNull
        "With 'assay' node"    | mapper.readTree(EXPERIMENT_SEARCH_RESULTS) | false
        "Without 'assay' node" | mapper.readTree(TARGETS_NODE)              | true

    }


    void "getEntity #label"() {
        when:
        final Experiment resultExperiment = this.restExperimentService.getEntity(experiment, node)

        then:
        assert resultExperiment
        assert resultExperiment.getId() == 346
        where:
        label                    | node                                       | experiment
        "Experiment is not null" | mapper.readTree(EXPERIMENT_SEARCH_RESULTS) | new Experiment()
        "Experiment is null"     | mapper.readTree(EXPERIMENT_SEARCH_RESULTS) | null
    }

    void "addPubChemAIDNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addPubChemAIDNode(experiment, node)
        then:
        assert experiment.getPubchemAid() == expectedResult
        where:
        label                              | node                                     | expectedResult
        "'pubchemAid' key in JSON Node"    | mapper.readTree("{ \"pubchemAid\": 17}") | 17
        "No 'pubchemAid' key in JSON Node" | mapper.readTree("{ \"someName\": 17}")   | null

    }

    void "addRoleNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addRoleNode(experiment, node)
        then:
        assert (experiment.getRole() == null) == expectedResult
        where:
        label                                  | node                                        | expectedResult
        "'classification' key in JSON Node"    | mapper.readTree("{ \"classification\": 0}") | false
        "No 'classification' key in JSON Node" | mapper.readTree("{ \"someName\": 17}")      | true
    }

    void "addTypeNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addTypeNode(experiment, node)
        then:
        assert (experiment.getType() == null) == expectedResult
        where:
        label                        | node                                   | expectedResult
        "'type' key in JSON Node"    | mapper.readTree("{ \"type\": 0}")      | false
        "No 'type' key in JSON Node" | mapper.readTree("{ \"someName\": 17}") | true
    }

    void "addCategoryNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addCategoryNode(experiment, node)
        then:
        assert (experiment.getCategory() == null) == expectedResult
        where:
        label                            | node                                   | expectedResult
        "'category' key in JSON Node"    | mapper.readTree("{ \"category\": 0}")  | false
        "No 'category' key in JSON Node" | mapper.readTree("{ \"someName\": 17}") | true
    }

    void "addDescriptionNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addDescriptionNode(experiment, node)
        then:
        assert (experiment.getDescription() == null) == expectedResult
        where:
        label                               | node                                                   | expectedResult
        "'description' key in JSON Node"    | mapper.readTree("{ \"description\": \"description\"}") | false
        "No 'description' key in JSON Node" | mapper.readTree("{ \"someName\": 17}")                 | true
    }

    void "addNameNode with Exception #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addNameNode(experiment, node)
        then:
        thrown(IllegalArgumentException)
        where:
        label                        | node
        "No 'name' key in JSON Node" | mapper.readTree("{ \"NoprojId\": 17}")
    }

    void "addIdNode with Exception #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addIdNode(experiment, node)
        then:
        thrown(IllegalArgumentException)
        where:
        label                          | node
        "No 'exptId' key in JSON Node" | mapper.readTree("{ \"NoprojId\": 17}")
    }

    void "addNameNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addNameNode(experiment, node)
        then:
        assert experiment.getName() == "name"
        where:
        label                     | node
        "'name' key in JSON Node" | mapper.readTree("{ \"name\": \"name\"}")
    }

    void "addIdNode #label"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.restExperimentService.addIdNode(experiment, node)
        then:
        assert experiment.getId() == 17
        where:
        label                       | node
        "'exptId' key in JSON Node" | mapper.readTree("{ \"exptId\": 17}")
    }

    void "createValue with exception #label"() {
        when:
        this.restExperimentService.createValue(dataSource, node)
        then:
        thrown(IllegalArgumentException)
        where:
        label                              | node
        "'No exptDataId' key in JSON Node" | mapper.readTree("{ \"noexptDataId\": 17}")

    }

    void "createValueFromID #label"() {
        when:
        Value value = this.restExperimentService.createValueFromID(dataSource, node)
        then:
        assert (value == null) == expectedResults
        where:
        label                              | node                                       | expectedResults
        "'No exptDataId' key in JSON Node" | mapper.readTree("{ \"noexptDataId\": 17}") | true
        "'exptDataId' key in JSON Node"    | mapper.readTree("{ \"exptDataId\": 17}")   | false

    }

    void "addEID #label"() {
        given:
        Value value = new Value(dataSource)
        when:
        this.restExperimentService.addEID(value, node)
        then:
        assert (value.getChild(EntityService.EID) == null) == expectedResults
        where:
        label                       | node                                | expectedResults
        "'No eid' key in JSON Node" | mapper.readTree("{ \"noeid\": 17}") | true
        "'eid' key in JSON Node"    | mapper.readTree("{ \"eid\": 17}")   | false

    }

    void "addCID #label"() {
        given:
        Value value = new Value(dataSource)
        when:
        this.restExperimentService.addCID(value, node)
        then:
        assert (value.getChild(EntityService.CID) == null) == expectedResults
        where:
        label                       | node                                | expectedResults
        "'No cid' key in JSON Node" | mapper.readTree("{ \"nocid\": 17}") | true
        "'cid' key in JSON Node"    | mapper.readTree("{ \"cid\": 17}")   | false

    }

    void "addSID #label"() {
        given:
        Value value = new Value(dataSource)
        when:
        this.restExperimentService.addSID(value, node)
        then:
        assert (value.getChild(EntityService.SID) == null) == expectedResults
        where:
        label                       | node                                | expectedResults
        "'No sid' key in JSON Node" | mapper.readTree("{ \"nosid\": 17}") | true
        "'sid' key in JSON Node"    | mapper.readTree("{ \"sid\": 17}")   | false

    }

    void "addOutcome #label"() {
        given:
        Value value = new Value(dataSource)
        when:
        this.restExperimentService.addOutcome(value, node)
        then:
        assert (value.getChild(EntityService.OUTCOME) == null) == expectedResults
        where:
        label                           | node                                    | expectedResults
        "'No outcome' key in JSON Node" | mapper.readTree("{ \"nooutcome\": 17}") | true
        "'outcome' key in JSON Node"    | mapper.readTree("{ \"outcome\": 17}")   | false

    }

    void "addPotency #label"() {
        given:
        Value value = new Value(dataSource)
        when:
        this.restExperimentService.addPotency(value, node)
        then:
        assert (value.getChild(EntityService.POTENCY) == null) == expectedResults
        where:
        label                           | node                                    | expectedResults
        "'No potency' key in JSON Node" | mapper.readTree("{ \"nopotency\": 17}") | true
        "'potency' key in JSON Node"    | mapper.readTree("{ \"potency\": 17}")   | false

    }

    void "addConcUnit #label"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        when:
        this.restExperimentService.addConcUnit(hcv, node)
        then:
        assert (hcv.getConcentrationUnits() == null) == expectedResults
        where:
        label                            | node                                      | expectedResults
        "'No concUnit' key in JSON Node" | mapper.readTree("{ \"noconcUnit\": 17}")  | true
        "'concUnit' key in JSON Node"    | mapper.readTree("{ \"concUnit\":\"uM\"}") | false

    }

    void "addSlope #label"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        when:
        this.restExperimentService.addSlope(hcv, node)
        then:
        assert (hcv.getSlope() == null) == expectedResults
        where:
        label                        | node                                 | expectedResults
        "No 'ac50' key in JSON Node" | mapper.readTree("{ \"noac50\": 17}") | true
        "'ac50' key in JSON Node"    | mapper.readTree("{ \"ac50\":17}")    | false

    }

    void "addCoeff #label"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        when:
        this.restExperimentService.addCoeff(hcv, node)
        then:
        assert (hcv.getCoef() == null) == expectedResults
        where:
        label                        | node                                 | expectedResults
        "No 'hill' key in JSON Node" | mapper.readTree("{ \"nohill\": 17}") | true
        "'hill' key in JSON Node"    | mapper.readTree("{ \"hill\":17}")    | false
    }

    void "addSInf #label"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        when:
        this.restExperimentService.addSinf(hcv, node)
        then:
        assert (hcv.getSinf() == null) == expectedResults
        where:
        label                        | node                                 | expectedResults
        "No 'sInf' key in JSON Node" | mapper.readTree("{ \"nosInf\": 17}") | true
        "'SInf' key in JSON Node"    | mapper.readTree("{ \"sInf\":17}")    | false
    }

    void "addReadOuts #label"() {
        given:
        final Value value = new Value(dataSource)
        when:
        this.restExperimentService.addReadOuts(value, node)
        then:
        assert (value.getChildCount() == 0) == expectedResults
        where:
        label                            | node                                 | expectedResults
        "No 'readouts' key in JSON Node" | mapper.readTree("{ \"nos0\": 17}")   | true
        "'readouts' key in JSON Node"    | mapper.readTree(EXPERIMENT_READOUTS) | false

    }

    void "parseReadout #label"() {
        given:
        final Value value = new Value(dataSource)
        when:
        final HillCurveValue hcv = (HillCurveValue)this.restExperimentService.parseReadout(value, node)
        then:
        assert hcv
        assert hcv.getS0()
        assert hcv.getCoef()
        assert hcv.getConc()
        assert hcv.getResponse()
        assert hcv.getSlope()
        assert hcv.getSinf()
        assert !hcv.getConcentrationUnits()
        where:
        label               | node
        "'Single Reada out" | mapper.readTree(EXPERIMENT_SINGLE_READOUT)

    }

    void "parseReadout - Empty Node"() {
        given:
        final Value value = new Value(dataSource)
        when:
        final HillCurveValue hcv = (HillCurveValue)this.restExperimentService.parseReadout(value, mapper.readTree("{ \"nos0\": 17}"))
        then:
        assert !hcv
    }

    void "extractValuesFromNode #label"() {
        given:
        final ArrayNode arrayNode = null
        when:
        final List<Value> values = this.restExperimentService.extractValuesFromNode(arrayNode, dataSource)
        then:
        assert values.isEmpty()
    }

    void "processRootNode #label"() {

        when:
        List<Value> values = this.restExperimentService.processRootNode(node, dataSource)
        then:
        assert values.isEmpty() == expectedResults
        where:
        label                              | node                                | expectedResults
        "'collection' key in JSON Node"    | mapper.readTree(EXPERIMENT_RESULTS) | false
        "No 'collection' key in JSON Node" | mapper.readTree("{ \"s0\":17}")     | true

    }

    void "buildExperimentQuery #label"() {
        given:
        final long top = 10
        final long skip = 10
        Experiment experiment = new Experiment()
        experiment.setId(111)
        when:
        String query = this.restExperimentService.buildExperimentQuery(experiment, etag, top, skip)
        then:
        assert query == expected
        where:
        label              | expected                                                             | etag
        "Etag is null"     | "base/experiments/111/exptdata?skip=10&top=10&expand=true"           | null
        "Etag is not null" | "base/experiments/111/etag/e123/exptdata?skip=10&top=10&expand=true" | "e123"
    }

    void "addS0 #label"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        when:
        this.restExperimentService.addS0(hcv, node)
        then:
        assert (hcv.getS0() == null) == expectedResults
        where:
        label                      | node                               | expectedResults
        "'s0' key in JSON Node"    | mapper.readTree("{ \"nos0\": 17}") | true
        "No 's0' key in JSON Node" | mapper.readTree("{ \"s0\":17}")    | false
    }

    void "addCrc"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        final ArrayNode xyCoordinate = mapper.readTree("[1.1,3.2]") as ArrayNode
        when:
        this.restExperimentService.addCrc(hcv, xyCoordinate)
        then:
        List<Double> concentrations = hcv.getConc() as List<Double>
        List<Double> responses = hcv.getResponse() as List<Double>
        assert responses.get(0) == 3.2
        assert concentrations.get(0) == 1.1
    }

    void "addCrcs"() {
        given:
        final HillCurveValue hcv = new HillCurveValue(dataSource)
        String crcs = '''{"cr":
        [
                [
                        1.1,
                        3.2
                ],
                [
                        5.4,
                        6
                ]
         ]
        }'''
        final JsonNode crcNode = mapper.readTree(crcs)
        when:
        this.restExperimentService.addCrcs(hcv, crcNode)
        then:
        List<Double> concentrations = hcv.getConc() as List<Double>
        List<Double> responses = hcv.getResponse() as List<Double>
        assert concentrations.size() == 2
        assert responses.size() == 2
        assert responses.get(0) == 3.2
        assert concentrations.get(0) == 1.1
    }

}


