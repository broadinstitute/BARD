package bard.db.registration

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/10/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AssayController)
@TestMixin(DomainClassUnitTestMixin)
@Build([Assay])
class AssayControllerSpec extends Specification {
    def 'test list templates'() {
        when:
        Assay template = Assay.build(assayType: Assay.TEMPLATE_ASSAY_TYPE)
        Assay nontemplate = Assay.build(assayType: Assay.REGULAR_ASSAY_TYPE)

        def m = controller.listTemplates()

        then:
        m.templates == [template]
    }
}
