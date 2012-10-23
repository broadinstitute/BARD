package bard.db.registration

import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before
import registration.AssayService
import spock.lang.Shared
import bard.db.experiment.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayServiceIntegrationSpec extends IntegrationSpec {

    AssayService assayService
    @Shared Assay assay1
    @Shared Assay assay2

    @Before
    void doSetup() {
        assay1 = Assay.buildWithoutSave() //An assay with two experiments, each link to a different external reference (e.g., PubChem AID)
        Experiment experiment1 = new Experiment(externalReferences: [new ExternalReference(extAssayRef: '1')], experimentName: 'experiment1', experimentStatus: 'Pending') //first experiment with a reference to aid=1
        Experiment experiment2 = new Experiment(externalReferences: [new ExternalReference(extAssayRef: '2')], experimentName: 'experiment2', experimentStatus: 'Pending')//second experiment with a reference to aid=2
        Experiment experiment3 = new Experiment(externalReferences: [new ExternalReference(extAssayRef: '3')], experimentName: 'experiment3', experimentStatus: 'Pending')//third experiment with a reference to aid=2
        assay1.addToExperiments(experiment1)
        assay1.addToExperiments(experiment2)
        assay1.addToExperiments(experiment3)
        assay1.validate()
        assert assay1.save()
        assay2 = Assay.buildWithoutSave() //As assay with two experiments, both link to the same external reference
        assay2.addToExperiments(experiment1)
        assay2.addToExperiments(experiment2)
        assert assay2.save()
    }

    void "test findByPubChemAid #label"() {

        given:

        when:
        Assay foundAssay = assayService.findByPubChemAid(aid)


        then: 'order preserved'
        assert foundAssay?.id == expectedAssayId

        where:
        label                    | aid       | expectedAssayId
        'find an exiting aid'    | 1         | 1
        'find a non-exiting aid' | 123456789 | null
    }
}
