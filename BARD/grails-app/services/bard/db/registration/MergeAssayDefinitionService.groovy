package bard.db.registration

import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import grails.plugins.springsecurity.SpringSecurityService
import merge.MergeAssayService
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/20/13
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
class MergeAssayDefinitionService {
    MergeAssayService mergeAssayService
    SpringSecurityService springSecurityService
    def sessionFactory

    List<Long> filterOutExperimentsNotOwnedByMe(List<Long> experimentIds) {
        //if you are an admin you can move any experiment
        if (BardCommand.isCurrentUserBARDAdmin()) {
            return experimentIds
        }
        if (!experimentIds) {
            return []
        }
        List<Long> roleIds = SpringSecurityUtils.principalAuthorities*.id
        if (!roleIds) {
            return []
        }

        String OWNED_EXPERIMENT_QUERY = "select experiment.id from Experiment experiment WHERE experiment.id in (" +
                StringUtils.join(experimentIds, ",") + ") AND experiment.ownerRole.id in (" + StringUtils.join(roleIds, ",") + ")"


        final List<Long> ownedExperiments = Experiment.executeQuery(OWNED_EXPERIMENT_QUERY)

        return ownedExperiments

    }

    List<Long> normalizeEntitiesToMoveToExperimentIds(final List<Long> entityIdsToMove, final IdType idType, final Assay targetAssay) {
        final List<Long> notFoundEntities = []
        final List<Long> entitiesToMove = []
        for (Long id : entityIdsToMove) {

            def found = convertIdToEntity(idType, id)
            if (found) {
                switch (idType) {
                    case IdType.ADID:
                        Assay assay = (Assay) found
                        if (assay == targetAssay) {
                            throw new RuntimeException("Assay with ${idType.name} ${id} cannot be moved into itself. Please remove it from the 'Assays to move list'")
                        }
                        for (Experiment experiment : assay.experiments) {
                            entitiesToMove << experiment.id
                        }
                        break;
                    case IdType.AID:
                        Experiment experiment = (Experiment) found
                        if (experiment.assay == targetAssay) {
                            throw new RuntimeException("Experiment with ID: ${id} already belongs to the target Assay ${targetAssay.id}. Please remove it from the 'Experiments to move list'")
                        }
                        entitiesToMove << experiment.id
                        break;
                    case IdType.EID:
                        Experiment foundExperiment = (Experiment) found
                        if (foundExperiment.assay == targetAssay) {
                            throw new RuntimeException("Experiment with ID: ${id} already belongs to the target Assay ${targetAssay.id}. Please remove it from the 'Experiments to move list'")
                        }
                        entitiesToMove << foundExperiment.id
                        break;
                }
            }
            if (!found) {
                notFoundEntities.add(id)
            }
        }
        //if we did not find any of the given assays throw an exception
        if (notFoundEntities) {
            switch (idType) {
                case IdType.ADID:
                case IdType.AID:
                    throw new RuntimeException("Could not find assays with ${idType.name}: " + StringUtils.join(notFoundEntities, ","))
                    break;
                case IdType.EID:
                    throw new RuntimeException("Could not find the following experiments with ids: " + StringUtils.join(notFoundEntities, ","))
                    break;
            }

        }
        return entitiesToMove
    }
    /**
     *
     * @param idType
     * @param entityId -  Could be an AID, ADID or EID
     * @return the entity (one of Experiment or Assay)
     */
    def convertIdToEntity(final IdType idType, final Long entityId) {
        return findEntityByIdType(entityId, idType)
    }

    static List<Long> convertStringToIdList(final String idsAsString) {
        final List<Long> ids = []

        final List<String> idsAsList = idsAsString?.trim()?.split("\\W+") as List<String>

        idsAsList.each {
            ids << Long.valueOf(it.trim())
        }
        return ids
    }

    void validateConfirmMergeInputs(final Long targetAssayId, final String assayIdsToMerge, final IdType idType) {
        if (!targetAssayId) {
            throw new RuntimeException("The ID of the Assay to merge into is required and must be a number")
        }

        if (!idType) {
            throw new RuntimeException("Select one of ${IdType.ADID.name} or ${IdType.AID.name} or ${IdType.EID.name}")
        }
        if (!assayIdsToMerge?.trim()) {
            String errorMessage = ""
            switch (idType) {
                case IdType.EID:
                    errorMessage = "Enter at least one experiment ID to move"
                    break;
                case IdType.ADID:
                    errorMessage = "Enter at least one Assay Definition ID(ADID) to move"
                    break;
                case IdType.AID:
                    errorMessage = "Enter at least one PubChem AID to move"
                    break;
            }
            throw new RuntimeException(errorMessage)
        }

    }
    /**
     *
     * @param entityId -  Could be an AID, ADID or EID
     *  * @param idType
     * @return the entity (one of Experiment or Assay)
     */
    def findEntityByIdType(final Long entityId, final IdType idType) {
        switch (idType) {
            case IdType.ADID:
                return Assay.findById(entityId)
            case IdType.EID:
                return Experiment.findById(entityId)
            case IdType.AID:
                ExternalReference externalReference = bard.db.registration.ExternalReference.findByExtAssayRef("aid=${entityId}")
                if (externalReference) {
                    return externalReference.experiment
                }

        }
        return null
    }


    protected void validateExperimentContextItem(final ExperimentContextItem experimentContextItem,
                                                 final Map<Element, AssayContextItem> targetElementToAssayContextItemMap,
                                                 final List<String> errorMessages) {
        final Element element = experimentContextItem.attributeElement
        final AssayContextItem assayContextItem = targetElementToAssayContextItemMap.get(element)
        if (assayContextItem) {
            if (assayContextItem.attributeType == AttributeType.Fixed) {
                //Context item with fixed attribute type cannot have experiment context items associated to it
                StringBuilder builder = new StringBuilder("Cannot validate Experiment Context Item : ${experimentContextItem.id},")
                builder.append(" on Experiment: ${experimentContextItem.experimentContext.experiment.id},")
                builder.append(" which is part of the Assay: ${experimentContextItem.experimentContext.experiment.assay.id} ")
                builder.append(" because the target context item ${assayContextItem.id} has an attribute type of ${assayContextItem.attributeType}")

                errorMessages.add(builder.toString())
            }
        } else {
            StringBuilder builder = new StringBuilder("Experiment Context Item : ${experimentContextItem.id},")
            builder.append(" on Experiment: ${experimentContextItem.experimentContext.experiment.id},")
            builder.append(" which is part of the Assay: ${experimentContextItem.experimentContext.experiment.assay.id}")
            builder.append(" does not exist as a context item on the target assay")

            errorMessages.add(builder.toString())
        }

    }

    Assay moveExperimentsFromAssay(Assay targetAssay,
                                   List<Experiment> experiments) {
        targetAssay.fullyValidateContextItems = false
        String modifiedBy = springSecurityService.principal?.username

        sessionFactory.currentSession.flush()
        mergeAssayService.handleMeasures(targetAssay, experiments.collect { it.assay } as Set)
        sessionFactory.currentSession.flush()

        mergeAssayService.moveExperiments(experiments, targetAssay, modifiedBy)
        println("end handleExperiments")

        sessionFactory.currentSession.flush()

        return Assay.findById(targetAssay.id)

    }

    public Assay mergeAllAssays(final Assay targetAssay, final List<Assay> assaysToMerge) {
        println("start handleExperiments")
        //Turn full validation off for this assay so that u can validate the context items
        targetAssay.fullyValidateContextItems = false
        String modifiedBy = springSecurityService.principal?.username
        mergeAssayService.handleExperiments(assaysToMerge, targetAssay, modifiedBy)     // associate experiments with kept
        println("end handleExperiments")
        sessionFactory.currentSession.flush()



        println("Update assays status to Retired")
        mergeAssayService.updateStatus(assaysToMerge, modifiedBy)         // associate measure
        println("End of marking assayStatus to retired")
        sessionFactory.currentSession.flush()
        return Assay.findById(targetAssay.id)
    }
}

enum IdType {
    ADID('Assay Definition ID'),
    AID('PubChem AID'),
    EID("Experiment ID")

    String name

    IdType(String name) {
        this.name = name
    }
}

