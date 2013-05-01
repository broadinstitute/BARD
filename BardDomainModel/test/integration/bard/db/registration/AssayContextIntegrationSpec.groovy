package bard.db.registration

import bard.db.BardIntegrationSpec
import bard.db.audit.BardContextUtils
import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextIntegrationSpec extends BardIntegrationSpec {

    AssayContext assayContext

    Session session

    @Before
    void doSetup() {
        session = sessionFactory.currentSession
        assayContext = AssayContext.buildWithoutSave()
        assert assayContext.assay.save()
    }

    void "test list order of assayContextItems persisted"() {

        given: 'an ordered list of assayContextItems'
        List<AssayContextItem> assayContextItems = [AssayContextItem.build(assayContext: assayContext, valueDisplay: 'a'),
                AssayContextItem.build(assayContext: assayContext, valueDisplay: 'b')]

        assert assayContext.save()
        def id = assayContext.getId()

        when:
        assayContext = flushClearReload(id)


        then: 'order preserved'
        assayContext.assayContextItems.size() == 2
        assayContext.assayContextItems*.valueDisplay == ['a', 'b']

    }

    private AssayContext flushClearReload(long id) {
        session.flush()
        session.clear()
        assayContext = null
        assayContext = AssayContext.get(id)
        assayContext
    }

}
