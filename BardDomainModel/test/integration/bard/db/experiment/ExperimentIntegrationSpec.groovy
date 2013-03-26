package bard.db.experiment

import bard.db.BardIntegrationSpec
import bard.db.registration.Assay
import org.springframework.dao.DataIntegrityViolationException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentIntegrationSpec extends BardIntegrationSpec {

    void "ensure Assay delete doesn't cascade to Experiment"() {
        given: 'a valid experiment'
        Experiment experiment = Experiment.build()

        when:
        Assay assay = experiment.assay
        assay.delete()
        Assay.withSession { it.flush(); it.clear(); }

        then:
        DataIntegrityViolationException e = thrown()
        e.message.contains('nested exception is org.hibernate.exception.ConstraintViolationException')
    }
}