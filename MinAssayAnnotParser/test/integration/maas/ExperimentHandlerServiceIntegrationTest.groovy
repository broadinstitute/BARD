package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentHandlerServiceIntegrationTest extends GroovyTestCase {
    def experimentHandlerService

    void testLoadExperimentsContext() {
        File file = new File("test/exampleData/minAssayAnnotationSpreadsheets/BARD_Broad_Raj.xlsx")
        def inputFiles = [file]
        experimentHandlerService.loadExperimentsContext("xiaorong", inputFiles)
    }

}
