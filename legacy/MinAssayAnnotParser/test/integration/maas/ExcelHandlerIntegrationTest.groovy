package maas

import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
class ExcelHandlerIntegrationTest extends GroovyTestCase {

    void testConstructInputFileList() {
        List<File> inputFileList = []
        List<String> dirs = ["test/exampleData/minAssayAnnotationSpreadsheets"]
        ExcelHandler.constructInputFileList(dirs, inputFileList)
        assert inputFileList.size() > 1
    }

    @Ignore
    void testBuildDto() {
        File inputFile = new File("test/exampleData/minAssayAnnotationSpreadsheets/BARD_Broad_Raj.xlsx.done")
        def contextGroups = ContextGroupsBuilder.buildExperimentContextGroup()
        def dtos = ExcelHandler.buildDto(inputFile, 2, contextGroups, 80)
        assert dtos.size() > 1
    }
}
