package bard.dm.cars.domainspreadsheetmapping

import bard.db.dictionary.Element
import bard.dm.Log
import bard.db.experiment.StepContextItem
import bard.dm.cars.spreadsheet.CarsExperiment

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/5/12
 * Time: 6:52 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectStepAnnotater {
    private CarsBardMapping carsBardMapping

    private String username

    private static final int assayStageElementId = 556

    private Element assayStageElement
    private Map<String, Element> carsElementMap

    /**
     * @param carsBardMapping
     * @param username for use when creating domain objects - goes in "modified by" field
     */
    ProjectStepAnnotater(CarsBardMapping carsBardMapping, String username) {
        this.carsBardMapping = carsBardMapping
        this.username = username

        assayStageElement = Element.findById(assayStageElementId)

        carsElementMap = carsBardMapping.stepElementValueElementMap.get(assayStageElement)
    }

    void addAnnotations(Collection<ProjectPair> projectPairColl) {
        Log.logger.info("add annotations to Project Steps")

        projectPairColl.each {ProjectPair projectPair ->
            Log.logger.info("\tproject id: " + projectPair.project.id + " uid: " + projectPair.carsProject.projectUid)

            projectPair.projectStepPairSet.each {ProjectStepPair projectStepPair ->
                Log.logger.info("\t\t project step id: " + projectStepPair.projectStep.id + " aid: " + projectStepPair.experimentPair.carsExperiment.aid)

                boolean foundAssayStageElement = false
                Iterator<StepContextItem> iter = projectStepPair.projectStep.stepContextItems.iterator()
                while (!foundAssayStageElement && iter.hasNext()) {
                    StepContextItem stepContextItem = iter.next()

                    if (stepContextItem.attributeElement.id == assayStageElement.id) {
                        foundAssayStageElement = true
                    }
                }

                if (!foundAssayStageElement) {
                    Log.logger.info("\t\t\tattempting to add assay stage annotation ")
                    if (projectStepPair.experimentPair.carsExperiment) {
                        CarsExperiment carsExperiment = projectStepPair.experimentPair.carsExperiment

                        String value = null
                        if (carsElementMap.containsKey(carsExperiment.assaySubtype.toLowerCase())) {
                            value = carsExperiment.assaySubtype.toLowerCase()
                        } else if (carsElementMap.containsKey(carsExperiment.assayTarget.toLowerCase())) {
                            value = carsExperiment.assayTarget.toLowerCase()
                        }

                        if (value) {
                            StepContextItem stepContextItem = new StepContextItem(dateCreated: (new Date()),
                                    modifiedBy: username)
                            stepContextItem.projectStep = projectStepPair.projectStep
                            stepContextItem.attributeElement = assayStageElement
                            stepContextItem.valueElement = carsElementMap.get(value)
                            stepContextItem.valueDisplay = stepContextItem.valueElement.label
                            assert stepContextItem.save()
                            Log.logger.info("\t\t\t\tmapping found - annotation is: " + stepContextItem.valueDisplay)

                            projectStepPair.projectStep.stepContextItems.add(stepContextItem)
                        }
                    }
                } else {
                    Log.logger.info("\t\t\tassay stage already mapped")
                }
            }
        }
    }
}
