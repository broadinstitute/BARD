package bard.db.registration

import bard.db.ContextService
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.project.ProjectDocument
import bard.taglib.TextFormatTagLib
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ContextService)
@Build([Assay, AssayContext])
@Mock([Assay, AssayContext])
class ContextServiceUnitSpec extends Specification {

    def 'test createContext'() {
        setup:
        AbstractContextOwner owner = Assay.build()

        when:
        AbstractContext context = service.createContext(owner, "name", "section");

        then:
        context != null
        owner.contexts.contains(context)
        context.contextGroup == "section"
        context.contextName == "name"
    }

    def 'test deleteContext'() {
        setup:
        AbstractContext context = AssayContext.build()
        AbstractContextOwner owner = context.owner

        when:
        service.deleteContext(context)

        then:
        !owner.contexts.contains(context)
    }
}
//    def 'test updateContextName'() {
//        setup:
//        AbstractContext context = AssayContext.build()
//
//        when:
//        service.updateContext(context)
//
//        then:
//    }
