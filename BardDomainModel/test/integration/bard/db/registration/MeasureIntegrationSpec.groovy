package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import grails.plugin.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.junit.Before
import org.springframework.dao.DataIntegrityViolationException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
class MeasureIntegrationSpec extends IntegrationSpec {

    def domainInstance

    SessionFactory sessionFactory

    @Before
    void doSetup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        domainInstance = buildWithoutSaveMeasure()
    }

    void "test childMeasures cascade delete "() {

        given: 'a measure with child Measures'
        3.times {
            domainInstance.addToChildMeasures(buildWithoutSaveMeasure())
        }

        when: 'the parent measure is saved and the session cleared'
        domainInstance.save()
        def childMeasureIds = domainInstance.childMeasures*.id
        flushAndClear()

        then: 'all childMeasures are saved'
        for (id in childMeasureIds) {
            assert Measure.findById(id)
        }

        when: 'parent measure deleted'
        domainInstance.refresh()
        domainInstance.delete(flush: true)

        then: 'all childMeasures are cascade deleted'
        notThrown(DataIntegrityViolationException)
        for (id in childMeasureIds) {
            assert Measure.findById(id) == null
        }
    }

    public void flushAndClear() {
        Measure.withSession {session ->
            session.flush()
            session.clear()
        }
    }

    private Measure buildWithoutSaveMeasure() {
        Measure.buildWithoutSave(assay: Assay.build(), resultType: Element.build())
    }
}
