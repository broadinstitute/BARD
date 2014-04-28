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

package bard.db.registration

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.validation.ValidationException
import groovy.transform.InheritConstructors
import org.apache.commons.lang.StringUtils

import javax.servlet.http.HttpServletResponse

@Mixin([EditingHelper])
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class SplitAssayDefinitionController {
    ExperimentService experimentService
    SpringSecurityService springSecurityService

    def index() {
        redirect(action: "show")
    }

    def show() {

    }

    def selectExperimentsToMove(SplitAssayCommand splitAssayCommand) {
        if (!splitAssayCommand.assayId) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Assay Id is required"])
            return
        }
        final Assay assay = splitAssayCommand.assayToSplit
        if (!assay) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Assay Id ${splitAssayCommand.assayId} cannot be found"])
            return
        }

        render(status: HttpServletResponse.SC_OK, template: "selectExperimentsToSplit", model: [assay: assay])
    }

    def splitExperiments(SplitExperimentCommand splitExperimentCommand) {
        if (!splitExperimentCommand.experimentIds) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Select at least one experiment to move to new assay definition"])
            return
        }

        if (!splitExperimentCommand.experiments) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: "Could not find experiments with ids ${StringUtils.join(splitExperimentCommand.experimentIds)}"])
            return
        }
        try {
            Assay newAssay =
                experimentService.splitExperimentsFromAssay(splitExperimentCommand.assay.id, splitExperimentCommand.experiments)
            if(!newAssay){
                throw new RuntimeException("Could not split assay ${splitExperimentCommand.assay.id}")
            }
            Assay oldAssay = Assay.findById(splitExperimentCommand.assay?.id)
            render(status: HttpServletResponse.SC_OK, template: "splitAssaySuccess", model: [oldAssay: oldAssay, newAssay: newAssay])
        }
        catch (ValidationException validationError) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitValidationError", model: [validationError: validationError])

        }
        catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "splitError", model: [message: ee?.message])
        }
    }
}

@InheritConstructors
@Validateable
class SplitAssayCommand extends BardCommand {
    Long assayId


    SplitAssayCommand() {}

    Assay getAssayToSplit() {
        return Assay.findById(assayId)
    }
}
@InheritConstructors
@Validateable
class SplitExperimentCommand extends BardCommand {
    Assay assay
    List<Long> experimentIds

    SplitExperimentCommand() {}

    List<Experiment> getExperiments() {
        List<Experiment> experiments = []
        for (Long id : experimentIds) {
            final Experiment experiment = Experiment.findById(id)
            if (experiment) {
                experiments.add(experiment)
            }
        }
        return experiments
    }
}


