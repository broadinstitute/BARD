package bard.db.registration

import bard.db.BardIntegrationSpec
import bard.db.experiment.Experiment
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
class AssayServiceIntegrationSpec extends BardIntegrationSpec {

    AssayService assayService

    void "test findByPubChemAid with fixtures #label"() {

        given:
        final Assay assay1 = Assay.build(assayName: 'assay1', capPermissionService:null)
        2.times { i ->
            i++
            final String experimentsAlias = "experiment${i}"
            final Experiment experiment = Experiment.build(experimentName: experimentsAlias, assay: assay1, capPermissionService:null)
            final ExternalReference externalReference = ExternalReference.build(extAssayRef: "aid=-${i}", experiment: experiment)
        }
        final Assay assay2 = Assay.build(assayName: 'assay2', capPermissionService:null)
        final Experiment experiment3 = Experiment.build(experimentName: 'experiment3', assay: assay2, capPermissionService:null)
        final ExternalReference externalReference = ExternalReference.build(extAssayRef: 'aid=-1', experiment: experiment3)

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