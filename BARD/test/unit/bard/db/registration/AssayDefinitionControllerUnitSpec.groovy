package bard.db.registration

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Before
import spock.lang.Specification
import grails.buildtestdata.mixin.Build

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */


@TestFor(AssayDefinitionController)
@Build(Assay)
class AssayDefinitionControllerUnitSpec extends Specification {

    Assay assay

    @Before
    void setup() {
        assay = Assay.build(assayName:'Test')
        assert assay.validate()
    }

    void 'test show'() {

        when:
        params.id = assay.id
        def model = controller.show()

        then:
        model.assayInstance == assay
    }

    void 'testFindById()'() {

        when:
        params.assayId = "${assay.id}"
        controller.findById()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
    }

	void 'testFindByName'() {
        when:
		params.assayName = assay.assayName
		controller.findByName()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
	}
}