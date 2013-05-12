package bard.db.registration

import bard.db.enums.AssayType
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
        Assay template = Assay.build(assayType: AssayType.TEMPLATE)
        Assay nontemplate = Assay.build(assayType: AssayType.REGULAR)

        def m = controller.listTemplates()

        then:
        m.templates == [template]
    }
}
