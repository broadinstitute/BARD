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
    void removeInvalid(List<ContextDTO> contextDTOList, Map attributeNameMapping) {

        Iterator<ContextDTO> contextDTOIterator = contextDTOList.iterator()
        while (contextDTOIterator.hasNext()) {
            ContextDTO contextDTO = contextDTOIterator.next()

            boolean isValid = true;

            for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {

                //have deliberately placed result as the second operand so that the checkForElement method
                //is run so that all elements are checked
                isValid  = checkForElement(contextItemDto.key, contextDTO) && isValid

                //Check that the value is defined in the database,
                // except for the ones that are numeric values or a type-in field or a Free-type field
                if (contextItemDto.value &&
                        (contextItemDto.value instanceof String) &&
                        !contextItemDto.typeIn &&
                        (contextItemDto.attributeType != AttributeType.Free)) {

                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    isValid  = checkForElement(contextItemDto.value, contextDTO) && isValid
                }

                //If concentration units are present check that they are defined in the database
                if (contextItemDto.concentrationUnits != null) {
                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    isValid  = checkForElement(contextItemDto.concentrationUnits, contextDTO) && isValid
                }
            }

            if (!isValid) {
                contextDTOIterator.remove()
            }
        }
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


