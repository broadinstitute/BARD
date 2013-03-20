package maas

import bard.dm.Log
import org.apache.log4j.Level
import bard.dm.minimumassayannotation.ContextGroup
import bard.dm.minimumassayannotation.AssayContextGroupsBuilder
import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import bard.dm.minimumassayannotation.AssayLoadResultsWriter
import bard.dm.minimumassayannotation.ParseAndBuildAttributeGroups
import bard.dm.minimumassayannotation.AttributesContentsCleaner
import bard.dm.minimumassayannotation.validateCreatePersist.AttributeContentAgainstElementTableValidator
import bard.dm.minimumassayannotation.validateCreatePersist.AssayContextsValidatorCreatorAndPersistor
import bard.dm.minimumassayannotation.AssayDto
import bard.dm.minimumassayannotation.CouldNotReadExcelFileException

/**
 * TODO: it is a service refactored from the old implementation of assay annotation.
 * TODO: more refactor need to be done to use same framework as project and experiment annotation loader
 */
class AssayHandlerService {

    def load(List<String> inputDirs, String outputDir, String runBy, List<Long> mustLoadedAids) {

        final String baseModifiedBy = runBy
        final String baseOutputPath = outputDir
        Log.initializeLogger("${baseOutputPath}${baseModifiedBy}.log")
        Log.logger.setLevel(Level.INFO)

        final Date startDate = new Date()
        Log.logger.info("Start load of minimum assay annotation spreadsheets ${startDate}")

        final Integer START_ROW = 2 //0-based
        final int MAX_ROW = 1000 // max number of rows we would like to process

        List<File> inputFileList = []

        def inputDirPathArray = inputDirs
        ExcelHandler.constructInputFileList(inputDirPathArray, inputFileList)

        Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

        List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()

        final String contextLoadResultFilePath = "${baseOutputPath}minAssayAnnotParseResults.csv"
        final ContextLoadResultsWriter loadResultsWriter = new ContextLoadResultsWriter(contextLoadResultFilePath)
        final AssayLoadResultsWriter assayLoadResultsWriter = new AssayLoadResultsWriter("${baseOutputPath}fileAndAssayStatsForMinAssayAnnotParse.csv")

        final ParseAndBuildAttributeGroups parseAndBuildAttributeGroups = new ParseAndBuildAttributeGroups(loadResultsWriter, MAX_ROW)
        final Map attributeNameMapping = AttributeNameMapping.build()
        final AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner(attributeNameMapping)
        final AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator = new AttributeContentAgainstElementTableValidator(loadResultsWriter)
        final AssayContextsValidatorCreatorAndPersistor assayContextsValidatorCreatorAndPersistor = new AssayContextsValidatorCreatorAndPersistor(baseModifiedBy, loadResultsWriter, false)


        try {
            for (File inputFile : inputFileList) {
                Log.logger.info("${new Date()} processing file ${inputFile.absolutePath} ")

                try {
                    List<AssayDto> assayDtoList = parseAndBuildAttributeGroups.build(inputFile, START_ROW, [spreadsheetAssayContextGroups])
                     attributesContentsCleaner.clean(assayDtoList)

                    for (AssayDto assayDto : assayDtoList) {
                        if (assayDto.aid && mustLoadedAids.contains(assayDto.aid)) {
                            attributeContentAgainstElementTableValidator.removeInvalid(assayDto.assayContextDTOList, attributeNameMapping)

                            if (assayDto.assayContextDTOList.size() > 0) {
                                String currentModifiedBy = "${baseModifiedBy}_${inputFile.name}"
                                if (currentModifiedBy.length() >= 40){
                                    currentModifiedBy = currentModifiedBy.substring(0,40)
                                }
                                assayContextsValidatorCreatorAndPersistor.modifiedBy = currentModifiedBy
                                if (assayContextsValidatorCreatorAndPersistor.createAndPersist(assayDto.assayContextDTOList)) {
                                    assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.success,
                                            "successfully loaded assay context")
                                } else {
                                    assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.fail,
                                            "failed to load assay contexts loaded assay context")
                                }
                            } else {
                                assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.nothingToLoad, null)
                            }
                        } else {
                            assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.fail,
                                    "aid found in cell not a number or not in must load list")
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
        }


    }
}
