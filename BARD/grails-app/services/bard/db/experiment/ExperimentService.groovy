package bard.db.experiment

import acl.CapPermissionService
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.enums.Status
import bard.db.people.Person
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bardqueryapi.TableModel
import bardqueryapi.experiment.ExperimentBuilder
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang.StringUtils
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class ExperimentService {

    AssayService assayService;
    ResultsExportService resultsExportService
    ExperimentBuilder experimentBuilder
    CapPermissionService capPermissionService
    SpringSecurityService springSecurityService


    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    ExperimentMeasure updateExperimentMeasure(Long id, ExperimentMeasure experimentMeasure, List<Long> assayContextIds) {
        ExperimentMeasure refreshedMeasure = experimentMeasure.save(flush: true)
        Experiment experiment = Experiment.get(id)
        if (experiment.experimentFiles) {  //no need to continue because we have already uploaded data
            return refreshedMeasure
        }

        final Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = refreshedMeasure.getAssayContextExperimentMeasures()

        if (assayContextExperimentMeasures) {


            List<AssayContextExperimentMeasure> assayContextExperimentMeasuresToRemove = []

            for (AssayContextExperimentMeasure assayContextExperimentMeasure : assayContextExperimentMeasures) {
                final AssayContext assayContext = assayContextExperimentMeasure.assayContext
                final long assayContextId = assayContext.id
                if (assayContextIds?.contains(assayContextId)) {
//means this context already exist, so no need to add it again
                    assayContextIds.remove(assayContextId)

                } else { //mark for removal, means that user wants to remove this measure
                    assayContextExperimentMeasuresToRemove.add(assayContextExperimentMeasure)
                }

            }
            assayContextExperimentMeasuresToRemove.each { AssayContextExperimentMeasure assayContextExperimentMeasure ->
                disassociateMeasureFromContext(assayContextExperimentMeasure, refreshedMeasure, assayContextExperimentMeasure.assayContext)
            }
            refreshedMeasure = refreshedMeasure.save(flush: true)
            if (!assayContextIds) {
                return refreshedMeasure
            }
        }

        //Now add only the newly added context items
        if (assayContextIds) {
            addAssayContextExperimentMeasures(assayContextIds, refreshedMeasure)
        }
        return refreshedMeasure

    }

    void disassociateMeasureFromContext(AssayContextExperimentMeasure assayContextExperimentMeasure, ExperimentMeasure measure, AssayContext assayContext) {
        assayContext.removeFromAssayContextExperimentMeasures(assayContextExperimentMeasure)
        measure.removeFromAssayContextExperimentMeasures(assayContextExperimentMeasure)
        assayContextExperimentMeasure.delete(flush: true)

    }

    void addAssayContextExperimentMeasures(List<Long> assayContextIds, ExperimentMeasure experimentMeasure) {
        for (Long contextId : assayContextIds) {
            AssayContext assayContext = AssayContext.get(contextId)
            if (assayContext) {
                AssayContextExperimentMeasure assayContextExperimentMeasure = new AssayContextExperimentMeasure()
                assayContext.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)
                experimentMeasure.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)
                assayContextExperimentMeasure.save(flush: true)
            }
        }
        if (assayContextIds) {
            experimentMeasure.save(flush: true)
        }

    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteExperimentMeasure(final Long id, final ExperimentMeasure experimentMeasure) {

        experimentMeasure.delete(flush: true);
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    ExperimentMeasure createNewExperimentMeasure(final Long id,
                                                 final ExperimentMeasure parentExperimentMeasure,
                                                 final Element resultType,
                                                 final Element statsModifier,
                                                 final List<Long> assayContextIds,
                                                 final HierarchyType hierarchyType,
                                                 final boolean isPriorityElement
    ) {
        Experiment experiment = Experiment.findById(id)


        ExperimentMeasure experimentMeasure =
                new ExperimentMeasure(experiment: experiment, resultType: resultType,
                        priorityElement: isPriorityElement, modifiedBy: springSecurityService.principal?.username, parent: parentExperimentMeasure,
                        parentChildRelationship: hierarchyType, statsModifier: statsModifier)
        experimentMeasure = experimentMeasure.save(flush: true)


        if (parentExperimentMeasure) {
            parentExperimentMeasure.addToChildMeasures(experimentMeasure)
            parentExperimentMeasure.save(flush: true)
        }
        //Now add the selected context
        if (assayContextIds) {
            addAssayContextExperimentMeasures(assayContextIds, experimentMeasure)
        }
        return experimentMeasure.refresh()
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    TableModel previewResults(final Long id, final int numberOfRecords = 10) {
        Experiment experiment = Experiment.findById(id)
        List<JsonSubstanceResults> results = resultsExportService.readResultsForSubstances(experiment, numberOfRecords)
        return experimentBuilder.buildModelForPreview(experiment, results)
    }


    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateOwnerRole(final Long id, final Role ownerRole) {
        Experiment experiment = Experiment.findById(id)
        experiment.ownerRole = ownerRole
        experiment.save(flush: true)

        capPermissionService.updatePermission(experiment, ownerRole)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateRunFromDate(final Long id, final Date runDateFrom) {
        Experiment experiment = Experiment.findById(id)
        experiment.runDateFrom = runDateFrom
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateRunToDate(final Long id, final Date runDateTo) {
        Experiment experiment = Experiment.findById(id)
        experiment.runDateTo = runDateTo
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateHoldUntilDate(final Long id, final Date newHoldUntilDate) {
        Experiment experiment = Experiment.findById(id)
        experiment.holdUntilDate = newHoldUntilDate
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentDescription(final Long id, final String newExperimentDescription) {
        Experiment experiment = Experiment.findById(id)
        experiment.description = newExperimentDescription

        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentName(final Long id, final String newExperimentName) {
        Experiment experiment = Experiment.findById(id)
        experiment.experimentName = newExperimentName
        //validate version here
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentStatus(final Long id, final Status experimentStatus) {
        Experiment experiment = Experiment.findById(id)
        experiment.experimentStatus = experimentStatus
        if ((experimentStatus.equals(Status.APPROVED) || experimentStatus.equals(Status.PROVISIONAL)) && experiment.isDirty('experimentStatus')) {
            Person currentUser = Person.findByUserName(springSecurityService.authentication.name)
            experiment.approvedBy = currentUser
            experiment.approvedDate = new Date()
        }
            experiment.save(flush: true)
        return Experiment.findById(id)
    }

    void validateExperimentsToMerge(Assay oldAssay, List<Experiment> experiments) {
        List<String> errorMessages = []
        for (Experiment experiment : experiments) {
            if (experiment?.assay?.id != oldAssay?.id) {
                errorMessages.add("Experiment EID: ${experiment?.id} , does not belong to Assay ADID: ${oldAssay?.id}")
            }
        }
        if (errorMessages) {
            throw new RuntimeException(StringUtils.join(errorMessages, ","))
        }
    }

    @PreAuthorize("hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay splitExperimentsFromAssay(Long assayId, List<Experiment> experiments) {
        Assay oldAssay = Assay.findById(assayId)

        validateExperimentsToMerge(oldAssay, experiments)

        return splitExperiments(oldAssay, experiments)
    }

    private Assay splitExperiments(Assay oldAssay, List<Experiment> experiments) {


        def mapping = assayService.cloneAssay(oldAssay)

        Assay newAssay = mapping.assay
        newAssay.fullyValidateContextItems = false


        for (Experiment experiment : experiments) {
            oldAssay.removeFromExperiments(experiment)
            newAssay.addToExperiments(experiment)
        }
        oldAssay.save(flush: true)

        newAssay = newAssay.save(flush: true)
        return Assay.findById(newAssay?.id)
    }

    public static int getTotalLengthRec2(final List<String> lst) {
        return getTotalLengthRecHelper(lst, 0, lst.size());
    }

    public static int getTotalLengthRecHelper(final List<String> lst, final int from, final int to) {
        if (to - from <= 1)
            return lst.get(from).length();
        final int middle = (from + to) >> 1;
        return getTotalLengthRecHelper(lst, from, middle) + getTotalLengthRecHelper(lst, middle, to);
    }

}
