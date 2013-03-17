import maas.ProjectExperimentStageHandlerService
import maas.MustLoadAid
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/15/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
ProjectExperimentStageHandlerService handlerService = new ProjectExperimentStageHandlerService()
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset1/aids_dataset_1.csv')
def dirs = ['data/maas/maasDataset1']

handlerService.handle('xiaorong', dirs, mustLoadAids)
