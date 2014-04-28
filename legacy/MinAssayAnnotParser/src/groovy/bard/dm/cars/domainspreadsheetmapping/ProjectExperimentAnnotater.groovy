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

package bard.dm.cars.domainspreadsheetmapping

import bard.db.dictionary.Element
import bard.dm.Log
import bard.db.project.StepContextItem
import bard.dm.cars.spreadsheet.CarsExperiment
import bard.db.project.ProjectExperimentContext
import bard.db.project.ProjectExperimentContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/5/12
 * Time: 6:52 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperimentAnnotater {
    private CarsBardMapping carsBardMapping

    private String username

    private static final int assayStageElementId = 556

    private Element assayStageElement
    private Map<String, Element> carsElementMap

    private static final String contextName = "assay stage"

    /**
     * @param carsBardMapping
     * @param username for use when creating domain objects - goes in "modified by" field
     */
    ProjectExperimentAnnotater(CarsBardMapping carsBardMapping, String username) {
        this.carsBardMapping = carsBardMapping
        this.username = username

        assayStageElement = Element.findById(assayStageElementId)

        carsElementMap = carsBardMapping.stepElementValueElementMap.get(assayStageElement)
    }

    void addAnnotations(Collection<ProjectPair> projectPairColl) {
        Log.logger.info("add annotations to Project Steps")

        for (ProjectPair projectPair : projectPairColl) {
            Log.logger.info("\tproject id: " + projectPair.project.id + " uid: " + projectPair.carsProject.projectUid)

            for (ProjectExperimentPair projectExperimentPair : projectPair.projectExperimentPairSet) {
                Log.logger.info("\t\t project step id: " + projectExperimentPair.projectExperiment.id + " aid: " + projectExperimentPair.experimentPair.carsExperiment.aid)

                boolean foundAssayStageElement = false

                for (ProjectExperimentContext projectExperimentContext : projectExperimentPair.projectExperiment.contexts) {
                    for (ProjectExperimentContextItem projectExperimentContextItem : projectExperimentContext) {
                        if (projectExperimentContextItem.attributeElement.id == assayStageElement.id) {
                            foundAssayStageElement = true
                        }
                    }
                }


                if (!foundAssayStageElement) {
                    Log.logger.info("\t\t\tattempting to add assay stage annotation ")
                    if (projectExperimentPair.experimentPair.carsExperiment) {
                        CarsExperiment carsExperiment = projectExperimentPair.experimentPair.carsExperiment

                        String value = null
                        if (carsElementMap.containsKey(carsExperiment.assaySubtype.toLowerCase())) {
                            value = carsExperiment.assaySubtype.toLowerCase()
                        } else if (carsElementMap.containsKey(carsExperiment.assayTarget.toLowerCase())) {
                            value = carsExperiment.assayTarget.toLowerCase()
                        }

                        if (value) {
                            ProjectExperimentContext projectExperimentContext =
                                new ProjectExperimentContext(projectExperiment: projectExperimentPair.projectExperiment,
                                contextName: contextName, dateCreated: new Date(), modifiedBy: username)
                            projectExperimentPair.projectExperiment.contexts.add(projectExperimentContext)
                            assert projectExperimentContext.save()

                            Element valueElement = carsElementMap.get(value)
                            ProjectExperimentContextItem projectExperimentContextItem =
                                new ProjectExperimentContextItem(context: projectExperimentContext,
                                        attributeElement: assayStageElement, valueElement: valueElement,
                                        valueDisplay: valueElement.label, dateCreated: new Date(), modifiedBy: username)
                            projectExperimentContext.contextItems.add(projectExperimentContextItem)
                            assert projectExperimentContextItem.save()

                            Log.logger.info("\t\t\t\tmapping found - annotation is: " + valueElement.label)
                        }
                    }
                } else {
                    Log.logger.info("\t\t\tassay stage already mapped")
                }
            }
        }
    }
}
