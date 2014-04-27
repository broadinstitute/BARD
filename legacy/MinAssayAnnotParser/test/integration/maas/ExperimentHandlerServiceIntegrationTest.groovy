package maas

import bard.db.experiment.Experiment
import org.springframework.transaction.support.DefaultTransactionStatus
import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentHandlerServiceIntegrationTest extends GroovyTestCase {
    def experimentHandlerService
    def projectExperimentStageHandlerService

    @Ignore
    void testLoadExperimentsContext() {
        File file = new File("data/maas/maasDataset1")
        def inputFiles = [file]
        experimentHandlerService.loadExperimentsContext("xiaorong", inputFiles)
    }

    @Ignore
    void testLoadProjectsContextFrom(){
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
        def dirs = ['test/exampleData/maas/what_we_should_load']
        experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    void testLoad() {
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids.csv')
        def dirs = ['data/maas/maasDataset2']
            experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    void testStage() {
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids.csv')
        def dirs = ['data/maas/maasDataset2']
        projectExperimentStageHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }
}
