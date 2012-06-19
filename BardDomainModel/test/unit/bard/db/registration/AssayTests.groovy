package bard.db.registration



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Assay)
class AssayTests {

    Assay assay

    @Before
    public void setUp() {

        mockForConstraintsTests(Assay)

        assay = new Assay(assayName: "Test", assayVersion: "2", assayStatus: "Active", readyForExtraction: "Complete")
    }

    void testValidConstraints() {

        assertTrue "Assay is valid", assay.validate()
    }

    void testNoAssayName() {
        assay.setAssayName(null)

        assertFalse "Assay should not be valid with no name", assay.validate()
        assertEquals "nullable", assay.errors["assayName"]

    }
}
