package barddataqa



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(MarginalProductService)
class MarginalProductServiceTests {


    void setUp() {
    }

    void test() {
        String result = MarginalProductService.buildRmProbQuery()
        assert result
        println(result)
    }
}
