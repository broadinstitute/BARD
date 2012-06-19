package bard.db.registration

import grails.test.mixin.TestFor
import org.junit.Before
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(AssayDefinitionController)
@Mock(Assay)
class AssayDefinitionControllerTests {

    Assay assay

    @Before
    void setUp() {
        assay = new Assay(assayName: "Test", assayVersion: "1")
        assay.setId(4)
        assay.save()
        assert assay.validate()
    }

    void testShow() {
        params.id = 4
        def model = controller.show()

        assert model.assayInstance == assay
    }
}
