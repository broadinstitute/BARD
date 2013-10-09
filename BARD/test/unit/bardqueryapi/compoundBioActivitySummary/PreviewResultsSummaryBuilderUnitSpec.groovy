package bardqueryapi.compoundBioActivitySummary

import bardqueryapi.*
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class PreviewResultsSummaryBuilderUnitSpec extends Specification {

    PreviewResultsSummaryBuilder previewResultsSummaryBuilder



    @Shared ObjectMapper objectMapper = new ObjectMapper()


    void setup() {
        this.previewResultsSummaryBuilder = new PreviewResultsSummaryBuilder()

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
        List<WebQueryValue> sortedValues = this.previewResultsSummaryBuilder.sortWebQueryValues(unsortedValues)
        then:
        assert sortedValues.get(0).getClass() == ConcentrationResponseSeriesValue
        assert sortedValues.get(1).getClass() == ListValue
        assert sortedValues.get(2).getClass() == PairValue
        assert sortedValues.get(3).getClass() == StringValue
        assert sortedValues.get(4).getClass() == AssayValue
    }

    void "test parse Units #desc"() {
        when:
        String units = this.previewResultsSummaryBuilder.parseUnits(displayValue)
        then:
        assert units == expectedUnits

        where:
        desc         | displayValue | expectedUnits
        "With units" | "25 um"      | "um"
        "No Units"   | "22"         | ""

    }
//    void "test buildModel #label"() {
//        when:
//        final TableModel tableModel = compoundBioActivitySummaryBuilder.buildModel(groupBy,
//                groupedByExperimentalData,
//                testedAssays,
//                hitAssays,
//                filterTypes,
//                [],
//                experimentsMap,
//                sortedKeys,
//                -10,
//                10)
//
//        then:
//        this.queryService.findProjectsByPIDs(_) >> { ['projectAdapters': [new ProjectAdapter(project1)]] }
//
//        assert tableModel.columnHeaders.size() == 2
//        assert tableModel.data.size() == expectedTableModelDataSize
//        assert tableModel.data.first().first().class == expectedResourceType
//
//        where:
//        label                            | sortedKeys | groupBy              | filterTypes                                           | expectedTableModelDataSize | expectedResourceType
//        "group-by assay, tested"         | [1, 2]     | GroupByTypes.ASSAY   | [FilterTypes.TESTED]                                  | 2                          | AssayValue
//        "group-by assay, actives-only"   | [1]        | GroupByTypes.ASSAY   | []                                                    | 1                          | AssayValue
//        "group-by project, tested"       | [1, 2]     | GroupByTypes.PROJECT | [FilterTypes.TESTED]                                  | 2                          | ProjectValue
//        "group-by project, actives-pnly" | [1]        | GroupByTypes.PROJECT | []                                                    | 1                          | ProjectValue
//        "group-by assay, single-point"   | [1, 2]     | GroupByTypes.ASSAY   | [FilterTypes.TESTED, FilterTypes.SINGLE_POINT_RESULT] | 1                          | AssayValue
//    }
//
//    void "test convertExperimentResultsToValues #label"() {
//        when:
//        List<WebQueryValue> values = compoundBioActivitySummaryBuilder.convertExperimentResultsToValues(exptData)
//
//        then:
//        assert values.size() == 1
//        assert values.first().class == expectedWebQueryValueClass
//
//        where:
//        label                | exptData      | expectedWebQueryValueClass
//        "CR_CER result-type" | activityCrSer | ConcentrationResponseSeriesValue.class
//        "SP result-type"     | activitySp    | ListValue.class
//    }
//
//    void "test generateFacetsFromResultTypeMap with an empty map"() {
//        when:
//        Collection<Value> facets = compoundBioActivitySummaryBuilder.generateFacetsFromResultTypeMap([:])
//
//        then:
//        assert facets.first().id == 'result_type'
//        assert facets.first().children.isEmpty()
//    }
//
//    void "test generateFacetsFromResultTypeMap with a few result types"() {
//        when:
//        List<Value> facets = compoundBioActivitySummaryBuilder.generateFacetsFromResultTypeMap(['AC50': 1, 'activity': 2])
//
//        then:
//        assert facets.first().id == 'result_type'
//        assert facets.first().children.toList()[0].id == 'activity'
//        assert facets.first().children.toList()[0].value == 2
//        assert facets.first().children.toList()[1].id == 'AC50'
//        assert facets.first().children.toList()[1].value == 1
//    }
}
