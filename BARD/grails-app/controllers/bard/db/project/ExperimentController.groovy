package bard.db.project

import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.ExperimentService
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

        JSON experimentMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experiment, false))
        JSON assayMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTreeWithSelections(assay, experiment, true))

        render(view: "edit", model: [experiment: experiment, experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree])
    }

    def renderCreate(Assay assay, Experiment experiment) {
        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(assay, true))

        render(view: "create", model: [assay: assay, experiment: experiment, measuresAsJsonTree: measuresAsJsonTree])
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
