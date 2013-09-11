package bard.db.registration

import bard.db.context.item.ContextDTO
import bard.db.context.item.ContextItemDTO
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import bard.db.experiment.ExperimentMeasure
import bard.db.people.Person
import bard.db.people.Role
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Query
import org.hibernate.Session
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class AssayDefinitionService {
    AssayService assayService

    List<Assay> getAssaysByGroup(String username){
        List<Assay> results = []
        List<String> userRoles = new ArrayList<String>();
        Person person = Person.findByUserName(username)
        if(person){
            for(Role role : person.roles){
                userRoles.add(role.authority)
            }
            if(userRoles && userRoles.size() > 0){
                Assay.withSession { Session session ->
                    Query query = session.createSQLQuery("""
                        select a.*
                        from assay a,
                        acl_object_identity acl_oi,
                        acl_class acl_c,
                        acl_entry acl_e,
                        acl_sid sid
                        where acl_c.class = 'bard.db.registration.Assay'
                        and acl_c.id = acl_oi.object_id_class
                        and acl_e.acl_object_identity = acl_oi.id
                        and acl_e.sid = sid.id
                        and a.assay_id = acl_oi.object_id_identity
                        and sid.sid in (:user_roles)
                        order by a.date_created desc
                    """)
                    query.addEntity(Assay)
                    query.setParameterList('user_roles', userRoles)
                    query.setReadOnly(true)
                    results = query.list()
                }
            }
        }
        return results
    }

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
    void moveMeasure(Long id, ExperimentMeasure measure, ExperimentMeasure parentMeasure) {

        measure.parent = parentMeasure
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
}
