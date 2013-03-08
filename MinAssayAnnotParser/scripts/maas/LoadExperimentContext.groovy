import maas.MustLoadAid
import org.springframework.transaction.support.DefaultTransactionStatus
import maas.ExperimentHandlerService
import bard.db.experiment.Experiment
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/8/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */

ExperimentHandlerService experimentHandlerService = new ExperimentHandlerService()
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
def dirs = ['test/exampleData/maas/what_we_should_load']
Experiment.withTransaction{DefaultTransactionStatus status ->
    experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
}