package maas

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
        File file = new File("test/exampleData/minAssayAnnotationSpreadsheets/BARD_Broad_Raj.xlsx")
        def inputFiles = [file]
        projectHandlerService.loadProjectsContext("xiaorong", inputFiles)
    }

}
