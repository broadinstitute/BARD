package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import javax.servlet.http.HttpServletResponse
import grails.converters.JSON

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(BardWebInterfaceController)
class BardWebInterfaceControllerUnitSpec extends Specification {

    QueryAssayApiService queryAssayApiService
    QueryExecutorService queryExecutorService


    void setup() {
        queryAssayApiService = Mock()
        controller.queryAssayApiService = queryAssayApiService
        queryExecutorService = Mock()
        controller.queryExecutorService = queryExecutorService
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
