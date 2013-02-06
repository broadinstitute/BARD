package bard.db.project

import bard.db.experiment.Experiment
import bard.db.registration.Assay

class ExperimentController {
    def create() {
        def assay = Assay.get(params.assayId)
        [assay: assay]
    }

    def save() {
        def assay = Assay.get(params.assayId)

        Experiment experiment = new Experiment(assay: assay)
        experiment.properties["description","experimentName"] = params
        experiment.dateCreated = new Date()
        if (!experiment.save(flush: true)) {
            flash.message = ""+experiment.errors
            redirect(action: "create", params: [ assayId: params.assayId ])
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
