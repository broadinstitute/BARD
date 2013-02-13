package bard.db.project

import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.ExperimentService
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ExperimentController {
    ExperimentService experimentService;

    def create() {
        def assay = Assay.get(params.assayId)
        [assay: assay, experiment: new Experiment()]
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

        [instance: experimentInstance]
    }
}
