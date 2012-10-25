package bard.db.registration

import bard.db.experiment.Experiment
import grails.plugin.fixtures.FixtureLoader
import grails.plugin.spock.IntegrationSpec
import registration.AssayService
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayServiceIntegrationSpec extends IntegrationSpec {

    AssayService assayService
    @Shared Assay assay1
    @Shared Assay assay2
    FixtureLoader fixtureLoader

    void manualSetup() {
        assay1 = Assay.build(assayName: 'assay1')
        Experiment experiment1 = Experiment.build(assay: assay1)
        assay1.addToExperiments(experiment1)
        ExternalReference extRef1 = ExternalReference.build(extAssayRef: 'aid=-1', experiment: experiment1)
        experiment1.addToExternalReferences(extRef1)

        Experiment experiment2 = Experiment.build(assay: assay1)
        assay1.addToExperiments(experiment2)
        ExternalReference extRef2 = ExternalReference.build(extAssayRef: 'aid=-2', experiment: experiment2)
        experiment2.addToExternalReferences(extRef2)

        assay1.validate()
        assert assay1.save(flush: true)

        assay2 = Assay.build(assayName: 'assay2')
        Experiment experiment3 = Experiment.build(assay: assay2)
        assay2.addToExperiments(experiment3)
        ExternalReference extRef3 = ExternalReference.build(extAssayRef: 'aid=-1', experiment: experiment3)
        experiment3.addToExternalReferences(extRef3)
        assert assay2.save(flush: true)
    }

    void "test findByPubChemAid #label"() {

        given:
        manualSetup()

        when:
        List<Assay> foundAssays = assayService.findByPubChemAid(aid)

        then: 'order preserved'
        assert foundAssays*.assayName.sort() == expectedAssayNames

        where:
        label                                           | aid       | expectedAssayNames
        'find an ADID with two AIDs associated with it' | -2        | ['assay1']
        'find a non-exiting aid'                        | 123456789 | []
        'find an exiting aid associated with two ADIDs' | -1        | ['assay1', 'assay2']
    }

    void "test findByPubChemAid with fixtures #label"() {

        given:
        grails.buildtestdata.TestDataConfigurationHolder.reset()
        def fixture = fixtureLoader.build {
            experiment1(Experiment, experimentName: 'experiment1')
            experiment2(Experiment, experimentName: 'experiment2')
            extRef2(ExternalReference, extAssayRef: 'aid=-2', experiment: experiment1)
            extRef1(ExternalReference, extAssayRef: 'aid=-1', experiment: experiment2)
            assay1(Assay, assayName: 'assay1', experiments: [experiment1, experiment2])

            experiment3(Experiment, experimentName: 'experiment3')
            extRef3(ExternalReference, extAssayRef: 'aid=-1', experiment: experiment3)
            assay2(Assay, assayName: 'assay2', experiments: [experiment3])
        }

        when:
        List<Assay> foundAssays = assayService.findByPubChemAid(aid)

        then:
        assert foundAssays*.assayName.sort() == expectedAssayNames

        where:
        label                                           | aid       | expectedAssayNames
        'find an ADID with two AIDs associated with it' | -2        | ['assay1']
        'find a non-exiting aid'                        | 123456789 | []
        'find an exiting aid associated with two ADIDs' | -1        | ['assay1', 'assay2']
    }
}