package bardqueryapi.compoundBioActivitySummary

import bard.core.Value
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.util.FilterTypes
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class CompoundBioActivitySummaryBuilderUnitSpec extends Specification {
    QueryService queryService
    CompoundBioActivitySummaryBuilder compoundBioActivitySummaryBuilder

    @Shared ProjectResult projectResult = new ProjectResult()
    @Shared Assay assay1 = new Assay(bardAssayId: 1L, name: "A1")
    @Shared Assay assay2 = new Assay(bardAssayId: 2L, name: "A2")
    @Shared ProjectExpanded project1 = new ProjectExpanded(name: "P1")
    @Shared Project project2 = new Project(bardProjectId: 2L, name: "P2")
    @Shared Project project3 = new Project(bardProjectId: 3L, name: "P3")
    @Shared Activity activity1 = new Activity(bardExptId: 1L, eid: 1L, bardAssayId: 1L, bardProjId: [2L])
    @Shared Activity activity2 = new Activity(bardExptId: 4L, eid: 4L, bardAssayId: 2L, bardProjId: [3L])
    @Shared Activity activityCrSer
    @Shared Activity activitySp
    @Shared Activity activityUnclass
    @Shared List<Assay> testedAssays = [assay1, assay2]
    @Shared List<Assay> hitAssays = [assay1]
    @Shared Map groupedByExperimentalData
    @Shared ExperimentSearch experiment1 = new ExperimentSearch(bardExptId: 1L)
    @Shared ExperimentSearch experiment2 = new ExperimentSearch(bardExptId: 2L)
    @Shared Map<Long, List<ExperimentSearch>> experimentsMap = [1L: [experiment1], 2L: [experiment2]]

    @Shared ObjectMapper objectMapper = new ObjectMapper()
    final String ACTIVITY_UNCLASS = """
{
   "exptDataId" : "21.16953511",
   "eid" : 0,
   "cid" : 2382353,
   "sid" : 16953511,
   "bardExptId" : 21,
   "bardAssayId" : 43,
   "capExptId" : 967,
   "capAssayId" : 973,
   "capProjId" :
      [
         3
      ],
   "bardProjId" :
      [
         2
      ],
   "resultJson" : "{\\"responseClass\\":\\"UNCLASS\\",\\"bardExptId\\":21,\\"capExptId\\":967,\\"bardAssayId\\":43,\\"capAssayId\\":973,\\"sid\\":16953511,\\"cid\\":2382353,\\"potency\\":null,\\"priorityElements\\":[],\\"rootElements\\":[{\\"displayName\\":\\"percent activity\\",\\"dictElemId\\":986,\\"value\\":\\"122.8\\"},{\\"displayName\\":\\"reproducibility\\",\\"dictElemId\\":1490,\\"value\\":\\"1.0\\"},{\\"displayName\\":\\"percent activity\\",\\"dictElemId\\":986,\\"value\\":\\"120.7\\"},{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\",\\"childElements\\":[{\\"displayName\\":\\"PubChem activity score\\",\\"dictElemId\\":898,\\"value\\":\\"122.0\\"}]}],\\"projects\\":[{\\"bardProjId\\":2,\\"capProjId\\":3}]}",
   "runset" : "default",
   "outcome" : 2,
   "score" : 122,
   "classification" : null,
   "potency" : null,
   "readouts" : null,
   "resourcePath" : "/exptdata/21.16953511"
}
"""

    final String ACTIVITY_SP = """
{
   "exptDataId" : "2.115950079",
   "eid" : 0,
   "cid" : 650573,
   "sid" : 115950079,
   "bardExptId" : 2,
   "bardAssayId" : 1,
   "capExptId" : 2,
   "capAssayId" : 1,
   "capProjId" :
      [
         3
      ],
   "bardProjId" :
      [
         2
      ],
   "resultJson" : "{\\"responseClass\\":\\"SP\\",\\"bardExptId\\":17,\\"capExptId\\":5514,\\"bardAssayId\\":39,\\"capAssayId\\":5519,\\"sid\\":115950079,\\"cid\\":650573,\\"potency\\":null,\\"priorityElements\\":[{\\"displayName\\":\\"REPLICATE_A_ACTIVITY_SCORE_10.01uM_(%)\\",\\"responseUnit\\":\\"percent\\",\\"testConcUnit\\":\\"um\\",\\"testConc\\":10.01000023,\\"value\\":\\"-0.394\\"}],\\"rootElements\\":[{\\"displayName\\":\\"percent efflux\\",\\"dictElemId\\":1009,\\"value\\":\\"13.0684\\"},{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\"},{\\"displayName\\":\\"percent efflux\\",\\"dictElemId\\":1009,\\"value\\":\\"13.0882\\"}],\\"projects\\":[{\\"bardProjId\\":2,\\"capProjId\\":3}]}",
   "runset" : "default",
   "outcome" : 2,
   "score" : 0,
   "classification" : null,
   "potency" : null,
   "readouts" : null,
   "resourcePath" : "/exptdata/17.115950079"
}
"""

    final String ACTIVITY_CR_SER = """
{
   "exptDataId" : "1.115950071",
   "eid" : 0,
   "cid" : 1017704,
   "sid" : 115950071,
   "bardExptId" : 1,
   "bardAssayId" : 2,
   "capExptId" : 1,
   "capAssayId" : 2,
   "capProjId" :
      [
         3
      ],
   "bardProjId" :
      [
         2
      ],
   "resultJson" : "{\\"responseClass\\":\\"CR_SER\\",\\"bardExptId\\":23,\\"capExptId\\":5409,\\"bardAssayId\\":44,\\"capAssayId\\":5499,\\"sid\\":115950071,\\"cid\\":1017704,\\"potency\\":0.593,\\"priorityElements\\":[{\\"displayName\\":\\"AC50\\",\\"dictElemId\\":959,\\"value\\":\\"0.593\\",\\"concResponseSeries\\":{\\"responseUnit\\":\\"percent activity (maximum)\\",\\"testConcUnit\\":\\"uM\\",\\"crSeriesDictId\\":986,\\"concRespParams\\":{\\"s0\\":0.0,\\"sInf\\":-100.0,\\"hillCoef\\":0.759,\\"logEc50\\":-0.22694530663573742},\\"concRespPoints\\":[{\\"testConc\\":35.0,\\"value\\":\\"-94.185\\"},{\\"testConc\\":0.015,\\"value\\":\\"-9.784\\"},{\\"testConc\\":0.046,\\"value\\":\\"-16.434\\"},{\\"testConc\\":0.42,\\"value\\":\\"-37.826\\"},{\\"testConc\\":0.42,\\"value\\":\\"-53.935\\"},{\\"testConc\\":1.2,\\"value\\":\\"-79.963\\"},{\\"testConc\\":11.0,\\"value\\":\\"-83.619\\"},{\\"testConc\\":11.0,\\"value\\":\\"-91.119\\"},{\\"testConc\\":35.0,\\"value\\":\\"-94.185\\"}]},\\"childElements\\":[{\\"displayName\\":\\"pAC50\\",\\"dictElemId\\":1378,\\"value\\":\\"6.227\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"},{\\"displayName\\":\\"number of points\\",\\"dictElemId\\":1397,\\"value\\":\\"16.0\\"}]}],\\"rootElements\\":[{\\"displayName\\":\\"PubChem outcome\\",\\"dictElemId\\":896,\\"value\\":\\"Active\\",\\"childElements\\":[{\\"displayName\\":\\"PubChem activity score\\",\\"dictElemId\\":898,\\"value\\":\\"62.0\\"}]}],\\"projects\\":[{\\"bardProjId\\":2,\\"capProjId\\":3}]}",
   "runset" : "default",
   "outcome" : 2,
   "score" : 62,
   "classification" : null,
   "potency" : 0.593,
   "readouts" : null,
   "resourcePath" : "/exptdata/23.115950071"
}
    """

    void setup() {
        this.queryService = Mock(QueryService)
        this.compoundBioActivitySummaryBuilder = new CompoundBioActivitySummaryBuilder(this.queryService)
        this.activityCrSer = objectMapper.readValue(ACTIVITY_CR_SER, Activity.class)
        this.activitySp = objectMapper.readValue(ACTIVITY_SP, Activity.class)
        this.activityUnclass = objectMapper.readValue(ACTIVITY_UNCLASS, Activity.class)
        this.groupedByExperimentalData = [1L: [activityCrSer], 2L: [activitySp]]
    }

    void "test buildModel #label"() {
        when:
        final TableModel tableModel = compoundBioActivitySummaryBuilder.buildModel(groupBy,
                groupedByExperimentalData,
                testedAssays,
                hitAssays,
                filterTypes,
                [],
                experimentsMap,
                sortedKeys,
                -10,
                10)

        then:
        this.queryService.findProjectsByPIDs(_) >> { ['projectAdapters': [new ProjectAdapter(project1)]] }

        assert tableModel.columnHeaders.size() == 2
        assert tableModel.data.size() == expectedTableModelDataSize
        assert tableModel.data.first().first().class == expectedResourceType

        where:
        label                            | sortedKeys | groupBy              | filterTypes                                           | expectedTableModelDataSize | expectedResourceType
        "group-by assay, tested"         | [1, 2]     | GroupByTypes.ASSAY   | [FilterTypes.TESTED]                                  | 2                          | AssayValue
        "group-by assay, actives-only"   | [1]        | GroupByTypes.ASSAY   | []                                                    | 1                          | AssayValue
        "group-by project, tested"       | [1, 2]     | GroupByTypes.PROJECT | [FilterTypes.TESTED]                                  | 2                          | ProjectValue
        "group-by project, actives-pnly" | [1]        | GroupByTypes.PROJECT | []                                                    | 1                          | ProjectValue
        "group-by assay, single-point"   | [1, 2]     | GroupByTypes.ASSAY   | [FilterTypes.TESTED, FilterTypes.SINGLE_POINT_RESULT] | 1                          | AssayValue
    }

    void "test convertExperimentResultsToValues #label"() {
        when:
        List<WebQueryValue> values = compoundBioActivitySummaryBuilder.convertExperimentResultsToValues(exptData)

        then:
        assert values.size() == 1
        assert values.first().class == expectedWebQueryValueClass

        where:
        label                | exptData      | expectedWebQueryValueClass
        "CR_CER result-type" | activityCrSer | ConcentrationResponseSeriesValue.class
        "SP result-type"     | activitySp    | ListValue.class
    }

    void "test generateFacetsFromResultTypeMap with an empty map"() {
        when:
        Collection<Value> facets = compoundBioActivitySummaryBuilder.generateFacetsFromResultTypeMap([:])

        then:
        assert facets.first().id == 'result_type'
        assert facets.first().children.isEmpty()
    }

    void "test generateFacetsFromResultTypeMap with a few result types"() {
        when:
        List<Value> facets = compoundBioActivitySummaryBuilder.generateFacetsFromResultTypeMap(['AC50': 1, 'activity': 2])

        then:
        assert facets.first().id == 'result_type'
        assert facets.first().children.toList()[0].id == 'activity'
        assert facets.first().children.toList()[0].value == 2
        assert facets.first().children.toList()[1].id == 'AC50'
        assert facets.first().children.toList()[1].value == 1
    }
}
