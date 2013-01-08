import bard.db.registration.AttributeType
import bard.dm.minimumassayannotation.ContextGroup
import bard.dm.minimumassayannotation.AssayContextGroupsBuilder
import bard.dm.minimumassayannotation.AttributeNameMappingBuilder
import bard.dm.minimumassayannotation.ParseAndBuildAttributeGroups
import bard.dm.minimumassayannotation.AttributesContentsCleaner
import bard.dm.minimumassayannotation.validateCreatePersist.AttributeContentAgainstElementTableValidator
import bard.dm.minimumassayannotation.validateCreatePersist.AssayContextsValidatorCreatorAndPersistor
import bard.dm.minimumassayannotation.validateCreatePersist.MeasureContextsValidatorCreatorAndPersistor
import bard.dm.Log
import org.apache.log4j.Level
import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import bard.dm.minimumassayannotation.CouldNotReadExcelFileException
import bard.dm.minimumassayannotation.ContextItemDto
import bard.dm.minimumassayannotation.AssayDto
import bard.dm.minimumassayannotation.AssayLoadResultsWriter

Log.initializeLogger("test/exampleData/logsAndOutput/dlahr_test_2013-01-05.log")
final Date startDate = new Date()
Log.logger.info("Start load of minimum assay annotation spreadsheets ${startDate}")

final String modifiedBy = "dlahr_test_2013-01-05"
Log.logger.setLevel(Level.INFO)

final Integer START_ROW = 3 //1-based

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".xlsx")
    }
}

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

println("build mapping of columns to attributes and values")
List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()
List<ContextItemDto> resultType = [new ContextItemDto('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', attributes: resultType)]

final String contextLoadResultFilePath = "test/exampleData/logsAndOutput/minAssayAnnotParseResults.csv"
final ContextLoadResultsWriter loadResultsWriter =
    new ContextLoadResultsWriter(contextLoadResultFilePath)
AssayLoadResultsWriter assayLoadResultsWriter =
    new AssayLoadResultsWriter("test/exampleData/logsAndOutput/fileAndAssayStatsForMinAssayAnnotParse.csv")

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
        List<AssayDto> assayDtoList = parseAndBuildAttributeGroups.build(inputFile, START_ROW,
                [spreadsheetAssayContextGroups, spreadsheetResultTypeContextGroups])

        println("Clean loaded attributes")
        attributesContentsCleaner.clean(assayDtoList, attributeNameMapping)

        println("validate loaded attributes")

        for (AssayDto assayDto : assayDtoList) {
            if (assayDto.aid) {
                if (attributeContentAgainstElementTableValidator.validate(assayDto.assayContextDTOList, attributeNameMapping) &&
                        attributeContentAgainstElementTableValidator.validate(assayDto.measureContextDTOList, attributeNameMapping)) {

                    println("create, check for duplicates, and persist domain objects")
                    if (assayContextsValidatorCreatorAndPersistor.createAndPersist(assayDto.assayContextDTOList)) {
                        if (measureContextsValidatorCreatorAndPersistor.createAndPersist(assayDto.measureContextDTOList)) {
                            assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.success, null)
                        } else {
                            assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.assayContextSuccessOnly,
                                    "assay contexts loaded but failed to load measure contexts - for details see $contextLoadResultFilePath")
                        }
                    } else {
                        assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.fail,
                                "failed to load assay contexts (and did not try measure contexts  - for details see $contextLoadResultFilePath")
                    }
                } else {
                    assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.fail,
                            "aid had invalid attributes in assay contexts or measure contexts")
                }
            } else {
                assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.fail,
                        "aid found in cell not a number")
            }
        }
    } catch (CouldNotReadExcelFileException e) {
        final String message = "could not read excel file: ${inputFile.absolutePath}"
        Log.logger.error(message)
        loadResultsWriter.write(null, null, null, ContextLoadResultsWriter.LoadResultType.fail, message)
    }
}

loadResultsWriter.close()
assayLoadResultsWriter.close()

final Date endDate = new Date()
final double durationMin = (endDate.time - startDate.time) / 60000.0
Log.logger.info("finished at ${endDate}   duration[min]: ${durationMin}")
Log.close()

return false