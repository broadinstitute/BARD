package bard.admin

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
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

    def selectAssays(ExperimentsFromAssayCommand experimentsFromAssayCommand) {
        if (!experimentsFromAssayCommand.sourceAssayId) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Source Assay Id is required"])
            return
        }
        if (!experimentsFromAssayCommand.targetAssayId) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Target Assay Id is required"])
            return
        }

        if (!experimentsFromAssayCommand.sourceAssay) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Source Assay ${experimentsFromAssayCommand.sourceAssayId} not found"])
            return
        }
        if (!experimentsFromAssayCommand.targetAssay) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: "Target Assay ${experimentsFromAssayCommand.targetAssayId} not found"])
            return
        }


        render(status: HttpServletResponse.SC_OK, template: "selectExperimentsToMove",
                model: [sourceAssay: experimentsFromAssayCommand.sourceAssay, targetAssay: experimentsFromAssayCommand.targetAssay])

    }
    def moveSelectedExperiments(MoveExperimentsCommand moveExperimentsCommand){
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
                    moveExperimentsCommand.sourceAssay,
                    moveExperimentsCommand.targetAssay,
                    moveExperimentsCommand.experiments
            )

            Assay sourceAssay = Assay.findById(moveExperimentsCommand.sourceAssay?.id)
            render(status: HttpServletResponse.SC_OK, template: "moveExperimentsSuccess", model: [sourceAssay: sourceAssay,
                    targetAssay: targetAssay, movedExperiments:moveExperimentsCommand.experiments])
        }
        catch (ValidationException validationError) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "moveValidationError", model: [validationError: validationError])

        }
        catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "assayError", model: [message: ee?.message])
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



