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
import org.apache.log4j.Appender
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Layout
import org.apache.log4j.PatternLayout
import bard.dm.minimumassayannotation.LoadResultsWriter
import bard.db.registration.AssayContext
import bard.dm.minimumassayannotation.CouldNotReadExcelFileException

Log.initializeLogger("test/exampleData/dlahr_test_2013-01-05.log")
final Date startDate = new Date()
Log.logger.info("Start load of minimum assay annotation spreadsheets ${startDate}")

final String modifiedBy = "dlahr_test_2013-01-05"
Log.logger.setLevel(Level.INFO)
//org.apache.log4j.Appender fileAppender = new DailyRollingFileAppender(new PatternLayout('%m%n'), "logs/ParseSpreadsheetLogger.log", "'.'yyyy-MM-dd")
//Log.logger.addAppender(fileAppender)
final Integer START_ROW = 3 //1-based

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".xlsx")
    }
}
//List<String> inputDirPathArray = ["test/exampleData/minAssayAnnotationSpreadsheets/", "test/exampleData/dnarepairmindataspreadsheets/"]
List<String> inputDirPathArray = ["C:\\Local\\i_drive\\projects\\bard\\dataMigration\\min assay annotation\\waiting qc",
        "test/exampleData/broadAssays/", "test/exampleData/BurnhamAssays", "test/exampleData/dnarepairmindataspreadsheets",
        "test/exampleData/minAssayAnnotationSpreadsheets"]

for (String inputDirPath : inputDirPathArray) {
    File inputDirFile = new File(inputDirPath)
    assert inputDirFile, inputDirPath
    println(inputDirPath)
    inputFileList.addAll(inputDirFile.listFiles(xlsxExtensionFilenameFilter))
}
Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

//final String inputFilePath = "test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/NCGC-DNA repair.short.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Burnham Center for Chemical Genomics-DNA repair only.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"

//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Broad+others-DNA_repair.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Burnham Center for Chemical Genomics-DNA repair only.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/NCGC-DNA repair.short.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx");


println("build mapping of columns to attributes and values")
List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()
List<Attribute> resultType = [new Attribute('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', attributes: resultType)]


final LoadResultsWriter loadResultsWriter = new LoadResultsWriter("test/exampleData/minAssayAnnotParseResults.csv")
final ParseAndBuildAttributeGroups parseAndBuildAttributeGroups = new ParseAndBuildAttributeGroups(loadResultsWriter)
final Map attributeNameMapping = (new AttributeNameMappingBuilder()).build()
final AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner()
final AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator =
    new AttributeContentAgainstElementTableValidator(loadResultsWriter)
final AssayContextsValidatorCreatorAndPersistor assayContextsValidatorCreatorAndPersistor =
    new AssayContextsValidatorCreatorAndPersistor(modifiedBy, loadResultsWriter)
final MeasureContextsValidatorCreatorAndPersistor measureContextsValidatorCreatorAndPersistor =
    new MeasureContextsValidatorCreatorAndPersistor(modifiedBy, loadResultsWriter)

for (File inputFile : inputFileList) {
    Log.logger.info("${new Date()} processing file ${inputFile.absolutePath}")

    println("Build assay and measure-context (groups) and populate their attribute from the spreadsheet cell contents.")

    try {
        List<List<ContextDTO>> contextDTOGroups = parseAndBuildAttributeGroups.build(inputFile, START_ROW,
                [spreadsheetAssayContextGroups, spreadsheetResultTypeContextGroups])

        List<ContextDTO> assayContextList = contextDTOGroups[0]
        println("assayContextList size: ${assayContextList.size()}")
        List<ContextDTO> measureContextList = contextDTOGroups[1]
        println("measureContextList size: ${measureContextList.size()}")

        println("Clean loaded attributes")
        List<ContextDTO> assayContextListCleaned = attributesContentsCleaner.clean(assayContextList, attributeNameMapping)
        List<ContextDTO> measureContextListCleaned = attributesContentsCleaner.clean(measureContextList, attributeNameMapping)
        println("cleaned sizes ${assayContextListCleaned.size()} ${measureContextListCleaned.size()}")

        println("validate loaded attributes")

        if (attributeContentAgainstElementTableValidator.validate(assayContextListCleaned, attributeNameMapping) &&
                attributeContentAgainstElementTableValidator.validate(measureContextListCleaned, attributeNameMapping)) {

            println("create, check for duplicates, and persist domain objects")
            if (assayContextsValidatorCreatorAndPersistor.createAndPersist(assayContextListCleaned)) {
                measureContextsValidatorCreatorAndPersistor.createAndPersist(measureContextListCleaned)
            }
        }
    } catch (CouldNotReadExcelFileException e) {
        final String message = "could not read excel file: ${inputFile.absolutePath}"
        Log.logger.error(message)
        loadResultsWriter.write(null, null, null, LoadResultsWriter.LoadResultType.fail, message)
    }
}
loadResultsWriter.close()

final Date endDate = new Date()
final double durationMin = (endDate.time - startDate.time) / 60000.0
Log.logger.info("finished at ${endDate}   duration[min]: ${durationMin}")
Log.close()

return false