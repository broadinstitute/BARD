package bard.db.project

import bard.db.experiment.Experiment

class ExperimentController {

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            return
        }

        [instance: experimentInstance]
    }
}
