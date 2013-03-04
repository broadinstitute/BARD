package bard.db.registration

import bard.db.model.IDocumentType
import grails.plugin.spock.IntegrationSpec
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/19/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
class DocumentControllerIntegrationSpec extends IntegrationSpec {

    Assay assay;
    AssayDocument existingAssayDocument

    DocumentController controller

    @Before
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        controller = new DocumentController()
        assay = Assay.build()
        existingAssayDocument = AssayDocument.build(assay: assay)

    }


    void 'test save with invalid assay id'() {

        when:
        controller.params.assayId = -1
        controller.params.documentName = 'foo'
        controller.params.documentType = IDocumentType.DOCUMENT_TYPE_DESCRIPTION
        controller.save()

        then:

        controller.modelAndView.model.document.assayId == -1
        controller.modelAndView.model.document.hasErrors()
        ValidationErrors errors = controller.modelAndView.model.document.errors
        errors.getAllErrors().collect { it.codes }.flatten().contains('nullable')


    }
}
