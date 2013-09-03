package bard.db.registration

import bard.db.BardIntegrationSpec
import spock.lang.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Ignore
class MeasureIntegrationSpec extends BardIntegrationSpec {

//    def domainInstance
//
//    @Before
//    void doSetup() {
//        domainInstance = buildWithoutSaveMeasure()
//    }
//
//    void "test childMeasures cascade delete "() {
//
//        given: 'a measure with child Measures'
//        3.times {
//            Measure measure = buildWithoutSaveMeasure()
//            measure.parentChildRelationship = HierarchyType.SUPPORTED_BY
//            domainInstance.addToChildMeasures(measure)
//        }
//
//        when: 'the parent measure is saved and the session cleared'
//        domainInstance.save()
//        def childMeasureIds = domainInstance.childMeasures*.id
//        flushAndClear()
//
//        then: 'all childMeasures are saved'
//        for (id in childMeasureIds) {
//            assert Measure.findById(id)
//        }
//
//        when: 'parent measure deleted'
//        domainInstance.refresh()
//        domainInstance.delete(flush: true)
//
//        then: 'all childMeasures are cascade deleted'
//        notThrown(DataIntegrityViolationException)
//        for (id in childMeasureIds) {
//            assert Measure.findById(id) == null
//        }
//    }
//
//    public void flushAndClear() {
//        Measure.withSession { session ->
//            session.flush()
//            session.clear()
//        }
//    }
//
//    private Measure buildWithoutSaveMeasure() {
//        Measure.buildWithoutSave(assay: Assay.build(), resultType: Element.build())
//    }
}
