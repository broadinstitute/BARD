package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import bard.dm.Log
import bard.dm.minimumassayannotation.Attribute
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.LoadResultsWriter

/**
 * Make sure that all the attribute's key and value match against valid values in the Element table
 */
class AttributeContentAgainstElementTableValidator {

    private final LoadResultsWriter loadResultsWriter

    public AttributeContentAgainstElementTableValidator(LoadResultsWriter loadResultsWriter) {
        this.loadResultsWriter = loadResultsWriter
    }

    /**
     * Make sure that all the attribute's key and value match against valid values in the Element table
     *
     * @param assayContextList
     */
    boolean validate(List<ContextDTO> assayContextList, Map attributeNameMapping) {

        assayContextList.each {ContextDTO assayContextDTO ->
            assayContextDTO.attributes.each {Attribute attribute ->

                if (! checkForElement(attribute.key, assayContextDTO.aid, assayContextDTO.name)) {
                    return false
                }

                //Check that the value is defined in the database,
                // except for the ones that are numeric values or a type-in field or a Free-type field
                if (attribute.value &&
                        (attribute.value instanceof String) &&
                        !attribute.typeIn &&
                        (attribute.attributeType != AttributeType.Free)) {

                    if (! checkForElement(attribute.value, assayContextDTO.aid, assayContextDTO.name)) {
                        return false
                    }

                }

                //If concentration units are present check that they are defined in the database
                if (attribute.concentrationUnits) {
                    if (! checkForElement(attribute.concentrationUnits, assayContextDTO.aid, assayContextDTO.name)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    private boolean checkForElement(String elementLabel, Long aid, String assayContextDtoName) {
        Element element = Element.findByLabelIlike(elementLabel)

        if (element) {
            return true;
        } else {
            final String message = "Attributes in context not found in database:  $elementLabel"
            Log.logger.error(message)
            loadResultsWriter.write(aid, null, assayContextDtoName, LoadResultsWriter.LoadResultType.fail, message)

            return false
        }
    }
}


