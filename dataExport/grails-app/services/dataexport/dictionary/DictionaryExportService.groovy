package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultType
import bard.db.dictionary.Stage
import exceptions.NotFoundException
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
     * @param elementId - This usually is called by the Rest controller.
     * The Id supplied is the Id if an Element, not the id of the Stage
     */
    public void generateStage(final MarkupBuilder xml, final Long elementId) {
        String errorMessage
        final Element element = Element.get(elementId)
        if (element) {
            final Stage stage = Stage.findByElement(element)
            if (stage) {
                this.dictionaryExportHelperService.generateStage(xml, stage)
                return
            } else {
                errorMessage = "Stage with element id ${elementId} does not exists"
            }
        } else {
            errorMessage = "Element with id ${elementId} does not exists"
        }
        log.error(errorMessage)
        throw new NotFoundException(errorMessage)
    }

    /**
     *  Generate a resultType given a resultTypeId
     *   <resultType></resultType>
     * @param xml
     * @param elementId - This usually is called by the Rest controller.
     * The Id supplied is the Id if an Element, not the id of the ResultType
     */
    public void generateResultType(final MarkupBuilder xml, final Long elementId) {

        String errorMessage
        final Element element = Element.get(elementId)
        if (element) {
            final ResultType resultType = ResultType.findByElement(element)
            if (resultType) {
                dictionaryExportHelperService.generateResultType(xml, resultType)
                return
            } else {
                errorMessage = "Result Type with element id ${elementId} does not exists"
            }
        } else {
            errorMessage = "Element with id ${elementId} does not exists"
        }
        log.error(errorMessage)
        throw new NotFoundException(errorMessage)
    }

    /**
     * Generate an element from an elementId
     * <element elementId=""></element>
     * @param xml
     * @param elementId - This usually is called by the Rest controller.
     */
    public void generateElement(final MarkupBuilder xml, final Long elementId) {
        final Element element = Element.get(elementId)
        if (element) {
            dictionaryExportHelperService.generateElement(xml, element)
            return
        }
        String errorMessage = "Element with id ${elementId} does not exists"
        log.error(errorMessage)
        throw new NotFoundException(errorMessage)
    }
    /**
     * Root node for generating the entire dictionary
     */
    public void generateDictionary(final MarkupBuilder xml) {
        dictionaryExportHelperService.generateDictionary(xml)
    }
}