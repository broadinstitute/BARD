package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Assay

import bard.dm.minimumassayannotation.ContextLoadResultsWriter

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ValidatorCreatorAndPersistor {
    final String modifiedBy

    final ContextLoadResultsWriter loadResultsWriter

    ValidatorCreatorAndPersistor(String modifiedBy, ContextLoadResultsWriter loadResultsWriter) {
        this.modifiedBy = modifiedBy
        this.loadResultsWriter = loadResultsWriter
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

    void writeMessageWhenAidNotFoundInDb(Long aid, String contextName) {
        loadResultsWriter.write(aid, null, contextName, ContextLoadResultsWriter.LoadResultType.fail, "could not find ADID for AID")
    }
}
