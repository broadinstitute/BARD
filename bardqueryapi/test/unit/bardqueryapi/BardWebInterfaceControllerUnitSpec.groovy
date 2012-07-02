package bardqueryapi

import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
class BardWebInterfaceControllerUnitSpec extends Specification {

    QueryAssayApiService queryAssayApiService
    QueryExecutorService queryExecutorService
    QueryTargetApiService queryTargetApiService

    void setup() {
        queryAssayApiService = Mock()
        controller.queryAssayApiService = this.queryAssayApiService
        queryExecutorService = Mock()
        controller.queryExecutorService = this.queryExecutorService
        queryTargetApiService = Mock()
        controller.queryTargetApiService = this.queryTargetApiService
        controller.grailsApplication.config.ncgc.server.root.url = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     */
    void "test findCompoundsForAssay if-branch #label"() {
        when:
        request.method = 'GET'
        controller.findCompoundsForAssay(max, offset, assayId)
        then:
        1 * queryAssayApiService.getTotalAssayCompounds(assayId) >> { totalAssayCompounds }
        1 * queryAssayApiService.getAssayCompoundsResultset(max, offset, assayId) >> { assayCompoundsResultset }

        "/bardWebInterface/findCompoundsForAssay" == view
        assayCompoundsResultset == model.assayCompoundsJsonArray
        totalAssayCompounds == model.totalCompounds
        assayId == model.aid

        where:
        label                                         | totalAssayCompounds | assayCompoundsResultset            | max            | offset         | assayId          | expectedTotalCompounds
        "Render view with name findCompoundsForAssay" | new Integer(10)     | ['/bard/rest/v1/compounds/661090'] | new Integer(1) | new Integer(2) | new Integer(872) | 1
        "Render view with name findCompoundsForAssay" | new Integer(0)      | ['a', 'b']                         | new Integer(1) | new Integer(2) | new Integer(872) | 1
    }

    void "test findCompoundsForAssay else-branch #label"() {
        when:
        request.method = 'GET'
        controller.findCompoundsForAssay(max, offset, assayId)

        then:
        0 * queryAssayApiService.getTotalAssayCompounds(assayId) >> { totalAssayCompounds }
        0 * queryAssayApiService.getAssayCompoundsResultset(max, offset, assayId) >> { assayCompoundsResultset }

        "Assay ID is not defined" == response.text
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        label                                         | totalAssayCompounds | assayCompoundsResultset | max            | offset         | assayId        | expectedTotalCompounds
        "Render view with name findCompoundsForAssay" | new Integer(10)     | ['a']                   | new Integer(1) | new Integer(2) | new Integer(0) | 1
    }
    /**
     */
    void "test search for Target if-branch #label"() {
        when:
        request.method = 'GET'
        params.searchString = target
        params.searchType = searchType.toString()
        controller.search()
        then:
        1 * queryTargetApiService.findAssaysForAccessionTarget(target) >> { expectedAssays }

        "/bardWebInterface/homePage" == view
        expectedAssays == model.assays
        0 == model.totalCompounds
        assert model.compounds.isEmpty()
        assert model.experiments.isEmpty()
        assert model.projects.isEmpty()
        where:
        label                    | expectedAssays     | target | searchType
        "Find Assays for target" | ['60001', '60003'] | "2"    | SearchType.TARGET
    }
    /**
     */
    void "test search for compounds given an assay #label"() {
        given:

        when: "We search with the Assay ids for Compounds"
        request.method = 'GET'
        params.searchString = assayId
        params.searchType = SearchType.COMPOUNDS.toString()
        params.max = max
        params.offset = offset
        controller.search()
        then: "We expect to call the following methods and then go back to the home page"
        1 * queryAssayApiService.getTotalAssayCompounds(new Integer(assayId)) >> { totalAssayCompounds }
        1 * queryAssayApiService.getAssayCompoundsResultset(max, offset, new Integer(assayId)) >> { assayCompoundsResultset }

        "/bardWebInterface/homePage" == view
        model.assays == [assayId]
        model.compounds == assayCompoundsResultset
        totalAssayCompounds == model.totalCompounds
        assert model.experiments.isEmpty()
        assert model.projects.isEmpty()
        where:
        label                                           | totalAssayCompounds | assayCompoundsResultset            | max            | offset         | assayId | expectedTotalCompounds
        "Search for compounds, given a single assay id" | new Integer(10)     | ['/bard/rest/v1/compounds/661090'] | new Integer(1) | new Integer(2) | '872'   | 1
    }
    /**
     */
    void "test search for compounds given an assayid which is not a number #label"() {
        given:

        when: "We search with the Assay ids for Compounds"
        request.method = 'GET'
        params.searchString = "P1234"
        params.searchType = SearchType.COMPOUNDS.toString()

        controller.search()
        then: "We expect to call the following methods and then go back to the home page"
        0 * queryAssayApiService.getTotalAssayCompounds(_) >> { }
        0 * queryAssayApiService.getAssayCompoundsResultset(_, _, _) >> { }
        assert flash.message == 'Search String must be a number'
        assert response.redirectedUrl == '/bardWebInterface/homePage?searchString=P1234'
    }
    /**
     */
    void "test search for compounds given a multiple assays #label"() {
        given:

        when: "We search with the Assay ids for Compounds"
        request.method = 'GET'
        params.searchString = assayId1 + " " + assayId2
        params.searchType = SearchType.COMPOUNDS.toString()
        params.max = max
        params.offset = offset
        controller.search()
        then: "We expect to call the following methods and then go back to the home page"
        2 * queryAssayApiService.getTotalAssayCompounds(_) >> { totalAssayCompounds }
        2 * queryAssayApiService.getAssayCompoundsResultset(_, _, _) >> { assayCompoundsResultset }

        "/bardWebInterface/homePage" == view
        model.assays == [assayId1, assayId2]
        model.compounds == assayCompoundsResultset
        totalAssayCompounds * 2 == model.totalCompounds
        assert model.experiments.isEmpty()
        assert model.projects.isEmpty()
        where:
        label                                           | totalAssayCompounds | assayCompoundsResultset            | max            | offset         | assayId1 | assayId2 | expectedTotalCompounds
        "Search for compounds, given a single assay id" | new Integer(10)     | ['/bard/rest/v1/compounds/661090'] | new Integer(1) | new Integer(2) | '872'    | '873'    | 2
    }
    /**
     */
    void "test search for else-branch #label"() {
        when:
        request.method = 'GET'
        controller.search()
        then:
        0 * queryTargetApiService.findAssaysForAccessionTarget(_) >> { }
        0 * queryAssayApiService.getTotalAssayCompounds(_) >> { }
        0 * queryAssayApiService.getAssayCompoundsResultset(_, _, _) >> { }
        assert flash.message == 'Search String is required'
        assert response.redirectedUrl == '/bardWebInterface/homePage'

    }

    /**
     */
    void "test search - erroneous input #label"() {
        given:

        when: "We search with the Assay ids for Compounds"
        request.method = 'GET'
        params.searchString = assayId
        params.searchType = SearchType.COMPOUNDS.toString()
        params.max = max
        params.offset = offset
        controller.search()
        then: "We expect to call the following methods and then go back to the home page"
        1 * queryAssayApiService.getTotalAssayCompounds(_) >> { totalAssayCompounds }
        0 * queryAssayApiService.getAssayCompoundsResultset(_, _, _) >> { assayCompoundsResultset }

        "/bardWebInterface/homePage" == view
        model.assays == [assayId]
        model.compounds == assayCompoundsResultset
        totalAssayCompounds * 2 == model.totalCompounds
        assert model.experiments.isEmpty()
        assert model.projects.isEmpty()
        where:
        label                                           | totalAssayCompounds | assayCompoundsResultset | max            | offset         | assayId   | expectedTotalCompounds
        "Search for compounds for a non-existing assay" | new Integer(0)      | []                      | new Integer(1) | new Integer(2) | '1234567' | 0
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(new Integer(872))
        then:
        1 * queryExecutorService.executeGetRequestJSON("httpMock:///bard/rest/v1/compounds/872", null) >> { compoundJson }

        "/bardWebInterface/showCompound" == view
        expectedCompoundJson.toString() == model.compoundJson.toString()
        872 == model.compoundId

        where:
        label                                | compoundJson                               | expectedCompoundJson
        "compound not found - error message" | [errorMessage: 'error message'] as JSON    | null
        "Return a compound"                  | ['/bard/rest/v1/compounds/661090'] as JSON | ['/bard/rest/v1/compounds/661090'] as JSON
    }
}
