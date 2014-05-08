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

import bard.db.ContextItemService
import bard.db.ContextService
import bard.db.context.item.BasicContextItemCommand
import bard.db.dictionary.Element
import bard.db.enums.Status
import bard.db.project.ExperimentCommand
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


SpringSecurityUtils.reauthenticate('dlahr', null)

def experimentController = ctx.getBean("bard.db.project.ExperimentController")
assert experimentController, "Could not find ExperimentController"

ContextService contextService = ctx.getBean("contextService")
assert contextService, "Could not find ContextService"

ContextItemService contextItemService = ctx.getBean("contextItemService")
assert contextItemService, "Could not find ContextItemService"



List<Experiment> experiments = Experiment.findAllByExperimentStatusNotInList([Status.RETIRED])
int totalExperiments = experiments.size()
int counter = 1
for (Experiment experiment : experiments) {
     if (!ExperimentContext.findByContextNameAndExperiment('Substance Identifier', experiment)) {

        //if this experiment already ha this context skip it
        final ExperimentContext context =
                (ExperimentContext) contextService.createExperimentContext(experiment.id, experiment, ExperimentCommand.SUBSTANCE_CARD_NAME,
                        bard.db.enums.ContextType.UNCLASSIFIED)
        //add context Item
        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand()
        basicContextItemCommand.contextItemService = contextItemService
        basicContextItemCommand.context = context
        basicContextItemCommand.contextId = context?.id
        basicContextItemCommand.contextClass = "ExperimentContext"

        final Element attributeElement = Element.findByIdOrLabel(ExperimentCommand.SUBSTANCE_IDENTIFIER_ATTRIBUTE_ELEMENT_ID,
                ExperimentCommand.SUBSTANCE_IDENTIFIER_ATTRIBUTE_ELEMENT_LABEL)
        basicContextItemCommand.attributeElementId = attributeElement.id

        Element substanceElementValue = Element.findByIdOrLabel(ExperimentCommand.PUBCHEM_SUBSTANCE_IDENTIFIER_VALUE_ID,
                ExperimentCommand.PUBCHEM_SUBSTANCE_IDENTIFIER_VALUE_LABEL)

        basicContextItemCommand.valueElementId = substanceElementValue.id

        boolean success = contextItemService.createExperimentContextItem(experiment.id, basicContextItemCommand)
        // boolean success = basicContextItemCommand.createNewContextItem()

        if (!success) {
            println "Could not create context for " + experiment.id
        }

    }
    if(counter % 100 == 0){
        ctx.getBean('sessionFactory').currentSession.flush()
        println "Created contexts for " + counter + " out of ${totalExperiments} experiments"
    }
    ++counter
}