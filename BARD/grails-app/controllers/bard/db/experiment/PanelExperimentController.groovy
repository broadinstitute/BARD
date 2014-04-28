/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.experiment

import bard.db.registration.Panel
import grails.converters.JSON
import grails.validation.ValidationException

import javax.servlet.http.HttpServletResponse

class PanelExperimentController {

    static allowedMethods = [save: "POST", update: "POST"]
    ExperimentService experimentService

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

    def findExperimentsForPanelAjax(Long id) {
        Panel panel = Panel.findById(id)
        if (!panel) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "Could not find a panel with panel-id=${id}.", contentType: 'text/plain', template: null)
            return
        }

        Set<Experiment> experiments = experimentService.findExperimentsForPanel(panel)

        render experiments.sort { a, b -> a.id <=> b.id } as JSON
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
