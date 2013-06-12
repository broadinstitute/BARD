package bard.db

import bard.db.audit.BardContextUtils
import bard.db.dictionary.BardDescriptor
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 6/11/13
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
class ReadyForExtractFlushListenerIntegrationSpec extends IntegrationSpec  {
    SessionFactory sessionFactory

    void "test ready flag gets set"() {
        // all of the various paths to find owners are tested in unit tests.  This is just to test that the spring config
        // and database update is working by doing a single example.
        setup:
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        Experiment experiment = Experiment.build(readyForExtraction: ReadyForExtraction.NOT_READY)
        ExperimentContext context = ExperimentContext.build(experiment:experiment)
        experiment.disableUpdateReadyForExtraction = true

        when:
        sessionFactory.currentSession.flush()

        then:
        experiment.readyForExtraction == ReadyForExtraction.NOT_READY

        when:
        experiment.disableUpdateReadyForExtraction = false
        ExperimentContextItem item = ExperimentContextItem.build(experimentContext: context)
        sessionFactory.currentSession.flush()

        then:
        experiment.readyForExtraction == ReadyForExtraction.READY
    }
}
