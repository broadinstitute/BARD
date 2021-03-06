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

package registration

import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.Status
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayDocument
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class AssayService {
    public static final String clonePrefix = "Clone of "

    List<Assay> findByPubChemAid(Long aid) {
        def criteria = Assay.createCriteria()
        return criteria.listDistinct {
            experiments {
                externalReferences {
                    eq('extAssayRef', "aid=${aid.toString()}")
                }
            }
        }
    }

    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments)
     */
    Map cloneAssay(Assay assay) {
        String assayNamePrefix = ""
        Assay newAssay = cloneAssayOnly(assay, assay.dateCreated, assay.designedBy, assayNamePrefix, assay.assayStatus, assay.readyForExtraction)
        newAssay = newAssay.save(flush: true)
        Map<AssayContext, AssayContext> assayContextOldToNew = cloneContexts(assay, newAssay, false)
        cloneDocuments(assay, newAssay)
        return [assay: newAssay, measureOldToNew: [:]]
    }

    Role findRoleForCloning() {
        final Collection<Role> authorities = SpringSecurityUtils.getPrincipalAuthorities()
        for (Role role : authorities) {
            if (role.authority?.startsWith("ROLE_TEAM_")) {
                Role foundRole = Role.findByAuthority(role.authority)
                if (foundRole) {
                    return foundRole
                }
            }
        }

        if (SpringSecurityUtils?.ifAnyGranted('ROLE_BARD_ADMINISTRATOR')) { //if this is an admin
            return Role.findByAuthority('ROLE_BARD_ADMINISTRATOR')

        }
    }
    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments and documents)
     */
    Assay cloneAssayForEditing(Assay assay, String designedBy) {
        Assay newAssay = cloneAssayOnly(assay, new Date(), designedBy)
        if (newAssay.assayType == AssayType.TEMPLATE) { //convert templates to regular
            newAssay.assayType = AssayType.REGULAR
        }
        newAssay.ownerRole = findRoleForCloning()
        newAssay.save(flush: true, validate: false)

        cloneContexts(assay, newAssay, false)

        newAssay.save(flush: true, failOnError: true, validate: false)

        //now call the manage names stored procedure
        //then look up and return the assay
        return Assay.findById(newAssay.id)
    }

    /**
     *
     */
    Assay recomputeAssayShortName(Assay assay) {
//  DISABLING until rollback problem has been addressed
//        Assay.withSession { session ->
//            session.createSQLQuery("""BEGIN MANAGE_NAMES.UPDATE_ASSAY_SHORT_NAME('${assay.id}'); END;""").executeUpdate()
//        }
        //now call the manage names stored procedure
        //then look up and return the assay
//        return Assay.findById(assay.id)
        return assay
    }

    Assay cloneAssayOnly(Assay assay,
                         Date dateCreated,
                         String designedBy,
                         String assayNamePrefix = clonePrefix,
                         Status assayStatus = Status.DRAFT,
                         ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY) {
        //find the first role that starts with Team

      Role role = findRoleForCloning()

        String assayName = assayNamePrefix + assay.assayName
        //we do not want to go over the max number of characters
        if (assayName.length() >= Assay.ASSAY_NAME_MAX_SIZE) {
            assayName = assayName?.trim().substring(0, Assay.ASSAY_NAME_MAX_SIZE)
        }
        //assay version should always be set to 1 after colining
        return new Assay(
                assayStatus: assayStatus,
                assayName: assayName,
                assayVersion: "1",
                designedBy: designedBy,
                readyForExtraction: readyForExtraction,
                dateCreated: dateCreated,
                ownerRole: role

        )
    }

    Map<AssayContext, AssayContext> cloneContexts(Assay assay, Assay newAssay, boolean validate) {
        Map<AssayContext, AssayContext> assayContextOldToNew = [:]

        for (context in assay.assayContexts) {
            AssayContext newContext = context.clone(newAssay)
            assayContextOldToNew[context] = newContext

            newContext.save(failOnError: true, validate: validate)

            // this shouldn't be necessary, but it appears that if you save, bypassing validating, any invalid items
            // were not actually getting written to the db (even though they _would_ get an ID assigned)
            for (item in newContext.contextItems) {
                item.save(failOnError: true, validate: validate)
            }
        }
        return assayContextOldToNew
    }

    void cloneDocuments(Assay assay, Assay clonedAssay) {
        for (document in assay.assayDocuments) {
            AssayDocument newDocument = document.clone()
            clonedAssay.addToAssayDocuments(newDocument)

            newDocument.save(failOnError: true)
        }
    }
    /**
     *
     * @param assay
     * @param clonedAssay
     * @return
     */
    @Deprecated
    Map cloneMeasures(Assay assay, Assay clonedAssay) {
//        Map<Measure, Measure> measureOldToNew = [:]
//        for (measure in assay.measures) {
//            Measure newMeasure = measure.clone()
//
//            measureOldToNew[measure] = newMeasure
//
//            clonedAssay.addToMeasures(newMeasure)
//        }
        // return measureOldToNew
        return [:]
    }

    @Deprecated
    void assignParentMeasures(Assay assay, Map measureOldToNew) {
        // assign parent measures now that all measures have been created
        /*for (measure in assay.measures) {
            measureOldToNew[measure].parentMeasure = measureOldToNew[measure.parentMeasure]
        }
        for (measure in measureOldToNew.values()) {
            measure.save(failOnError: true)
        }*/
    }

    @Deprecated
    void cloneContextsMeasures(Assay assay, Map<AssayContext, AssayContext> assayContextOldToNew, Map measureOldToNew) {
//        Set<AssayContextMeasure> assayContextMeasures = assay.measures.collectMany { it.assayContextMeasures }
//
//        for (assayContextMeasure in assayContextMeasures) {
//            cloneContextsMeasure(assayContextMeasure, assayContextOldToNew, measureOldToNew)
//        }
    }

    @Deprecated
    void cloneContextsMeasure(def assayContextMeasure, Map<AssayContext, AssayContext> assayContextOldToNew, Map measureOldToNew) {

        /* AssayContext newAssayContext = assayContextOldToNew[assayContextMeasure.assayContext]
         Measure newMeasure = measureOldToNew[assayContextMeasure.measure]

         AssayContextMeasure newAssayContextMeasure = new AssayContextMeasure()

         newAssayContext.addToAssayContextMeasures(newAssayContextMeasure)
         newMeasure.addToAssayContextMeasures(newAssayContextMeasure)

         newAssayContextMeasure.save(failOnError: true)  */

    }


}
