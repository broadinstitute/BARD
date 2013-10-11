package bard.db.registration

import bard.db.BardIntegrationSpec
import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import org.hibernate.Session
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
    Element textAttribute

    Session session

    @Before
    void doSetup() {
        session = sessionFactory.currentSession
        textAttribute = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)

        Assay assay = Assay.build().save(flush: true)
        assayContext =AssayContext.buildWithoutSave(assay: assay)
        assert assayContext.assay.save()
    }

    void "test list order of assayContextItems persisted"() {

        given: 'an ordered list of assayContextItems'
        List<AssayContextItem> assayContextItems = [AssayContextItem.build(assayContext: assayContext, attributeElement: textAttribute, valueDisplay: 'a'),
                AssayContextItem.build(assayContext: assayContext, attributeElement: textAttribute, valueDisplay: 'b')]

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
