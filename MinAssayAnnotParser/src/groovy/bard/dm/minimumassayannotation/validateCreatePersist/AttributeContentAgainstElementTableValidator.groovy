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

        boolean blankContextDto = false
        Long prevAid = null

        for (ContextDTO contextDTO : contextDTOList) {
            if (contextDTO != null) {
                for (ContextItemDto contextItemDto : contextDTO.attributes) {
                    if (! checkForElement(contextItemDto.key, contextDTO.aid, contextDTO.name)) {
                        return false
                    }

                    //Check that the value is defined in the database,
                    // except for the ones that are numeric values or a type-in field or a Free-type field
                    if (contextItemDto.value &&
                            (contextItemDto.value instanceof String) &&
                            !contextItemDto.typeIn &&
                            (contextItemDto.attributeType != AttributeType.Free)) {

                        if (! checkForElement(contextItemDto.value, contextDTO.aid, contextDTO.name)) {
                            return false
                        }

                    }

                    //If concentration units are present check that they are defined in the database
                    if (contextItemDto.concentrationUnits) {
                        if (! checkForElement(contextItemDto.concentrationUnits, contextDTO.aid, contextDTO.name)) {
                            return false
                        }
                    }
                }

                prevAid = contextDTO.aid
            } else {
                Log.logger.error("null contextDTO found.  Previous non-null aid: $prevAid")
                blankContextDto = true
            }

            if (blankContextDto) {
                Log.logger.error("null contextDTO found.  Subsequent non-null aid: ${contextDTO?.aid}")
                blankContextDto = false
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
            loadResultsWriter.write(aid, null, assayContextDtoName, ContextLoadResultsWriter.LoadResultType.fail, message)

            return false
        }
    }
}


