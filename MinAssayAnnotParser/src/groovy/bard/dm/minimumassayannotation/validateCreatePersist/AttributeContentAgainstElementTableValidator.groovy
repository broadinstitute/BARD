package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import bard.dm.Log
import bard.dm.minimumassayannotation.ContextItemDto
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ContextLoadResultsWriter

/**
 * Make sure that all the attribute's key and value match against valid values in the Element table
 */
class AttributeContentAgainstElementTableValidator {

    private final ContextLoadResultsWriter loadResultsWriter

    public AttributeContentAgainstElementTableValidator(ContextLoadResultsWriter loadResultsWriter) {
        this.loadResultsWriter = loadResultsWriter
    }

    /**
     * Make sure that all the attribute's key and value match against valid values in the Element table
     *
     * @param contextList
     */
    boolean validate(List<ContextDTO> contextDTOList, Map attributeNameMapping) {

        boolean result = true;
        for (ContextDTO contextDTO : contextDTOList) {
            for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {

                //have deliberately placed result as the second operand so that the checkForElement method
                //is run so that all elements are checked
                result = checkForElement(contextItemDto.key, contextDTO) && result

                //Check that the value is defined in the database,
                // except for the ones that are numeric values or a type-in field or a Free-type field
                if (contextItemDto.value &&
                        (contextItemDto.value instanceof String) &&
                        !contextItemDto.typeIn &&
                        (contextItemDto.attributeType != AttributeType.Free)) {

                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    result = checkForElement(contextItemDto.value, contextDTO) && result
                }

                //If concentration units are present check that they are defined in the database
                if (contextItemDto.concentrationUnits != null) {
                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    result = checkForElement(contextItemDto.concentrationUnits, contextDTO) && result
                }
            }
        }

        return result
    }

    private boolean checkForElement(String elementLabel, ContextDTO contextDTO) {
        Element element = Element.findByLabelIlike(elementLabel)

        if (element) {
            return true;
        } else {
            final String message = "Attributes in context not found in database:  $elementLabel"
            Log.logger.error(message)
            loadResultsWriter.write(contextDTO, null, ContextLoadResultsWriter.LoadResultType.fail, null, 0, message)

            return false
        }
    }
}


