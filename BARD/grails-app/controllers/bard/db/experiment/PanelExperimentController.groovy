package bard.db.experiment

import bard.db.registration.Panel
import grails.validation.ValidationException
import org.springframework.dao.DataIntegrityViolationException

class PanelExperimentController {

    static allowedMethods = [save: "POST", update: "POST"]

    def create() {
        [panelExperimentCommand: new PanelExperimentCommand()]
    }

    /**
     * Accepts a list of EIDs and a Panel. Creates and saves a new Panel-Experiment, associating the experiments to the panel.
     * If one or more experiment is already associated with a panel, return that back to user and ask for a confirmation to override the experiment.panel
     *
     * @param panelExperimentCommand
     * @return
     */
    def save(PanelExperimentCommand panelExperimentCommand) {
        if (!panelExperimentCommand.validate()) {
            render(view: "create", model: [panelExperimentCommand: panelExperimentCommand])
            return
        }

        PanelExperiment panelExperimentInstance = panelExperimentCommand.panelExperiment ?: new PanelExperiment(panel: panelExperimentCommand.panel)
        try {
            List<Experiment> experimentsAlreadyLinkedToAnotherPanel = []

            for (Long experimentId : panelExperimentCommand.experimentIds) {
                Experiment experiment = Experiment.findById(experimentId)
                if (!experiment.panel || panelExperimentCommand.confirmExperimentPanelOverride) {
                    experiment.panel = panelExperimentInstance
                    panelExperimentInstance.experiments.add(experiment)
                    experiment.save(failOnError: true)
                } else {
                    experimentsAlreadyLinkedToAnotherPanel.add(experiment)
                    continue
                }
            }

            panelExperimentInstance.save(failOnError: true, flush: true)

            if (experimentsAlreadyLinkedToAnotherPanel) {
                flash.message = message(code: 'panelExperiment.create.confirmOverride', default: 'Some experiments are already associated with an existing Panel-Experiment. Please confirm associating them with a new Panel-Experiment', args: [experimentsAlreadyLinkedToAnotherPanel*.id.join(', ')])
                panelExperimentCommand.confirmExperimentPanelOverride = true
                panelExperimentCommand.panelExperiment = panelExperimentInstance
                panelExperimentCommand.experimentIds = []
                render(view: "create", model: [panelExperimentCommand: panelExperimentCommand, experimentsAlreadyLinkedToAnotherPanel: experimentsAlreadyLinkedToAnotherPanel])
                return
            }

            flash.message = message(code: 'panelExperiment.create.success', default: 'Panel-Experiment was created successfully', args: [panelExperimentInstance.id])
        } catch (ValidationException exp) {
            flash.message = message(code: 'panelExperiment.create.failed', default: 'Could not create Panel-Experiment')
        }

        redirect(controller: "bardWebInterface", action: "navigationPage")
    }
}

class PanelExperimentCommand {
    Panel panel
    List<Long> experimentIds = []
    Boolean confirmExperimentPanelOverride = false
    PanelExperiment panelExperiment

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