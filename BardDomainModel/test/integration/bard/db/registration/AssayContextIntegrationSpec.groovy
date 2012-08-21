package bard.db.registration

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
class AssayContextIntegrationSpec extends IntegrationSpec {

    AssayContext assayContext
    SessionFactory sessionFactory
    Session session

    @Before
    void doSetup() {
        assayContext = AssayContext.buildWithoutSave()
        assert assayContext.assay.save()
        session = sessionFactory.currentSession
    }

    void "test list order of assayContextItems persisted"() {

        given: 'an ordered list of assayContextItems'
        List<AssayContextItem> assayContextItems = [AssayContextItem.build(valueDisplay: 'a'), AssayContextItem.build(valueDisplay: 'b')]
        assayContext.assayContextItems = assayContextItems
        assert assayContext.save()
        def id = assayContext.getId()

        when:
        assayContext = flushClearReload(id)


        then: 'order preserved'
        assayContext.assayContextItems.size() == 2
        assayContext.assayContextItems*.valueDisplay == ['a', 'b']

        when: ' list order changed'
        assayContext.assayContextItems.reverse(true)
        assayContext = flushClearReload(id)

        then: ' the new order is preserved'
        assayContext.assayContextItems*.valueDisplay == ['b', 'a']


    }

    private AssayContext flushClearReload(long id) {
        session.flush()
        session.clear()
        assayContext = null
        assayContext = AssayContext.get(id)
        assayContext
    }

}
