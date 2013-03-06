package bard.db.project

import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.experiment.ExperimentService
import bard.db.registration.MeasureTreeService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ExperimentController {
    ExperimentService experimentService;
    MeasureTreeService measureTreeService

    def create() {
        def assay = Assay.get(params.assayId)

        renderCreate(assay, new Experiment())
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
        experiment.properties["experimentName","description","holdUntilDate","runDateFrom","runDateTo"] = params
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
        experiment.properties["experimentName","description","holdUntilDate","runDateFrom","runDateTo"] = params
        experiment.dateCreated = new Date()
        if (!experiment.save(flush: true)) {
            println("errors:"+experiment.errors)
            renderCreate(assay, experiment)
        } else {
            experimentService.updateMeasures(experiment, JSON.parse(params.experimentTree))
            redirect(action: "show", id: experiment.id)
        }
    }

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            return
        }

        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance, false))

        [instance: experimentInstance, measuresAsJsonTree: measuresAsJsonTree]
    }
}
