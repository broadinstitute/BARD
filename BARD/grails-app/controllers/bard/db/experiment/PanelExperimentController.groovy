package bard.db.experiment

import bard.db.registration.Panel
import org.springframework.dao.DataIntegrityViolationException

class PanelExperimentController {

    static allowedMethods = [save: "POST", update: "POST"]

    def create() {
        [panelExperimentCommand: new PanelExperimentCommand()]
    }

    def save(PanelExperimentCommand panelExperimentCommand) {
        if (!panelExperimentCommand.validate()) {
            render(view: "create", model: [panelExperimentCommand: panelExperimentCommand])
            return
        }

        def panelExperimentInstance = new PanelExperiment(panel: panelExperimentCommand.panel)
        Boolean hasErrors = false
        for (Long experimentId : panelExperimentCommand.experimentIds) {
            Experiment experiment = Experiment.findById(experimentId)
            experiment.panel = panelExperimentInstance
            panelExperimentInstance.experiments.add(experiment)
            if (!experiment.save()) {
                hasErrors = true
            }
        }
        if (!panelExperimentInstance.save(flush: true)) {
            hasErrors = true
        }

        if (hasErrors) {
            flash.message = message(code: 'panelExperiment.create.failed', default: 'Could not create Panel-Experiment')
        } else {
            flash.message = message(code: 'panelExperiment.create.success', default: 'Panel-Experiment was created successfully', args: [panelExperimentInstance.id])
        }

        redirect(controller: "bardWebInterface", action: "navigationPage")
    }
}

class PanelExperimentCommand {
    Panel panel
    List<Long> experimentIds = []

    static constraints = {
        panel(nullable: false)
        experimentIds(nullable: true, validator: { value, command, err ->
            for (Long experimentId : value) {
                if (!Experiment.get(experimentId)) {
                    err.rejectValue('experimentIds', "associateExperimentsCommand.experiment.id.not.found",
                            ["${value}"] as Object[],
                            "Could not find Experiment with ID: ${experimentId}");
                }
            }
        })
    }
}