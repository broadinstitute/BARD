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

String dir = "data/maas/maasDataset2/"
String aidsFile = "aids_dataset_2.csv"
final String runBy = "xx"
final List<String> inputDirs = [dir]
def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}${aidsFile}")

ProjectHandlerService projectHandlerService = new ProjectHandlerService()
Project.withTransaction{DefaultTransactionStatus status ->
    projectHandlerService.handle(runBy, inputDirs, mustLoadedAids)
}
