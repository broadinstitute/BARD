package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import grails.plugins.springsecurity.SpringSecurityService
import merge.MergeAssayService
import org.apache.commons.lang3.StringUtils

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

    List<Long> convertStringToIdList(final String assayIdsToMerge) {
        final List<Long> ids = []

        final List<String> assayIds = StringUtils.split(assayIdsToMerge, " ")

        assayIds.each {
            ids << Long.valueOf(it)
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
                if(externalReference){
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

    protected void validateExperimentContextItems(final List<ExperimentContextItem> experimentContextItems,
                                                  final Map<Element, AssayContextItem> targetElementToAssayContextItemMap,
                                                  final List<String> errorMessages) {
        for (ExperimentContextItem experimentContextItem : experimentContextItems) {
            validateExperimentContextItem(experimentContextItem, targetElementToAssayContextItemMap, errorMessages)
        }
    }
    /**
     *  1.for each context item i, make sure there exists a context item j on the target assay where i.attributeElement == j.attributeElement
     *  2. and the Context Item on the target(merging into) assay must have an AttributeType != AttributeType.Fixed
     * @param targetAssay
     * @param sourceAssays
     * @return
     */
    public List<String> validateAllContextItems(final Assay targetAssay, final List<Assay> sourceAssays) {
        Map<Element, AssayContextItem> targetElementToAssayContextItemMap = [:]
        final List<AssayContextItem> assayContextItems = targetAssay.assayContextItems
        for (AssayContextItem assayContextItem : assayContextItems) {
            final Element element = assayContextItem.attributeElement
            targetElementToAssayContextItemMap.put(element, assayContextItem)
        }
        final List<String> errorMessages = []
        for (Assay assayToMerge : sourceAssays) {
            for (Experiment experiment : assayToMerge.experiments) {
                final List<ExperimentContextItem> experimentContextItems = experiment.experimentContextItems
                validateExperimentContextItems(experimentContextItems, targetElementToAssayContextItemMap, errorMessages)
            }
        }
        return errorMessages
    }

    void validateExperimentsToMerge(Assay oldAssay, List<Experiment> experiments) {
        List<String> errorMessages = []
        for (Experiment experiment : experiments) {
            if (experiment?.assay?.id != oldAssay?.id) {
                errorMessages.add("Experiment EID: ${experiment?.id} , does not belong to Assay ADID: ${oldAssay?.id}")
            }
        }
        if (errorMessages) {
            throw new RuntimeException(org.apache.commons.lang.StringUtils.join(errorMessages, ","))
        }
    }
    //use #moveExperimentsFromAssay(Assay,List<Experiment>) instead
    @Deprecated
    Assay moveExperimentsFromAssay(Assay sourceAssay, Assay targetAssay,
                                   List<Experiment> experiments) {
        validateExperimentsToMerge(sourceAssay, experiments)
        targetAssay.fullyValidateContextItems = false

        String modifiedBy = springSecurityService.principal?.username
        mergeAssayService.moveExperiments(experiments, targetAssay, modifiedBy)
        println("end handleExperiments")
        sessionFactory.currentSession.flush()
        // def session, Assay sourceAssay, Assay targetAssay, List<Experiment> sourceExperiments, String modifiedBy
        mergeAssayService.handleMeasuresForMovedExperiments(sessionFactory.currentSession, sourceAssay, targetAssay, experiments, modifiedBy)
        sessionFactory.currentSession.flush()

        return Assay.findById(targetAssay.id)

    }

    Assay moveExperimentsFromAssay(Assay targetAssay,
                                   List<Experiment> experiments) {
        targetAssay.fullyValidateContextItems = false

        String modifiedBy = springSecurityService.principal?.username
        mergeAssayService.moveExperiments(experiments, targetAssay, modifiedBy)
        println("end handleExperiments")
        sessionFactory.currentSession.flush()
        // def session, Assay sourceAssay, Assay targetAssay, List<Experiment> sourceExperiments, String modifiedBy
        mergeAssayService.handleMeasuresForMovedExperiments(sessionFactory.currentSession, targetAssay, experiments, modifiedBy)
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


        println("start handleMeasure")

        mergeAssayService.handleMeasure(sessionFactory.currentSession, assaysToMerge, targetAssay, modifiedBy)         // associate measure
        println("end handleMeasure")
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

