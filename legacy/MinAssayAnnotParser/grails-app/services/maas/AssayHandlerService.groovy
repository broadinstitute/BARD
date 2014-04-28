/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        final int MAX_ROW = 4000 // max number of rows we would like to process

        List<File> inputFileList = []

        def inputDirPathArray = inputDirs
        ExcelHandler.constructInputFileList(inputDirPathArray, inputFileList)

        Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

        List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()

        final String contextLoadResultFilePath = "${baseOutputPath}minAssayAnnotParseResults.csv"
        final ContextLoadResultsWriter loadResultsWriter = new ContextLoadResultsWriter(contextLoadResultFilePath)
        final AssayLoadResultsWriter assayLoadResultsWriter = new AssayLoadResultsWriter("${baseOutputPath}fileAndAssayStatsForMinAssayAnnotParse.csv")

        final ParseAndBuildAttributeGroups parseAndBuildAttributeGroups = new ParseAndBuildAttributeGroups(loadResultsWriter, MAX_ROW)
        final Map attributeNameMapping = ElementIdMapping.build()
        final AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner(attributeNameMapping)
        final AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator = new AttributeContentAgainstElementTableValidator(loadResultsWriter)
        final AssayContextsValidatorCreatorAndPersistor assayContextsValidatorCreatorAndPersistor = new AssayContextsValidatorCreatorAndPersistor(baseModifiedBy, loadResultsWriter, false)


        try {
            for (File inputFile : inputFileList) {
                Log.logger.info("${new Date()} processing file ${inputFile.absolutePath} ")
                String currentModifiedBy = "${baseModifiedBy}_${inputFile.name}"
                if (currentModifiedBy.length() >= 40){
                    currentModifiedBy = currentModifiedBy.substring(0,40)
                }

                try {
                    List<AssayDto> assayDtoList = parseAndBuildAttributeGroups.build(inputFile, START_ROW, [spreadsheetAssayContextGroups])
                     attributesContentsCleaner.clean(assayDtoList)

                    for (AssayDto assayDto : assayDtoList) {
                        if (assayDto.aid) {
                            if (mustLoadedAids.contains(assayDto.aid)) {
                                Log.logger.info("Processing: aid: ${assayDto.aid}, line #: ${assayDto.rowNum} in file ${inputFile.absolutePath}")
                                attributeContentAgainstElementTableValidator.removeInvalid(assayDto.assayContextDTOList, attributeNameMapping)

                                if (assayDto.assayContextDTOList.size() > 0) {
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
                                assayLoadResultsWriter.write(assayDto, AssayLoadResultsWriter.LoadResultType.nothingToLoad,
                                        "aid found in cell not in must load list")
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
                } catch (Exception e) {
                    e.printStackTrace()
                    Log.logger.error("Something wrong of this file : ${inputFile.absolutePath} ${e.message}")
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
