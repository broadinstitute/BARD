package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Measure
import org.springframework.transaction.TransactionStatus
import bard.db.dictionary.Element
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.Log

/**
 * Creates and persists a Measure object from the group of attributes we created earlier.
 * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
 * 2. Assay is taken from the context's aid
 */
class MeasureContextsValidatorCreatorAndPersistor extends ValidatorCreatorAndPersistor {

    MeasureContextsValidatorCreatorAndPersistor(String modifiedBy) {
        super(modifiedBy)
    }

    /**
     * Creates and persists a Measure object from the group of attributes we created earlier.
     * 1. Element (result-type - Measure.element) is taken from the context's value (the key is always 'Result type')
     * 2. Assay is taken from the context's aid
     *
     * @param measureContextListCleaned
     */
    void createAndPersist(List<ContextDTO> measureContextList) {
        super.validate(measureContextList)

        def out = new File('DnaSpreadsheetParserResultMeasure' + '_' + System.currentTimeMillis() + '.txt')
        out.withWriterAppend { writer ->
            Integer totalMeasureContext = 0
            measureContextList.each { ContextDTO measureContextDTO -> totalMeasureContext += measureContextDTO.attributes.size()}
            Integer tally = 0

            measureContextList.each { ContextDTO measureContextDTO ->
                Measure.withTransaction { TransactionStatus status ->
                    //create the assay-context
                    Measure measureContext = new Measure()
                    measureContext.assay = super.getAssayFromAid(measureContextDTO.aid)
                    measureContext.modifiedBy = super.modifiedBy
                    //TODO DELETE DELETE DELETE the following line should be deleted once all assays have been uploaded to CAP
                    if (!measureContext.assay) {//skip this assay context
                        totalMeasureContext -= measureContextDTO.attributes.size()
                        return
                    }

                    assert measureContextDTO.attributes.size() == 1, "There could be only one attribure for a measure-context"
                    //                Log.logger.info("Measure attribute: key='${measureContextDTO.attributes.first().key}'; value='${measureContextDTO.attributes.first().value}'")
                    Element element = Element.findByLabelIlike(measureContextDTO.attributes.first().value) //The value is the result-type
                    assert element, "We must have an element for the measure-context-item attribute"
                    measureContext.element = element
                    Log.logger.info("Measure's Assay ID: ${measureContext.assay.id} (${tally++}/${totalMeasureContext})")

                    //Validate that the assay_id/result_type_id (element) combination is not already in the DB
                    def existingMeasures = Measure.findAll("from Measure as m \
                                where m.assay = :assay and m.element = :resultType",
                            [assay: measureContext.assay, resultType: measureContext.element])
                    if (existingMeasures) return

                    measureContext.save()
                    if (measureContext.hasErrors()) {
                        Log.logger.info("MeasureContext errors")
                        writer.writeLine("MeasureContext Errors: ${measureContext.errors}")
                        assert false, "Measure-context errros"
                    } else {
                        writer.writeLine("Assay ID: ${measureContext.assay.id}; Element: ${measureContext.element.label}")
                    }

                    //comment out to commit the transaction
                    //status.setRollbackOnly()
                }
            }
        }
    }


}
