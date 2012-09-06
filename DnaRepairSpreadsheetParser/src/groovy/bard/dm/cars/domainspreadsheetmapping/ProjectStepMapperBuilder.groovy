package bard.dm.cars.domainspreadsheetmapping

import bard.dm.Log
import bard.db.experiment.Experiment
import bard.db.experiment.ProjectStep
import bard.db.registration.ExternalReference
import bard.db.experiment.StepContextItem
import bard.db.dictionary.Element
import bard.dm.cars.spreadsheet.CarsExperiment
import bard.db.experiment.Project

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/4/12
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */

class ProjectStepMapperBuilder {
    private CarsBardMapping carsBardMapping

    private String username

    /**
     * @param carsBardMapping
     * @param username for use when creating domain objects - goes in "modified by" field
     */
    ProjectStepMapperBuilder(CarsBardMapping carsBardMapping, String username) {
        this.carsBardMapping = carsBardMapping
        this.username = username
    }

    void mapOrBuildProjectSteps(Collection<ProjectPair> projectPairColl) {
        Log.logger.info("map or build project steps")

        projectPairColl.each {ProjectPair projectPair ->
            Log.logger.info("\tproject uid: " + projectPair.carsProject.projectUid + " id: " + projectPair.project.id)

            Map<Experiment, Set<ExperimentPair>> dbPairExperimentMap = buildDbPairExperimentMap(projectPair.experimentPairList)

            projectPair.project.projectSteps.each {ProjectStep projectStep ->
                if (dbPairExperimentMap.containsKey(projectStep.experiment))  {

                    Set<ExperimentPair> experimentPairSet = dbPairExperimentMap.get(projectStep.experiment)

                    ExperimentPair foundMatch = findOneExperimentThatMatchesProjectStep(projectStep, experimentPairSet)
                    if (foundMatch) {
                        projectPair.projectStepPairSet.add(new ProjectStepPair(experimentPair: foundMatch, projectStep: projectStep))
                        experimentPairSet.remove(foundMatch)
                    }
                }
            }

            dbPairExperimentMap.keySet().each {Experiment experiment ->
                Set<ExperimentPair> experimentPairSet = dbPairExperimentMap.get(experiment)

                StringBuilder builder = new StringBuilder()
                experimentPairSet.each {ExperimentPair experimentPair ->
                    ProjectStep projectStep = createProjectStep(projectPair.project, experiment)
                    projectPair.project.projectSteps.add(projectStep)
                    experiment.projectSteps.add(projectStep)

                    ProjectStepPair projectStepPair = new ProjectStepPair(projectStep: projectStep, experimentPair: experimentPair)
                    projectPair.projectStepPairSet.add(projectStepPair)

                    builder.append(experimentPair.carsExperiment.aid).append(" ")
                }

                Log.logger.info("\t\tno project step found for db experiment - will add id: " + experiment.id + " aid(s):" + builder.toString())
            }

            projectPair.unmatchedExternalReferences.each {ExternalReference extRef ->
                if (extRef.experiment) {
                    Log.logger.info("\t\tunmatched external reference contains an experiment so a project step is being created linking it to the project.  Note: will not be annotated from CARS (no matching CARS experiment).  experiment id: " + extRef.experiment.id)
                    ProjectStep projectStep = createProjectStep(projectPair.project, extRef.experiment)
                    projectPair.project.projectSteps.add(projectStep)
                    extRef.experiment.projectSteps.add(projectStep)
                }
            }
        }
    }

    private ProjectStep createProjectStep(Project project, Experiment experiment) {
        ProjectStep result =  new ProjectStep(project: project, experiment: experiment, dateCreated: (new Date()),
                modifiedBy: username)
        assert result.save()
        return result
    }

    /**
     * attempt to find cars experiment that matches provided projectStep, based on annotations
     * @param projectStep
     * @param remainingCarsExperiments
     * @return matching cars experiment, null if none found
     */
    private ExperimentPair findOneExperimentThatMatchesProjectStep(ProjectStep projectStep, Set<ExperimentPair> experimentPairSet) {
        experimentPairSet.each {ExperimentPair experimentPair ->
            projectStep.stepContextItems.each {StepContextItem stepContextItem ->
                Element matchingAttributeElement = stepContextItem.attributeElement
                String carsMatchingPropertyName = carsBardMapping.experimentAttributePropertyNameMap.get(matchingAttributeElement)

                if (carsMatchingPropertyName) { //only can attempt to match if it is mapped
                    if (stepContextItem.valueElement != null) { //match values that are elements

                        if (carsBardMapping.stepElementValueElementMap.containsKey(matchingAttributeElement)) {
                            String carsValue = experimentPair.carsExperiment.getProperty(carsMatchingPropertyName)
                            Element carsValueElement = carsBardMapping.stepElementValueElementMap.get(matchingAttributeElement).get(carsValue)
                            if (carsValueElement != null && carsValueElement.id == stepContextItem.valueElement.id) {
                                return experimentPair
                            }
                        }
                    } else { //match values that are not elements
                        if (!carsBardMapping.stepElementValueElementMap.containsKey(matchingAttributeElement)) { //only match if it is not an element in CARS
                            if (stepContextItem.valueDisplay?.equalsIgnoreCase(experimentPair.carsExperiment.getProperty(carsMatchingPropertyName).toString())) {
                                return experimentPair
                            }
                        } else {
                            Log.logger.warn("\t\tmatching step to cars experiment, cars experiment does not have value element but cars does - no match made")
                        }
                    }
                }
            }
        }

        return null
    }

//    private void addStepContext(ProjectStep projectStep, CarsExperiment carsExperiment) {
//        Set<Element> attributeSet = new HashSet<Element>(carsBardMapping.stepAttributePropertyNameMap.keySet())
//
//        projectStep.stepContextItems.each {StepContextItem stepContextItem ->
//            if (attributeSet.remove(stepContextItem.attributeElement)) {
//                Log.logger.info("\t\t\tproject step context already present for attribute " + stepContextItem.attributeElement.id + " " + stepContextItem.attributeElement.label)
//            }
//        }
//
//        attributeSet.each {Element attributeElement ->
//            Log.logger.info("\t\t\tadding project step context for attribute " + attributeElement.id + " " + attributeElement.label)
//
//            String carsPropertyName = carsBardMapping.stepAttributePropertyNameMap.get(attributeElement)
//            if (carsPropertyName) {
//                String carsValue = carsExperiment.getProperty(carsPropertyName)
//                carsValue = carsValue.toLowerCase()
//
//                Map<String, Element> valueElementMap = carsBardMapping.stepElementValueElementMap.get(attributeElement)
//
//                if (valueElementMap) {
//                    Log.logger.info("\t\t\t\tattempting to use mapping between CARS values and ontology elements for attribute: " + attributeElement.id + " " + attributeElement.label + " CARS property: " + carsPropertyName)
//
//                    if (valueElementMap.containsKey(carsValue)) {
//                        StepContextItem stepContextItem = new StepContextItem()
//                        stepContextItem.attributeElement = attributeElement
//                        stepContextItem.valueElement = valueElementMap.get(carsValue)
//                        projectStep.stepContextItems.add(stepContextItem)
//                    } else {
//                        Log.logger.info("\t\t\t\tCARS value that should be mapped to an element is not - attribute: " + attributeElement.id + " " + attributeElement.label + " CARS value: " + carsValue)
//                    }
//                } else {
//                    //TODO code for handling unmapped but recorded
//                }
//            }
//        }
//    }

    private static Map<Experiment, Set<ExperimentPair>> buildDbPairExperimentMap(Collection<ExperimentPair> experimentPairColl) {
        Map<Experiment, Set<ExperimentPair>> result = new HashMap<Experiment, Set<ExperimentPair>>()

        experimentPairColl.each {ExperimentPair experimentPair ->
            if (experimentPair.experiment) {
                Set<ExperimentPair> experimentPairSet = result.get(experimentPair.experiment)
                if (null == experimentPairSet) {
                    experimentPairSet = new HashSet<ExperimentPair>()
                    result.put(experimentPair.experiment, experimentPairSet)
                }

                experimentPairSet.add(experimentPair)
            }
        }

        return result
    }

}

