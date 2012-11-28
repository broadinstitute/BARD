package bard.db.registration

import bard.db.experiment.Experiment
import grails.plugin.spock.IntegrationSpec
import registration.AssayService
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
    def fixtureLoader

    void "test findByPubChemAid with fixtures #label"() {

        given:
        def fixture = fixtureLoader.build {
            assay1(Assay, assayName: 'assay1')
            for (int i in 1..2) {
                String experimentsAlias = "experiment${i}"
                "${experimentsAlias}"(Experiment, experimentName: "${experimentsAlias}", assay: assay1)
                "extRef${i}"(ExternalReference, extAssayRef: "aid=-${i}", experiment: ref("${experimentsAlias}"))
            }

            assay2(Assay, assayName: 'assay2')
            experiment3(Experiment, experimentName: 'experiment3', assay: assay2)
            extRef3(ExternalReference, extAssayRef: 'aid=-1', experiment: experiment3)
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