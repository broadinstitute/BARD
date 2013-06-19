package bard.db.project

import bard.db.enums.ExperimentStatus
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import bard.db.registration.MeasureTreeService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.SimpleDateFormat

class ExperimentCommand implements Serializable {


}
@Mixin([EditingHelper])
@Secured(['isAuthenticated()'])
class ExperimentController {
    static final DateFormat inlineDateFormater = new SimpleDateFormat("yyyy-MM-dd")


    ExperimentService experimentService;
    MeasureTreeService measureTreeService

    def create() {
        def assay = Assay.get(params.assayId)

        renderCreate(assay, new Experiment())
    }

    def editHoldUntilDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date holdUntilDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateHoldUntilDate(inlineEditableCommand.pk, holdUntilDate)
            final String updatedDateAsString = formatter.format(experiment.holdUntilDate)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editRunFromDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date runFromDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateRunFromDate(inlineEditableCommand.pk, runFromDate)
            final String updatedDateAsString = formatter.format(experiment.runDateFrom)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editRunToDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date runToDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateRunToDate(inlineEditableCommand.pk, runToDate)
            final String updatedDateAsString = formatter.format(experiment.runDateTo)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editDescription(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Project.class)
            if (message) {
                conflictMessage(message)
                return
            }
            experiment = experimentService.updateExperimentDescription(inlineEditableCommand.pk, inlineEditableCommand.value)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.description)

        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editExperimentName(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            experiment = experimentService.updateExperimentName(inlineEditableCommand.pk, inlineEditableCommand.value)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.experimentName)

        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def experimentStatus() {
        List<String> sorted = []
        final Collection<ExperimentStatus> experimentStatuses = ExperimentStatus.values()
        for (ExperimentStatus experimentStatus : experimentStatuses) {
            sorted.add(experimentStatus.id)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null

    }

    def editExperimentStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final ExperimentStatus experimentStatus = ExperimentStatus.byId(inlineEditableCommand.value)
            final Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            experiment = experimentService.updateExperimentStatus(inlineEditableCommand.pk, experimentStatus)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.experimentStatus.id)

        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def edit() {
        def experiment = Experiment.get(params.id)

        renderEdit(experiment, experiment.assay)
    }

    def renderEdit(Experiment experiment, Assay assay) {
        renderEditFieldsView("edit", experiment, assay);
    }

    def renderCreate(Assay assay, Experiment experiment) {
        renderEditFieldsView("create", experiment, assay);
    }

    def renderEditFieldsView(String viewName, Experiment experiment, Assay assay) {
        JSON experimentMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experiment, false))
        JSON assayMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTreeWithSelections(assay, experiment, true))

        render(view: viewName, model: [experiment: experiment, assay: assay, experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree])
    }

    def update() {
        def experiment = Experiment.get(params.id)
        experimentService.updateMeasures(experiment, JSON.parse(params.experimentTree))
        if (!experiment.save(flush: true)) {
            renderEdit(experiment, experiment.assay)
        } else {
            redirect(action: "show", id: experiment.id)
        }
    }

    def save() {
        def assay = Assay.get(params.assayId)

        Experiment experiment = new Experiment()
        experiment.assay = assay
        setEditFormParams(experiment)
        experiment.dateCreated = new Date()
        if (!validateExperiment(experiment)) {
            renderCreate(assay, experiment)
        } else {
            if (!experiment.save(flush: true)) {
                renderCreate(assay, experiment)
            } else {
                experimentService.updateMeasures(experiment, JSON.parse(params.experimentTree))
                redirect(action: "show", id: experiment.id)
            }
        }
    }

    private boolean validateExperiment(Experiment experiment) {
        println "Validating Experiment dates"

        Date today = new Date();
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 1)
        Date oneYearFromToday = cal.getTime()
        if (experiment.holdUntilDate) {
            // Checks that the Hold Until Today is before today date
            if (experiment.holdUntilDate.before(today)) {
                experiment.errors.reject('experiment.holdUntilDate.incorrectEarlyValue', 'Hold Until Date must be equal or after today')
            }
            // Checks that the Hold Until Today date is not more than 1 year from today
            if (experiment.holdUntilDate.after(oneYearFromToday)) {
                experiment.errors.reject('experiment.holdUntilDate.incorrecMoreThan1YearFromToday', 'Hold Until Date is more than 1 year from today')
            }
        }
        if (experiment.runDateFrom && experiment.runDateTo) {
            //Checks that Run Date To is not before Run From Date
            if (experiment.runDateFrom.after(experiment.runDateTo)) {
                experiment.errors.reject('experiment.holdUntilDate.incorrecRunDateFrom', 'Run Date To cannot be before Run From Date')
            }
        }
        return !experiment.hasErrors()
    }

    private def setEditFormParams(Experiment experiment) {
        experiment.properties["experimentName", "description", "experimentStatus"] = params
        experiment.holdUntilDate = params.holdUntilDate ? new SimpleDateFormat("MM/dd/yyyy").parse(params.holdUntilDate) : null
        experiment.runDateFrom = params.runDateFrom ? new SimpleDateFormat("MM/dd/yyyy").parse(params.runDateFrom) : null
        experiment.runDateTo = params.runDateTo ? new SimpleDateFormat("MM/dd/yyyy").parse(params.runDateTo) : null


    }

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            return
        }

        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance, false))

        JSON assayMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance.assay, false))

        [instance: experimentInstance, measuresAsJsonTree: measuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree]
    }
}

