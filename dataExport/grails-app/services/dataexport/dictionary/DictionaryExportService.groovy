package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultTypeElement
import bard.db.dictionary.StageElement
import dataexport.registration.BardHttpResponse
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.sql.Sql
import groovy.xml.MarkupBuilder

import javax.sql.DataSource
import bard.db.enums.ReadyForExtraction
import bard.db.dictionary.ResultTypeTree

/**
 * Top Level service for handling the
 * generation of dictionary and dictionary elements
 *
 * Please, familiarize yourself with the dictionary.xsd located in the BARD repos
 *
 */
class DictionaryExportService {
    DictionaryExportHelperService dictionaryExportHelperService
    UtilityService utilityService
    DataSource dataSource
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
    public BardHttpResponse update(final Long id, final Long clientVersion, final String latestStatus) {
        final Element element = Element.findById(id)
        return utilityService.update(element, id, clientVersion, latestStatus as ReadyForExtraction, "Element")

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
        final Sql sql = new Sql(dataSource)
        def stageRow = sql.firstRow("SELECT * FROM STAGE_TREE WHERE STAGE_ID=?", [elementId])
        if (stageRow) {
            String parentName = null

            final String label
            Long parentNodeId = stageRow.PARENT_NODE_ID
            if (parentNodeId) {
                def parentRow = sql.firstRow("SELECT STAGE FROM STAGE_TREE WHERE NODE_ID=?", [parentNodeId])
                parentName = parentRow.STAGE
            }
            StageElement stageElement = StageElement.get(stageRow.STAGE_ID)
            Stage stage = new Stage(stageRow, parentName, stageElement?.label)
            this.dictionaryExportHelperService.generateStage(xml, stage)
            return 0
        }

        errorMessage = "Element with id ${elementId} does not exists"
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

        final Sql sql = new Sql(dataSource)

        def resultTypeRow = sql.firstRow("SELECT * FROM RESULT_TYPE_TREE WHERE RESULT_TYPE_ID=? ORDER BY NODE_ID", [elementId])
        if (resultTypeRow) {
            ResultTypeTree resultTypeTree = ResultTypeTree.read(resultTypeRow.NODE_ID)
            dictionaryExportHelperService.generateResultType(xml, resultTypeTree)
            return 0
        }
        errorMessage = "Result Type with element id ${elementId} does not exists"
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