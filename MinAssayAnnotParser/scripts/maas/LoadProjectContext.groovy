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
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
def dirs = ['test/exampleData/maas/what_we_should_load']
Project.withTransaction{DefaultTransactionStatus status ->
    projectHandlerService.handle('xiaorong', dirs, mustLoadAids)
}
