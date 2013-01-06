package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Measure
import bard.db.dictionary.Element
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.Log
import bard.dm.minimumassayannotation.LoadResultsWriter
import bard.dm.minimumassayannotation.LoadResultsWriter.LoadResultType

/**
 * Creates and persists a Measure object from the group of attributes we created earlier.
 * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
 * 2. Assay is taken from the context's aid
 */
class MeasureContextsValidatorCreatorAndPersistor extends ValidatorCreatorAndPersistor {

    MeasureContextsValidatorCreatorAndPersistor(String modifiedBy, LoadResultsWriter loadResultsWriter) {
        super(modifiedBy, loadResultsWriter)
    }

    /**
     * Creates and persists a Measure object from the group of attributes we created earlier.
     * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
     * 2. Assay is taken from the context's aid
     *
     * @param measureContextListCleaned
     */
    void createAndPersist(List<ContextDTO> measureContextList) {

        Integer totalMeasureContext = 0
        measureContextList.each { ContextDTO measureContextDTO -> totalMeasureContext += measureContextDTO.attributes.size()}
        Integer tally = 0

        Measure.withTransaction { status ->
            measureContextList.each { ContextDTO measureContextDTO ->

                //create the assay-context
                Measure measureContext = new Measure()
                measureContext.assay = super.getAssayFromAid(measureContextDTO.aid)
                measureContext.modifiedBy = super.modifiedBy
                //TODO DELETE DELETE DELETE the following line should be deleted once all assays have been uploaded to CAP
                if (!measureContext.assay) {//skip this assay context
                    totalMeasureContext -= measureContextDTO.attributes.size()
                    super.writeMessageWhenAidNotFoundInDb(measureContextDTO.aid, measureContextDTO.name)
                    status.setRollbackOnly()
                    return
                }

                if (measureContextDTO.attributes.size() == 1) {
                    super.loadResultsWriter.write(measureContextDTO.aid, measureContext.assay.id, measureContextDTO.name,
                            LoadResultType.fail, "more than 1 attribute found in measure context should only be 1")
                    status.setRollbackOnly()
                    return
                }

                Element element = Element.findByLabelIlike(measureContextDTO.attributes.first().value) //The value is the result-type
                if (!element) {
                    final String message = "We must have an element for the measure-context-item attribute / result type: '${measureContextDTO.attributes.first().value}'"
                    super.loadResultsWriter.write(measureContextDTO.aid, measureContext.assay.id, measureContextDTO.name,
                            LoadResultsWriter.LoadResultType.fail, message)
                    status.setRollbackOnly()
                    return
                }
                measureContext.resultType = element
                Log.logger.info("Measure's Assay ID: ${measureContext.assay.id} (${tally++}/${totalMeasureContext})")

                //Validate that the assay_id/result_type_id (element) combination is not already in the DB
                def existingMeasures = Measure.findAll("from Measure as m \
                                where m.assay = :assay and m.resultType = :resultType",
                        [assay: measureContext.assay, resultType: measureContext.resultType])
                if (existingMeasures) {
                    super.loadResultsWriter.write(measureContextDTO.aid, measureContext.assay.id, measureContextDTO.name,
                            LoadResultType.alreadyLoaded, null)
                    status.setRollbackOnly()
                    return
                }

                measureContext.save()

                final String message
                final LoadResultType loadResultType
                if (measureContext.hasErrors()) {
                    loadResultType = LoadResultType.fail
                    message = "MeasureContext Errors: ${measureContext.errors}"
                    Log.logger.info(message)
                    status.setRollbackOnly()
                    return
                } else {
                    loadResultType = LoadResultType.success
                    message = ""
                }
                super.loadResultsWriter.write(measureContextDTO.aid, measureContext.assay.id, measureContextDTO.name, loadResultType, message)
            }
        }
    }
}
