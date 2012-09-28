package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import bard.dm.Log
import bard.dm.minimumassayannotation.Attribute
import bard.dm.minimumassayannotation.ContextDTO

/**
 * Make sure that all the attribute's key and value match against valid values in the Element table
 */
class AttributeContentAgainstElementTableValidator {
    /**
     * Make sure that all the attribute's key and value match against valid values in the Element table
     *
     * @param assayContextList
     */
    void validate(List<ContextDTO> assayContextList, Map attributeNameMapping) {
        //Move all the attributes into a sorted-set to search against the database
        SortedSet<String> attributeVocabulary = [] as SortedSet
        assayContextList.each {ContextDTO assayContextDTO ->
            assayContextDTO.attributes.each {Attribute attribute ->
                //Add all keys
                attributeVocabulary.add(attribute.key)
                //Add all the values, except for the ones that are numeric values or a type-in field or a Free-type field
                if (attribute.value &&
                        (attribute.value instanceof String) &&
                        !attribute.typeIn &&
                        (attribute.attributeType != AttributeType.Free)) {
                    attributeVocabulary.add(attribute.value)
                }
                //Add the concentration-units if exists
                if (attribute.concentrationUnits) {
                    attributeVocabulary.add(attribute.concentrationUnits)
                }
            }
        }

        List<Element> foundElements = []
        List<String> missingAttributes = []
        attributeVocabulary.each {String attr ->
//            Log.logger.info("Attribute: '${attr}'")
            //Swap the attribute name with the mapping we have (e.g., '[detector] assay component (type in)' --> 'assay component'
//            attr = attributeNameMapping.containsKey(attr) ? attributeNameMapping.get(attr) : attr
            Element element = Element.findByLabelIlike(attr)
            //    Log.logger.info("Element: ${element}")
            if (element) {
                foundElements << element
            }
            else {
                missingAttributes << attr
            }
        }


        Log.logger.info("Found elements: ${foundElements.collect {Element element -> [element.id, element.label]}}")
        Log.logger.error("Missing attributes: ${missingAttributes}")
        assert missingAttributes.isEmpty(), "We could not have missing attributes - all attributes should be validatied against the Element table"
    }
}
