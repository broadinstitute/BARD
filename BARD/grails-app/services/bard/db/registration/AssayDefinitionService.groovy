package bard.db.registration

import acl.CapPermissionService
import bard.db.context.item.ContextDTO
import bard.db.context.item.ContextItemDTO
import bard.db.enums.AssayType
import bard.db.enums.Status
import bard.db.people.Person
import bard.db.people.Role
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.Query
import org.hibernate.Session
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class AssayDefinitionService {
    AssayService assayService
    CapPermissionService capPermissionService
    def springSecurityService

    @PreAuthorize("hasPermission(#id, 'bard.db.project.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateOwnerRole(Long id, Role ownerRole) {
        Assay assay = Assay.findById(id)
        assay.ownerRole = ownerRole
        assay.save(flush: true)


        capPermissionService.updatePermission(assay, ownerRole)
        return Assay.findById(id)
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
    Assay updateAssayStatus(long id, Status assayStatus) {
        Assay assay = Assay.findById(id)
        final Status originalStatus = assay.assayStatus

        assay.assayStatus = assayStatus
        assay.validateItems()
        if (!assay.hasErrors()) {
            if ((Status.APPROVED == assay.assayStatus || Status.PROVISIONAL == assay.assayStatus) && originalStatus != assay.assayStatus) {
                Person currentUser = Person.findByUserName(springSecurityService.authentication.name)
                assay.approvedBy = currentUser
                assay.approvedDate = new Date()
            }
            assay.save(flush: true)
        }
        return assay
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

//    Object getApprovalInfoForAssayStatus(Long id){
//        Assay.withSession { Session session ->
//            Query query = session.createSQLQuery("""
//                        select *
//                        from(
//                        select
//                        arl.AUDIT_TIMESTAMP
//                        ,arl.USERNAME audit_username
//                        ,p.USERNAME   person_username
//                        ,p.FULL_NAME
//                        from AUDIT_ROW_LOG arl
//                        join AUDIT_COLUMN_LOG acl on acl.AUDIT_ID = arl.AUDIT_ID
//                        join ASSAY a on a.ASSAY_ID = arl.PRIMARY_KEY
//                        left outer join PERSON p on p.USERNAME = arl.USERNAME
//                        where arl.TABLE_NAME = 'ASSAY'
//                        and acl.COLUMN_NAME = 'ASSAY_STATUS'
//                        and arl.ACTION = 'UPDATE'
//                        and a.ASSAY_ID = :assayId
//                        order by arl.AUDIT_TIMESTAMP desc
//                        )
//                        where rownum = 1
//                    """)
//            query.setLong('assayId', id)
//            query.setReadOnly(true)
//            return query.uniqueResult()
//        }
//    }
}
