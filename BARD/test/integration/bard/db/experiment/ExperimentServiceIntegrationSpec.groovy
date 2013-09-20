package bard.db.experiment

import bard.db.audit.BardContextUtils
import bard.db.registration.Assay
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExperimentServiceIntegrationSpec extends IntegrationSpec {

    ExperimentService experimentService
    SessionFactory sessionFactory

    @Before
    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }

    void "test splitExperimentsFromAssay"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName3')
        final Experiment experiment = Experiment.build(experimentName: "experimentsAlias", assay: assay).save(flush: true)

        when:
        final Assay newAssay = experimentService.splitExperimentsFromAssay(assay.id, [experiment])
        then:
        assert !assay.experiments
        assert newAssay.experiments
        assert newAssay.experiments.first().id == experiment.id
    }


//    void 'test x'() {
//        when:
//
//        experimentService.loadNCGCExperimentIds()
//
//        then:
//        assert 1==1
//    }


}