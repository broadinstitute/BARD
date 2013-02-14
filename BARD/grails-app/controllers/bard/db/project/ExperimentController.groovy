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
        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(assay, true))

        println("measuresAsJsonTree=${measuresAsJsonTree}")

        [assay: assay, experiment: new Experiment(), measuresAsJsonTree: measuresAsJsonTree]
    }

    def save() {
        def assay = Assay.get(params.assayId)

        Experiment experiment = experimentService.createNewExperiment(assay, params.experimentName, params.description)
        if (experiment.hasErrors()) {
            render(view: "create", model: [assay: assay, experiment: experiment])
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
