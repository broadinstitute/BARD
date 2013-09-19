package bard.db.registration

import bard.core.SearchParams
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.assays.AssayResult
import bard.db.context.item.ContextDTO
import bard.db.context.item.ContextItemDTO
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import bard.db.enums.ReadyForExtraction
import org.apache.commons.collections.CollectionUtils
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class AssayDefinitionService {
    AssayService assayService
    AssayRestService assayRestService

    Map generateAssayComparisonReport(final Assay assayOne, final Assay assayTwo) {

        //get all assay context items in AssayOne
        final List<ContextItemDTO> assayOneContextItems = ContextItemDTO.toContextItemDTOs(assayOne.assayContextItems)

        //get all assay context items in AssayTwo
        final List<ContextItemDTO> assayTwoContextItems = ContextItemDTO.toContextItemDTOs(assayTwo.assayContextItems)

        //Subtract 2 from 1 to get assay context items exclusive to 1
        Collection<ContextItemDTO> exclusiveToAssayOne = CollectionUtils.subtract(assayOneContextItems, assayTwoContextItems)

        //Subtract 1 from 2 to get assay context items exclusive to 2
        Collection<ContextItemDTO> exclusiveToAssayTwo = CollectionUtils.subtract(assayTwoContextItems, assayOneContextItems)

        //Reconstruct the cards containing the exclusive items for each assay
        final SortedMap<ContextDTO, List<ContextItemDTO>> cardMapForAssayOne = ContextItemDTO.buildCardMap(exclusiveToAssayOne)

        //for each context that has exclusive items, add back all of the context items
        ContextItemDTO.addCommonItemsToEachCard(cardMapForAssayOne)
        //for each one, get the context id and then find all the context items and mark as exclusive
        final SortedMap<ContextDTO, List<ContextItemDTO>> cardMapForAssayTwo = ContextItemDTO.buildCardMap(exclusiveToAssayTwo)
        ContextItemDTO.addCommonItemsToEachCard(cardMapForAssayTwo)

        return [
                exclusiveToAssayOne: cardMapForAssayOne,
                exclusiveToAssayTwo: cardMapForAssayTwo,
                assayOneName: assayOne.assayName,
                assayOneADID: assayOne.id,
                assayTwoName: assayTwo.assayName,
                assayTwoADID: assayTwo.id
        ]
    }

    /**
     * Move measure to parent
     * @param assay
     * @param measure
     * @param parentMeasure
     * @return
     */
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void moveMeasure(Long id, Measure measure, Measure parentMeasure) {

        measure.parentMeasure = parentMeasure
        if (!measure.parentChildRelationship) {
            if (parentMeasure) {
                measure.parentChildRelationship = HierarchyType.SUPPORTED_BY
            }
        }
    }

    Assay saveNewAssay(Assay assayInstance) {
        return assayInstance.save(flush: true)
    }

    Assay recomputeAssayShortName(Assay assay) {
        return assayService.recomputeAssayShortName(assay)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayType(long id, AssayType assayType) {
        Assay assay = Assay.findById(id)
        assay.assayType = assayType
        assay.save(flush: true)
        return Assay.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayStatus(long id, AssayStatus assayStatus) {
        Assay assay = Assay.findById(id)
        assay.assayStatus = assayStatus
        assay.save(flush: true)
        return Assay.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayName(Long id, String newAssayName) {
        Assay assay = Assay.findById(id)
        assay.assayName = newAssayName
        //validate version here
        assay.save(flush: true)
        return Assay.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateDesignedBy(long id, String newDesigner) {
        Assay assay = Assay.findById(id)
        assay.designedBy = newDesigner
        assay.save(flush: true)
        return Assay.findById(id)
    }
    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments and documents)
     */
    Assay cloneAssayForEditing(Assay assay, String designedBy) {
        return assayService.cloneAssayForEditing(assay, designedBy)
    }

    void loadNCGCAssayIds() {
        //look for assays with an NCGC ID of null and Ready for extraction status of complete
        List<Long> capIds = Assay.findAllByNcgcWarehouseIdIsNullAndReadyForExtraction(ReadyForExtraction.COMPLETE).collect { it.id }

        if (!capIds) {
            return
        }
        int start = 0
        final int NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND = 5
        int end = capIds.size() > NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND ? NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND : capIds.size()
        List<String> updateStatements = []
        List<Long> toProcess = capIds.subList(start, end)
        while (toProcess) {
            updateStatements.addAll(loadNCGCAssayIdsFromList(toProcess))
            start = end
            end = end + NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND

            if (end > capIds.size()) {
                end = capIds.size()
            }
            if (start == end) {
                toProcess = []
            } else {
                toProcess = capIds.subList(start, end)
            }
        }

        println updateStatements.join("\n")
    }

    List<String> loadNCGCAssayIdsFromList(List<Long> capAssayIds) {
        if (!capAssayIds) {
            return
        }
        List<String> updateStatements = []
        SearchParams searchParams = new SearchParams(skip: 0, top: capAssayIds.size())

        try {
            AssayResult assayResult = assayRestService.searchAssaysByCapIds(capAssayIds, searchParams)
            if (assayResult) {
                final List<bard.core.rest.spring.assays.Assay> assays = assayResult.assays
                if (assays) {
                    for (bard.core.rest.spring.assays.Assay assayR : assays) {
                        if (assayR) {
                            //batch update or do it one by one?
                            final Assay assay = Assay.get(assayR.capAssayId)
                            if (assay) {
                                updateStatements.add("UPDATE ASSAY SET NCGC_WAREHOUSE_ID=${assayR.bardAssayId} WHERE ASSAY_ID=${assayR.capAssayId};")
                                assay.ncgcWarehouseId = assayR.bardAssayId
                                assay.save(flush: true)
                            } else {
                                log.error("Could not find Assay with id ${assayR.capAssayId}")
                            }
                        }
                    }
                } else {
                    log.error("The following Cap Assay Ids ${capAssayIds} could not be found in the warehouse")
                }
            } else {
                log.error("The following Cap Assay Ids ${capAssayIds} could not be found in the warehouse")
            }
        } catch (Exception ee) {
            log.error("Error From loadNCGCAssayIdsFromList " + ee.message)
            ee.stackTrace
        }
        return updateStatements
    }

}
