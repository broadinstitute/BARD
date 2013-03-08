package maas

import bard.db.project.Project

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectHandlerServiceIntegrationTest extends GroovyTestCase {
    def projectHandlerService

    void testLoadProjectsContext() {
        final List<String> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
        File file = new File("test/exampleData/minAssayAnnotationSpreadsheets/BARD_Broad_Raj.xlsx")
        def inputFiles = [file]
        projectHandlerService.loadProjectsContext("xiaorong", inputFiles, mustLoadAids)
    }

    void testLoadProjectsContextFrom(){
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
        def dirs = ['test/exampleData/maas/what_we_should_load']
        projectHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    void testDeleteContext() {
        Project project = Project.findById(87)
        projectHandlerService.deleteExistingContext(project)
    }
}
