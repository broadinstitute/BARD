package barddataexport.dictionary

import groovy.sql.Sql
import groovy.xml.MarkupBuilder

/**
 * Top Level service for handling the
 * generation of dictionary and dictionary elements
 *
 * Please, familiarize yourself with the dictionary.xsd located in the BARD repos
 *
 */
class DictionaryExportService {
    DictionaryExportHelperService dictionaryExportHelperService

    /**
     *  Generate a stage element given a stageId
     *  <stage></stage>
     * @param xml
     * @param stageId
     */
    public void generateStage(final MarkupBuilder xml, final BigDecimal stageId) {
        this.dictionaryExportHelperService.generateStage(xml, stageId)
        //validate stage
    }

    /**
     *  Generate a resultType given a resultTypeId
     *   <resultType></resultType>
     * @param xml
     * @param resultTypeId
     */
    public void generateResultType(final MarkupBuilder xml, final BigDecimal resultTypeId) {
        dictionaryExportHelperService.generateResultType(xml, resultTypeId)
        //validate result type
    }

    /**
     * Generate an element from an elementId
     * <element elementId=""></element>
     * @param xml
     * @param elementId
     */
    public void generateElementWithElementId(final MarkupBuilder xml, final BigDecimal elementId) {
        dictionaryExportHelperService.generateElementWithElementId(xml, elementId)
        //validate element
    }
    /**
     *  Generate element from a dto
     * @param xml
     * @param elementDTO
     *
     * <element></element>
     */
    public void generateElement(final MarkupBuilder xml, final ElementDTO elementDTO) {
        dictionaryExportHelperService.generateElement(xml, elementDTO)
        //validate element
    }
    /**
     * Root node for generating the entire dictionary
     */
    public void generateDictionary(final MarkupBuilder xml) {
        dictionaryExportHelperService.generateDictionary(xml)
        //validate dictionary
    }
}

class ElementDTO {
    final BigDecimal elementId
    final String label
    final String description
    final String abbreviation
    final String synonyms
    final String externalUrl
    final String unit
    final String elementStatus
    final String readyForExtraction

    ElementDTO(final BigDecimal elementId,
               final String label,
               final String description,
               final String abbreviation,
               final String synonyms,
               final String externalUrl,
               final String unit,
               final String elementStatus,
               final String readyForExtraction) {
        this.readyForExtraction = readyForExtraction
        this.elementId = elementId
        this.label = label
        this.description = description
        this.abbreviation = abbreviation
        this.synonyms = synonyms
        this.externalUrl = externalUrl
        this.unit = unit
        this.elementStatus = elementStatus
    }
}