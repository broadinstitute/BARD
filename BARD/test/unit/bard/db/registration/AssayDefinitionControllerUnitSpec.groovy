package bard.db.registration

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Before
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(AssayDefinitionController)
@Mock(Assay)
class AssayDefinitionControllerUnitSpec extends Specification {

    Assay assay

    @Before
    void setup() {
        assay = new Assay(assayName: "Test", assayVersion: "1")
        assay.setId(4)
        assay.save()
        assert assay.validate()
    }

    void 'test show'() {
        given:
        defineBeans {
            cardFactoryService(CardFactoryService)
        }

        when:
        params.id = 4
        def model = controller.show()

        then:
        model.assayInstance == assay
        model.cardDtoList != null
    }

    void 'testFindById()'() {

        when:
        params.assayId = '4'
        controller.findById()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
    }

	void 'testFindByName'() {
        when:
		params.assayName = "Test"
		controller.findByName()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
	}
}