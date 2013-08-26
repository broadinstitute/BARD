package bard.db.registration

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.validation.ValidationException
import groovy.transform.InheritConstructors
import org.apache.commons.lang.StringUtils

import javax.servlet.http.HttpServletResponse

@Mixin([EditingHelper])
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class SplitAssayDefinitionController {
    ExperimentService experimentService
    SpringSecurityService springSecurityService

    def index() {
        redirect(action: "show")
    }

    def show() {

    }

    def selectExperimentsToMove(SplitAssayCommand splitAssayCommand) {
        if (!splitAssayCommand.assayId) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Assay Id is required"])
            return
        }
        final Assay assay = splitAssayCommand.assayToSplit
        if (!assay) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Assay Id ${splitAssayCommand.assayId} cannot be found"])
            return
        }

        render(status: HttpServletResponse.SC_OK, template: "selectExperimentsToSplit", model: [assay: assay])
    }

    def splitExperiments(SplitExperimentCommand splitExperimentCommand) {
        if (!splitExperimentCommand.experimentIds) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Select at least one experiment to move to new assay definition"])
            return
        }

        if (!splitExperimentCommand.experiments) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Could not find experiments with ids ${StringUtils.join(splitExperimentCommand.experimentIds)}"])
            return
        }
        try {
            Assay newAssay =
                experimentService.splitExperimentsFromAssay(splitExperimentCommand.assay.id, splitExperimentCommand.experiments)
            if(!newAssay){
                throw new RuntimeException("Could not split assay ${splitExperimentCommand.assay.id}")
            }
            Assay oldAssay = Assay.findById(splitExperimentCommand.assay?.id)
            render(status: HttpServletResponse.SC_OK, template: "splitAssaySuccess", model: [oldAssay: oldAssay, newAssay: newAssay])
        }
        catch (ValidationException validationError) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitValidationError", model: [validationError: validationError])

        }
        catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: ee?.message])
        }
    }
}

@InheritConstructors
@Validateable
class SplitAssayCommand extends BardCommand {
    Long assayId


    SplitAssayCommand() {}

    Assay getAssayToSplit() {
        return Assay.findById(assayId)
    }
}
@InheritConstructors
@Validateable
class SplitExperimentCommand extends BardCommand {
    Assay assay
    List<Long> experimentIds

    SplitExperimentCommand() {}

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


