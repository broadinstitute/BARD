package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Assay

import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import bard.dm.minimumassayannotation.ContextDTO

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ValidatorCreatorAndPersistor {
    String modifiedBy

    final ContextLoadResultsWriter loadResultsWriter

    final boolean flushSetting

    ValidatorCreatorAndPersistor(String modifiedBy, ContextLoadResultsWriter loadResultsWriter, boolean flushSetting) {
        this.modifiedBy = modifiedBy
        this.loadResultsWriter = loadResultsWriter
        this.flushSetting = flushSetting
    }

    /**
     * Finds an Assay that is associated with an AID.
     * If no assay found, or more than one assay are associated with a single aid, return null.
     *
     * @param AID
     * @return
     */
    Assay getAssayFromAid(long AID) {
        def criteria = Assay.createCriteria()
        List<Assay> results = criteria.list {
            experiments {
                externalReferences {
                    eq('extAssayRef', "aid=${AID.toString()}")
                }
            }
        }

        return (results && (results.size() == 1)) ? results.first() : null
    }

    void writeMessageWhenAidNotFoundInDb(ContextDTO contextDTO) {
        loadResultsWriter.write(contextDTO, null, ContextLoadResultsWriter.LoadResultType.fail, null, 0,
                "could not find ADID for AID")
    }
}
