package bard.dm.cars.domainspreadsheetmapping

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
class CarsBardMapping {
    Map<Element, String> projectAttributePropertyNameMap
    Map<Element, Map<String, Element>> projectElementValueElementMap

    Map<Element, Map<String, Element>> stepElementValueElementMap

    Map<Element, String> experimentAttributePropertyNameMap
    Map<Element, Map<String, Element>> experimentElementValueElementMap

    CarsBardMapping(String elementFieldMapPath) {
        ConfigObject config = (new ConfigSlurper()).parse(new File(elementFieldMapPath).toURL())

        loadProjectAttributePropertyNameMap(config)
        loadProjectElementValueElementMap(config)

        loadStepElementValueElementMap(config)

//        stepAttributePropertyNameMap(config)

        loadExperimentAttributePropertyNameMap(config)
    }

    private void loadExperimentAttributePropertyNameMap(ConfigObject config) {
        Map<Integer, String> map = config.experiment.attributeMap

        experimentAttributePropertyNameMap = buildAttributeProjectMap(map)
    }

    private void loadExperimentElementValueElementMap(ConfigObject config) {
        Map<Integer, Map<String, Integer>> map = config.experiment.valueMap

        experimentElementValueElementMap = buildElementValueElementMap(map)
    }

    private void loadStepElementValueElementMap(ConfigObject config) {
        Map<Integer, Map<String,Integer>> elementIdValueElementIdMap = config.projectStep.valueMap

        stepElementValueElementMap = buildElementValueElementMap(elementIdValueElementIdMap)
    }

    private void loadProjectElementValueElementMap(ConfigObject config) {
        Map<Integer, Map<String, Integer>> elementIdValueElementIdMap = config.project.valueMap

        projectElementValueElementMap = buildElementValueElementMap(elementIdValueElementIdMap)
    }

    private static Map<Element, Map<String, Element>> buildElementValueElementMap(Map<Integer, Map<String, Integer>> elementIdValueElementIdMap) {
        Map<Element, Map<String, Element>> result = new HashMap<Element, Map<String,Element>>()

        for (Integer attributeElementId : elementIdValueElementIdMap.keySet()) {
            Map<String, Element> valueElementMap = new HashMap<String, Element>()

            Map<String, Integer> valueElementIdMap = elementIdValueElementIdMap.get(attributeElementId)
            for (String carsValue : valueElementIdMap.keySet()) {
                Integer valueElementId = valueElementIdMap.get(carsValue)

                Element valueElement = Element.findById(valueElementId)

                valueElementMap.put(carsValue.trim().toLowerCase(), valueElement)
            }

            Element attributeElement = Element.findById(attributeElementId)
            result.put(attributeElement, valueElementMap)
        }

        return result
    }

    private void stepAttributePropertyNameMap(ConfigObject config) {
        Map<Integer, String> elementIdPropertyNameMap = config.projectStep.attributeMap

//        stepAttributePropertyNameMap = buildAttributeProjectMap(elementIdPropertyNameMap)
    }

    private void loadProjectAttributePropertyNameMap(ConfigObject config) {
        Map<Integer, String> elementIdPropertyNameMap = config.project.attributeMap

        projectAttributePropertyNameMap = buildAttributeProjectMap(elementIdPropertyNameMap)
    }

    private static Map<Element, String> buildAttributeProjectMap(Map<Integer, String> elementIdPropertyNameMap) {
        Map<Element, String> result = new HashMap<Element, String>()

        for (Integer elementId : elementIdPropertyNameMap.keySet()) {
            Element element = Element.findById(elementId)
            result.put(element, elementIdPropertyNameMap.get(elementId))
        }

        return result
    }

    void printProjectElementValueElementMap() {
        for (Element attributeElement : projectElementValueElementMap.keySet()) {
            println(attributeElement.label + " " + attributeElement.id)

            Map<String, Element> map = projectElementValueElementMap.get(attributeElement)
            for (String carsValue : map.keySet()) {
                Element valueElement = map.get(carsValue)
                println(carsValue + " " + valueElement.id + " " + valueElement.label)
            }
        }
    }
}