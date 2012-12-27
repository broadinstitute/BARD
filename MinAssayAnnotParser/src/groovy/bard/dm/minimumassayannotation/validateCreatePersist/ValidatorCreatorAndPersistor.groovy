package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.Assay
import bard.dm.minimumassayannotation.ContextDTO

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ValidatorCreatorAndPersistor {
    final String modifiedBy



    ValidatorCreatorAndPersistor(String modifiedBy) {
        this.modifiedBy = modifiedBy
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

    /**
     * Make sure all the aid map to valid Assay IDs
     *
     * @param assayContextListCleaned
     */
    void validate(List<ContextDTO> assayContextListCleaned) {
    //Build aid-to-AssayId mapping and validate that all aid exist
        Map<Long, Assay> aidToAssayMap = [:]
        assayContextListCleaned*.aid.unique().each { Long AID ->
            Assay assay = getAssayFromAid(AID)
    //        assert assay, "Could not find an Assay that is associated with aid ${AID}"
            aidToAssayMap.put(AID, assay)
        }

        Map<Long, Assay> failedMapping = aidToAssayMap.findAll {aid, assay -> !assay}
    //    assert failedMapping.isEmpty(), "There must be only one and only one AssayId for each aid. Some aids are not associated with an assay: ${failedMapping.keySet()}"
    }
}
