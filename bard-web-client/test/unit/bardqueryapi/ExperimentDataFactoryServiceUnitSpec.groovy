package bardqueryapi

import bard.core.SearchParams
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.util.FilterTypes

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(ExperimentDataFactoryService)
class ExperimentDataFactoryServiceUnitSpec extends Specification {

    void createTableModel() {
        given:
        SpreadSheetInput spreadSheetInput = new SpreadSheetInput()
        GroupByTypes groupTypes = GroupByTypes.ASSAY
        List<FilterTypes> filterTypes = []
        SearchParams searchParams = new SearchParams()
        when:
        WebQueryTableModel tableModel = service.createTableModel(spreadSheetInput, groupTypes, filterTypes, searchParams)
        then:
        assert !tableModel

    }
}
