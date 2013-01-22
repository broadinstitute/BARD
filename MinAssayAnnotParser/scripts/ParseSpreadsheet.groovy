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
import bard.db.dictionary.Element


Log.initializeLogger("test/exampleData/logsAndOutput/dlahr_test_2013-01-21.log")
final Date startDate = new Date()
Log.logger.info("Start load of minimum assay annotation spreadsheets ${startDate}")

final String baseModifiedBy = "dlahr_test_2013-01-21"
Log.logger.setLevel(Level.INFO)




final ElementAdder elementAdder = new ElementAdder(baseModifiedBy)
elementAdder.add(["percent parasitemia":"percent content or parasites in the blood",
        "hematocrit":"volume percentage of red blood cells. aka packed cell volume or erythrocyte volume fraction",
        "PerkinElmer AlphaScreen reagent":null,
        "green coffee bean":"unroasted coffee bean",
        "ABI Prism 7900HT":"sequence detection system instrument designed for automated, high-throughput detection of fluorescent PCR-related chemistries",
        "Tecan Spectra Mini Reader":null,
        "PerkinElmer AlphaScreen SureFire ERK reagent":null,
        "Bacillus anthracis":null,
        "Bacillus subtilis":null,
        "Biorad ChemiDoc Plus Imager":null,
        "Giardia lamblia":null,
        "Cercopithecus aethiops":null,
        "Microcal VP-ITC":null,
        "Packard Cobra Gamma Counter":null,
        "Methanococcus maripaludis":null,
        "Accuri C6 flow cytometer":null,
        "Marburg marburgvirus":null,
        "Lassa virus":null,
        "Avian infectious bronchitis virus":null,
        "Cricetulus griseus":"Chinese Hamster",
        "Tecan Freedom Evo 150":null,
        "Streptococcus pyogenes":"spherical gram-positive bacteria",
        "BMG Pherastar":null
])

final Integer START_ROW = 3 //1-based

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        final String lower = name.toLowerCase()
        return lower.endsWith(".xlsx") || lower.endsWith(".xls")
    }
}

List<String> inputDirPathArray = //["test/exampleData/temp"]
    ["C:\\Local\\i_drive\\projects\\bard\\dataMigration\\min assay annotation\\waiting qc"]//,
//            "test/exampleData/broadAssays/", "test/exampleData/BurnhamAssays", "test/exampleData/dnarepairmindataspreadsheets",
//            "test/exampleData/minAssayAnnotationSpreadsheets"]

for (String inputDirPath : inputDirPathArray) {
    File inputDirFile = new File(inputDirPath)
    assert inputDirFile, inputDirPath

    List<File> fileList = inputDirFile.listFiles(xlsxExtensionFilenameFilter)
    inputFileList.addAll(fileList)

    Log.logger.info("loading files from directory: ${inputDirPath} contains ${fileList.size()}")
}

Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

println("build mapping of columns to attributes and values")
List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()
List<ContextItemDto> resultType = [new ContextItemDto('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', contextItemDtoList: resultType)]

final String contextLoadResultFilePath = "test/exampleData/logsAndOutput/minAssayAnnotParseResults.csv"
final ContextLoadResultsWriter loadResultsWriter =
    new ContextLoadResultsWriter(contextLoadResultFilePath)
AssayLoadResultsWriter assayLoadResultsWriter =
    new AssayLoadResultsWriter("test/exampleData/logsAndOutput/fileAndAssayStatsForMinAssayAnnotParse.csv")

final ParseAndBuildAttributeGroups parseAndBuildAttributeGroups = new ParseAndBuildAttributeGroups(loadResultsWriter)
final Map attributeNameMapping = (new AttributeNameMappingBuilder()).build()
final AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner(attributeNameMapping)
final AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator =
    new AttributeContentAgainstElementTableValidator(loadResultsWriter)
final AssayContextsValidatorCreatorAndPersistor assayContextsValidatorCreatorAndPersistor =
    new AssayContextsValidatorCreatorAndPersistor(baseModifiedBy, loadResultsWriter, false)
final MeasureContextsValidatorCreatorAndPersistor measureContextsValidatorCreatorAndPersistor =
    new MeasureContextsValidatorCreatorAndPersistor(baseModifiedBy, loadResultsWriter, false)

Map<String, Integer> filePathHashCodeMap = new HashMap<String, Integer>()
try {
    for (File inputFile : inputFileList) {
        Log.logger.info("${new Date()} processing file ${inputFile.absolutePath}")
        final int fileHash = inputFile.absolutePath.hashCode()
        filePathHashCodeMap.put(inputFile.absolutePath, fileHash)
        Log.logger.info("file path hashcode $fileHash")

        println("Build assay and measure-context (groups) and populate their attribute from the spreadsheet cell contents.")

        try {
            List<AssayDto> assayDtoList = parseAndBuildAttributeGroups.build(inputFile, START_ROW,
                    [spreadsheetAssayContextGroups, spreadsheetResultTypeContextGroups])

            println("Clean loaded attributes")
            attributesContentsCleaner.clean(assayDtoList)

            println("validate loaded attributes")

            for (AssayDto assayDto : assayDtoList) {
                if (assayDto.aid) {
                    //want both of these to run so we determine all the dictionary terms that need to be either corrected
                    //in the spreadsheet, aliased, or mapped
                    final boolean areAssayContextsValid = attributeContentAgainstElementTableValidator.validate(assayDto.assayContextDTOList, attributeNameMapping)
                    final boolean areMeasureContextsValid = attributeContentAgainstElementTableValidator.validate(assayDto.measureContextDTOList, attributeNameMapping)

                    if (areAssayContextsValid && areMeasureContextsValid) {
                        final String currentModifiedBy = "${baseModifiedBy}_FH$fileHash"
                        assayContextsValidatorCreatorAndPersistor.modifiedBy = currentModifiedBy
                        if (assayContextsValidatorCreatorAndPersistor.createAndPersist(assayDto.assayContextDTOList)) {
                            measureContextsValidatorCreatorAndPersistor.modifiedBy = currentModifiedBy
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
            final String message = "could not read excel file: ${inputFile.absolutePath} ${e.message}"
            Log.logger.error(message)
            loadResultsWriter.write(null, null, ContextLoadResultsWriter.LoadResultType.fail, null, 0, message)
        }
    }
} catch (Exception e) {
    e.printStackTrace()
    Log.logger.error(e.message)
} finally {
    loadResultsWriter.close()
    assayLoadResultsWriter.close()

    final Date endDate = new Date()
    final double durationMin = (endDate.time - startDate.time) / 60000.0
    Log.logger.info("finished at ${endDate}   duration[min]: ${durationMin}")
    Log.close()

    println("filepath hashcodes that were appended onto modified by statements")
    for (File inputFile : inputFileList) {
        final int filePathHash = filePathHashCodeMap.get(inputFile.absolutePath)
        println("$filePathHash inputFile.absolutePath")
    }
}

return


class ElementAdder {
    private final modifiedBy

    public ElementAdder(String modifiedBy) {
        this.modifiedBy = modifiedBy
    }

    void add(Map<String, String> elementLabelDescriptionMap) {
        Element.withTransaction {status ->
            for (String elementLabel : elementLabelDescriptionMap.keySet()) {
                Element element = Element.findByLabelIlike(elementLabel)

                if (! element) {
                    String elementDescription = elementLabelDescriptionMap.get(elementLabel)
                    element = new Element(label: elementLabel, modifiedBy: modifiedBy, description: elementDescription)
                    element.save()
                }
            }
        }
    }
}
