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
