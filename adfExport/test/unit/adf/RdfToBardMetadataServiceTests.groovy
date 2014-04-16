package adf



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(RdfToBardMetadataService)
class RdfToBardMetadataServiceTests {

    def rdfToBardMetadataService
    void testSomething() {
       def m = rdfToBardMetadataService.createModel("test1.n3")
       rdfToBardMetadataService.handleAssay(m)
    }
}
