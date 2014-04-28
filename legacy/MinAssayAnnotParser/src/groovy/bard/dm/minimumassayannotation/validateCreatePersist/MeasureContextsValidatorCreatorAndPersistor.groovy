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

package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Measure
import bard.db.dictionary.Element
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.Log
import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import bard.dm.minimumassayannotation.ContextLoadResultsWriter.LoadResultType

/**
 * Creates and persists a Measure object from the group of attributes we created earlier.
 * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
 * 2. Assay is taken from the context's aid
 */
class MeasureContextsValidatorCreatorAndPersistor extends ValidatorCreatorAndPersistor {

    MeasureContextsValidatorCreatorAndPersistor(String modifiedBy, ContextLoadResultsWriter loadResultsWriter,
                                                boolean flushSetting) {
        super(modifiedBy, loadResultsWriter, flushSetting)
    }

    /**
     * Creates and persists a Measure object from the group of attributes we created earlier.
     * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
     * 2. Assay is taken from the context's aid
     *
     * @param measureContextListCleaned
     */
    boolean createAndPersist(List<ContextDTO> measureContextList) {

        Measure.withTransaction { status ->
            for (ContextDTO measureContextDTO : measureContextList) {

                //create the assay-context
                Measure measureContext = new Measure()
                measureContext.assay = super.getAssayFromAid(measureContextDTO.aid)
                measureContext.modifiedBy = super.modifiedBy
                //TODO DELETE DELETE DELETE the following line should be deleted once all assays have been uploaded to CAP
                if (!measureContext.assay) {//skip this assay context
                    super.writeMessageWhenAidNotFoundInDb(measureContextDTO)
                    status.setRollbackOnly()
                    return false
                }
                measureContext.assay.measures.add(measureContext)

                if (measureContextDTO.contextItemDtoList.size() == 1) {
                    super.loadResultsWriter.write(measureContextDTO, measureContext.assay.id,
                            LoadResultType.fail, null, 0, "more than 1 attribute found in measure context should only be 1")
                    status.setRollbackOnly()
                    return false
                }

                Element element = Element.findByLabelIlike(measureContextDTO.contextItemDtoList.first().value) //The value is the result-type
                if (!element) {
                    final String message = "We must have an element for the measure-context-item attribute / result type: '${measureContextDTO.contextItemDtoList.first().value}'"
                    super.loadResultsWriter.write(measureContextDTO, measureContext.assay.id, ContextLoadResultsWriter.LoadResultType.fail,
                            null, 0, message)
                    status.setRollbackOnly()
                    return false
                }
                measureContext.resultType = element

                //Validate that the assay_id/result_type_id (element) combination is not already in the DB
                def existingMeasures = Measure.findAll("from Measure as m \
                                where m.assay = :assay and m.resultType = :resultType",
                        [assay: measureContext.assay, resultType: measureContext.resultType])
                if (existingMeasures && existingMeasures.size() > 0) {
                    super.loadResultsWriter.write(measureContextDTO, measureContext.assay.id, LoadResultType.alreadyLoaded,
                            existingMeasures.get(0).assayContextMeasures.size(), 0, null)
                    status.setRollbackOnly()
                    return false
                }

                if (null == measureContext.assay.id) {
                    Log.logger.error("measureContext assay has null id $measureContextDTO.aid")
                }
                measureContext.save(flush: super.flushSetting)


                if (measureContext.hasErrors()) {
                    final String message = "MeasureContext Errors: ${measureContext.errors}"
                    Log.logger.info(message)
                    status.setRollbackOnly()
                    super.loadResultsWriter.write(measureContextDTO, measureContext.assay.id, LoadResultType.fail,
                            null, 0, message)
                    return false
                } else {
                    super.loadResultsWriter.write(measureContextDTO, measureContext.assay.id, LoadResultType.success,
                            null, measureContextDTO.contextItemDtoList.size(), null)
                }
            }

//            status.setRollbackOnly()
        }

        return true
    }
}
