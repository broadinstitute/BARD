package bard.admin

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import bard.db.registration.IdType
import bard.db.registration.MergeAssayDefinitionService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.validation.ValidationException
import groovy.transform.InheritConstructors
import org.apache.commons.lang.StringUtils

import javax.servlet.http.HttpServletResponse

@Mixin([EditingHelper])
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class MoveExperimentsController {
    MergeAssayDefinitionService mergeAssayDefinitionService

    SpringSecurityService springSecurityService

    def index() {
        redirect(action: "show")
    }

    def show() {

    }

    def confirmMoveExperiments(ConfirmMoveExperimentsCommand confirmMoveExperimentsCommand) {
        try {
            mergeAssayDefinitionService.validateConfirmMergeInputs(confirmMoveExperimentsCommand.targetAssayId, confirmMoveExperimentsCommand.sourceEntityIds, confirmMoveExperimentsCommand.idType)

            final List<Long> entityIdsToMove = mergeAssayDefinitionService.convertStringToIdList(confirmMoveExperimentsCommand.sourceEntityIds)

            final Assay targetAssay = mergeAssayDefinitionService.convertIdToEntity(IdType.ADID, confirmMoveExperimentsCommand.targetAssayId)

            if (!targetAssay) {
                throw new RuntimeException("Could not find assay with ADID: ${confirmMoveExperimentsCommand.targetAssayId}")
            }
            final List<Long> experimentsToMove = mergeAssayDefinitionService.normalizeEntitiesToMoveToExperimentIds(
                    entityIdsToMove, confirmMoveExperimentsCommand.idType, targetAssay)

            MoveExperimentsCommand moveExperimentsCommand = new MoveExperimentsCommand(targetAssay: targetAssay, experimentIds: experimentsToMove)
            render(status: HttpServletResponse.SC_OK, template: "selectExperimentsToMove", model: [moveExperimentsCommand: moveExperimentsCommand])


        } catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: ee.message])
            log.error("error moving experiment", ee)
        }
    }



    def moveSelectedExperiments(MoveExperimentsCommand moveExperimentsCommand) {
        if (!moveExperimentsCommand.experimentIds) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Select at least one experiment to move to target"])
            return
        }
        if (!moveExperimentsCommand.experiments) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Could not find experiments with ids ${StringUtils.join(moveExperimentsCommand.experimentIds)}"])
            return
        }

        try {
            Assay targetAssay = mergeAssayDefinitionService.moveExperimentsFromAssay(
                    moveExperimentsCommand.targetAssay,
                    moveExperimentsCommand.experiments
            )

            render(status: HttpServletResponse.SC_OK, template: "moveExperimentsSuccess", model: [
                    targetAssay: targetAssay, movedExperiments: moveExperimentsCommand.experiments])
        }
        catch (ValidationException validationError) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "moveValidationError", model: [validationError: validationError])

        }
        catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: ee?.message])
            log.error("error moving experiment", ee)
        }
    }
}

@InheritConstructors
@Validateable
class MoveExperimentsCommand extends BardCommand {
    Assay sourceAssay
    Assay targetAssay
    List<Long> experimentIds

    MoveExperimentsCommand() {}

    List<Experiment> getExperiments() {
        List<Experiment> experiments = []
        for (Long id : experimentIds) {
            final Experiment experiment = Experiment.findById(id)
            if (experiment) {
                experiments.add(experiment)
            }
        }
        return experiments
    }
}
@InheritConstructors
@Validateable
class ConfirmMoveExperimentsCommand extends BardCommand {
    Long targetAssayId
    String sourceEntityIds
    IdType idType

    ConfirmMoveExperimentsCommand() {}



    static constraints = {
        targetAssayId(nullable: false)
        sourceEntityIds(nullable: false, blank: false)
        idType(nullable: false)
    }
}
@InheritConstructors
@Validateable
class ExperimentsFromAssayCommand extends BardCommand {

    Long targetAssayId
    Long sourceAssayId
    List<String> errorMessages = []

    ExperimentsFromAssayCommand() {}

    Assay getTargetAssay() {
        return Assay.findById(targetAssayId)
    }

    Assay getSourceAssay() {
        return Assay.findById(sourceAssayId)
    }



    static constraints = {
        sourceAssayId(nullable: false)
        targetAssayId(nullable: false)
    }
}



