package bardqueryapi.compoundBioActivitySummary

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.dictionary.ResultTypeTree
import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bardqueryapi.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Element, ResultTypeTree])
@Mock([Element, ResultTypeTree])
@TestMixin(ServiceUnitTestMixin)
@Unroll
class PreviewExperimentResultsSummaryBuilderUnitSpec extends Specification {

    PreviewExperimentResultsSummaryBuilder previewExperimentResultsSummaryBuilder
    JsonResultContextItem jsonResultContextItem
    JsonResult childJsonResult
    JsonResult parentJsonResult

    def setup() {
        this.previewExperimentResultsSummaryBuilder = new PreviewExperimentResultsSummaryBuilder()
        this.previewExperimentResultsSummaryBuilder.ontologyDataAccessService = Mock(OntologyDataAccessService)

        Element element = Element.build(label: "AC50")
        ResultTypeTree.build(element: element,
                fullPath: "",
                label: "")

        this.jsonResultContextItem = new JsonResultContextItem(
                qualifier: "<", valueMax: 200, valueMin: -200,
                valueNum: 102, valueDisplay: "value", itemId: 100,
                attribute: "attribute",
                attributeId: 53, valueElementId: 100);
        this.childJsonResult = new JsonResult(qualifier: "=", replicateNumber: 1, resultId: 100,
                resultTypeId: element.id, resultType: element.label, valueDisplay: "valueDisplay", valueMax: 1000,
                valueMin: 0, valueNum: 10, relationship: "Derives", statsModifierId: 15)

        this.parentJsonResult = new JsonResult(
                qualifier: "=",
                replicateNumber: 1,
                resultId: 100,
                resultTypeId: element.id,
                resultType: element.label,
                valueDisplay: "valueDisplay",
                valueMax: 1000, valueMin: 0, valueNum: 10,
                related: [childJsonResult],
                contextItems: [jsonResultContextItem],
                statsModifierId: 15
        )
    }

    void "test Sort Web Query Values"() {
        given:
        PairValue pairValue = new PairValue()
        ListValue listValue = new ListValue()
        StringValue stringValue = new StringValue()
        AssayValue assayValue = new AssayValue()
        ConcentrationResponseSeriesValue concentrationResponseSeriesValue = new ConcentrationResponseSeriesValue()
        List<WebQueryValue> unsortedValues = [pairValue, listValue, stringValue, assayValue, concentrationResponseSeriesValue]
        when:
        List<WebQueryValue> sortedValues = this.previewExperimentResultsSummaryBuilder.sortWebQueryValues(unsortedValues)
        then:
        assert sortedValues.get(0).getClass() == ConcentrationResponseSeriesValue
        assert sortedValues.get(1).getClass() == ListValue
        assert sortedValues.get(2).getClass() == PairValue
        assert sortedValues.get(3).getClass() == StringValue
        assert sortedValues.get(4).getClass() == AssayValue
    }

    void "test parse Units #desc"() {
        when:
        String units = this.previewExperimentResultsSummaryBuilder.parseUnits(displayValue)
        then:
        assert units == expectedUnits

        where:
        desc         | displayValue | expectedUnits
        "With units" | "25 um"      | "um"
        "No Units"   | "22"         | ""

    }

    void "test handleContextItemsAndRelatedResults"() {
        given:
        final Map resultsMap = [:]
        resultsMap.priorityElements = []
        resultsMap.priorityElementValues = []
        resultsMap.yNormMin = null
        resultsMap.yNormMax = null
        resultsMap.outcome = null
        resultsMap.experimentalValues = []
        resultsMap.childElements = []
        when:
        previewExperimentResultsSummaryBuilder.handleContextItemsAndRelatedResults(resultsMap, this.parentJsonResult)
        then:
        assert resultsMap
        assert resultsMap.childElements
    }
}
