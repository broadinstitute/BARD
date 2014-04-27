package barddataqa



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TidIssueService)
class TidIssueServiceTests {

    void testSomething() {
        TidIssueService tidIssueService = new TidIssueService()

        println(tidIssueService.columnsString)
    }
}
