package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultType
import bard.db.dictionary.Stage
import dataexport.registration.BardHttpResponse
import dataexport.registration.UpdateType
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder

import javax.servlet.http.HttpServletResponse

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
     * Set the ReadyForExtraction value on the element to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(final Long id, final Long clientVersion,final String latestStatus) {
        final Element element = Element.findById(id)
        if (!element) { //we could not find the element
            throw new NotFoundException("Element with ID: ${id}, could not be found")
        }
        if (element.version > clientVersion) { //There is a conflict, supplied version is less than the current version
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_CONFLICT, ETag: element.version)
        }
        if (element.version != clientVersion) {//supplied version is not equal to the version in database
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_PRECONDITION_FAILED, ETag: element.version)
        }
        final String currentStatus = element.readyForExtraction
        if (currentStatus != latestStatus) {
            element.readyForExtraction = latestStatus
            element.save(flush: true)
        }
        return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_OK, ETag: element.version)
    }
    /**
     *  Generate a stage element given a stageId
     *  <stage></stage>
     * @param xml
     * @param elementId - This usually is called by the Rest controller.
     * The Id supplied is the Id if an Element, not the id of the Stage
     */
    public Long generateStage(final MarkupBuilder xml, final Long elementId) {
        String errorMessage
        final Element element = Element.get(elementId)
        if (element) {
            final Stage stage = Stage.findByElement(element)
            if (stage) {
                this.dictionaryExportHelperService.generateStage(xml, stage)
                return stage.version
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
    public Long generateResultType(final MarkupBuilder xml, final Long elementId) {

        String errorMessage
        final Element element = Element.get(elementId)
        if (element) {
            final ResultType resultType = ResultType.findByElement(element)
            if (resultType) {
                dictionaryExportHelperService.generateResultType(xml, resultType)

                return resultType.version
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
    public Long generateElement(final MarkupBuilder xml, final Long elementId) {
        final Element element = Element.get(elementId)
        if (element) {
            dictionaryExportHelperService.generateElement(xml, element)
            return element.version
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