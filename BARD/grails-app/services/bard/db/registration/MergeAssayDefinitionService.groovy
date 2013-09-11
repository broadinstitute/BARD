package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import grails.plugins.springsecurity.SpringSecurityService
import merge.MergeAssayService
import org.apache.commons.lang3.StringUtils
import registration.AssayService

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
    AssayService assayService
    def sessionFactory

    List<Long> convertAssaysToMerge(
            final List<Long> assayIdsToMerge, final AssayIdType assayIdType, final Assay assayToMergeInto) {
        final List<Long> notFoundAssays = []
        final List<Long> assaysToMerge = []
        for (Long id : assayIdsToMerge) {
            Assay found = convertIdToAssayDefinition(assayIdType, id)
            if (found == assayToMergeInto) {
                throw new RuntimeException("Assay with ${assayIdType} ${id} cannot be merged into itself. Please remove it from the 'Assays to merge into list'")
            }
            if (found) {
                assaysToMerge << found.id
            } else {
                notFoundAssays.add(id)
            }
        }
        //if we did not find any of the given assays throw an exception
        if (notFoundAssays) {
            throw new RuntimeException("Could not find assays with ${assayIdType} " + StringUtils.join(notFoundAssays, ","))
        }
        return assaysToMerge
    }

    Assay convertIdToAssayDefinition(final AssayIdType assayIdType, final Long assayId) {
        return findAssayByAssayIdType(assayId, assayIdType)
    }

    static List<Long> convertStringToIdList(final String idsAsString) {
        final List<Long> ids = []

        final List<String> idsAsList = StringUtils.split(idsAsString, " ")

        idsAsList.each {
            ids << Long.valueOf(it)
        }
        return ids
    }

    void validateConfirmMergeInputs(final Long targetAssayId, final String assayIdsToMerge, final AssayIdType assayIdType) {
        if (!targetAssayId) {
            throw new RuntimeException("The ID of the Assay to merge into is required and must be a number")
        }

        if (!assayIdsToMerge?.trim()) {
            throw new RuntimeException("Enter at least one id for an assay to merge")
        }

        if (!assayIdType) {
            throw new RuntimeException("Select one of ${AssayIdType.ADID} or ${AssayIdType.AID}")
        }
    }

    Assay findAssayByAssayIdType(final Long someAssayId, final AssayIdType assayIdType) {
        switch (assayIdType) {
            case AssayIdType.ADID:
                return Assay.findById(someAssayId)
            case AssayIdType.AID:
                final List<Assay> assays = assayService.findByPubChemAid(someAssayId)
                if (assays) {
                    return assays.get(0)
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
     * @param assaysToMerge
     * @return
     */
    public List<String> validateAllContextItems(final Assay targetAssay, final List<Assay> assaysToMerge) {
        Map<Element, AssayContextItem> targetElementToAssayContextItemMap = [:]
        final List<AssayContextItem> assayContextItems = targetAssay.assayContextItems
        for (AssayContextItem assayContextItem : assayContextItems) {
            final Element element = assayContextItem.attributeElement
            targetElementToAssayContextItemMap.put(element, assayContextItem)
        }
        final List<String> errorMessages = []
        for (Assay assayToMerge : assaysToMerge) {
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


    Assay moveExperimentsFromAssay(Assay sourceAssay, Assay targetAssay,
                                   List<Experiment> experiments) {
        validateExperimentsToMerge(sourceAssay, experiments)
        targetAssay.fullyValidateContextItems = false

        String modifiedBy = springSecurityService.principal?.username
        mergeAssayService.moveExperiments(experiments,targetAssay,modifiedBy)
        println("end handleExperiments")
        sessionFactory.currentSession.flush()
       // def session, Assay sourceAssay, Assay targetAssay, List<Experiment> sourceExperiments, String modifiedBy
        //TODO: Removing copying of measures
      // mergeAssayService.handleMeasuresForMovedExperiments(sessionFactory.currentSession,sourceAssay,targetAssay,experiments,modifiedBy)
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
enum AssayIdType {
    ADID('ADID'),
    AID('AID')

    String name

    AssayIdType(String name) {
        this.name = name
    }
}

