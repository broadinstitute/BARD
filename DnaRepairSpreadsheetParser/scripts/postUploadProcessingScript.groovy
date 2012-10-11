import bard.db.registration.AttributeType
import bard.dm.minimumassayannotation.Attribute
import bard.dm.minimumassayannotation.ContextGroup
import bard.dm.minimumassayannotation.AssayContextGroupsBuilder
import bard.dm.minimumassayannotation.AttributeNameMappingBuilder
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ParseAndBuildAttributeGroups
import bard.dm.minimumassayannotation.AttributesContentsCleaner
import bard.dm.minimumassayannotation.validateCreatePersist.AttributeContentAgainstElementTableValidator
import bard.dm.minimumassayannotation.validateCreatePersist.AssayContextsValidatorCreatorAndPersistor
import bard.dm.minimumassayannotation.validateCreatePersist.MeasureContextsValidatorCreatorAndPersistor
import bard.dm.Log
import org.apache.log4j.Level
import postUploadProcessing.ContextGroupService
import bard.dm.postUploadProcessing.ContextChange

final Date startDate = new Date()
Log.logger.info("Start post-processing the spreadsheet uplaods ${startDate}")

final String modifiedBy = "jbittker"
Log.logger.setLevel(Level.INFO)
final Integer START_ROW = 3 //1-based

ContextGroupService contextGroupService = ctx.getBean("contextGroupService")

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".xlsx")
    }
}
List<String> inputDirPathArray = ["test/exampleData/postUploadProcessing/"]
for (String inputDirPath : inputDirPathArray) {
    File inputDirFile = new File(inputDirPath)
    inputFileList.addAll(inputDirFile.listFiles(xlsxExtensionFilenameFilter))
}
Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

List<ContextChange> contextChangeDTOs = []
for (File inputFile : inputFileList) {
    Log.logger.info("${new Date()} processing file ${inputFile.absolutePath}")
    contextChangeDTOs.addAll(contextGroupService.parseFile(inputFile, START_ROW))
}

List<ContextChange> contextChangeList = contextGroupService.buildContextChangeListFromDTOs(contextChangeDTOs)

for (ContextChange contextChange : contextChangeList) {
    contextChange.doChange()
}

final Date endDate = new Date()
final double durationMin = (endDate.time - startDate.time) / 60000.0
Log.logger.info("finished at ${endDate}   duration[min]: ${durationMin}")

return false
