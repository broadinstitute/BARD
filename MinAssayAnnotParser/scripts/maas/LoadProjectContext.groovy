import maas.ProjectHandlerService
import maas.MustLoadAid
import bard.db.project.Project
import org.springframework.transaction.support.DefaultTransactionStatus
/**
 *
 * Run this script to load project context
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/7/13
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */

ProjectHandlerService projectHandlerService = new ProjectHandlerService()
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset1/aids_dataset_1.csv')
def dirs = ['data/maas/maasDataset1']
Project.withTransaction{DefaultTransactionStatus status ->
    projectHandlerService.handle('xiaorong', dirs, mustLoadAids)
}
