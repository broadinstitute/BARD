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
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids_dataset_2.csv')
def dirs = ['data/maas/maasDataset2']
experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
