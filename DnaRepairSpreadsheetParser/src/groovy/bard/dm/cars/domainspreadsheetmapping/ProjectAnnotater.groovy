package bard.dm.cars.domainspreadsheetmapping

import bard.dm.Log
import bard.db.dictionary.Element
import bard.db.experiment.ProjectContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/3/12
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectAnnotater {
    private CarsBardMapping carsBardMapping

    private String username

    /**
     * @param carsBardMapping
     * @param username for use when creating domain objects - goes in "modified by" field
     */
    ProjectAnnotater(CarsBardMapping carsBardMapping, String username) {
        this.carsBardMapping = carsBardMapping
        this.username = username
    }

    void addAnnotations(Collection<ProjectPair> projectPairColl) {
        Log.logger.info("add annotations to projects")

        projectPairColl.each {ProjectPair projectPair ->
            Log.logger.info("\tproject uid: " + projectPair.carsProject.projectUid + " project id: " + projectPair.project.id)

            Set<Element> carsAttributeSet = new HashSet<Element>(carsBardMapping.projectAttributePropertyNameMap.keySet())

            projectPair.project.projectContextItems.each {ProjectContextItem projectContextItem ->
                if (carsAttributeSet.remove(projectContextItem.attributeElement)) {
                    Log.logger.info("\t\tannotation already present for " + projectContextItem.attributeElement.id + " " + projectContextItem.attributeElement.label)
                }
            }

            carsAttributeSet.each {Element attribute ->
                Log.logger.info("\t\tadding annotation for " + attribute.id + " " + attribute.label)

                ProjectContextItem projectContextItem = new ProjectContextItem(dateCreated: (new Date()),
                        modifiedBy: username)
                projectContextItem.attributeElement = attribute
                projectContextItem.project = projectPair.project
                assert projectContextItem.save()

                projectPair.project.projectContextItems.add(projectContextItem)

                String carsProperty = carsBardMapping.projectAttributePropertyNameMap.get(attribute)
                String carsValue = projectPair.carsProject.getProperty(carsProperty)

                Map<String, Element> valueElementMap = carsBardMapping.projectElementValueElementMap.get(attribute)
                if (valueElementMap != null) {
                    Log.logger.info("\t\t\tannotation is mapped")

                    Element valueElement = valueElementMap.get(carsValue.toLowerCase())

                    if (valueElement) {
                        projectContextItem.valueElement = valueElement
                        projectContextItem.valueDisplay = valueElement.label
                    } else {
                        Log.logger.info("\t\t\t\tcars to bard ontology mapping expected but not found - attribute: " + attribute.id + " cars value: " + carsValue)
                    }
                } else {
                    Log.logger.info("\t\t\tannotation is not mapped, takes value directly")

                    projectContextItem.valueDisplay = carsValue
                }
            }
        }
    }
}
