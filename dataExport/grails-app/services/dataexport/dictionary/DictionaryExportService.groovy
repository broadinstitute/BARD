package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultTypeElement
import dataexport.registration.BardHttpResponse
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.sql.Sql
import groovy.xml.MarkupBuilder

import javax.sql.DataSource
import bard.db.dictionary.StageElement

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
        return utilityService.update(element, id, clientVersion, latestStatus, "Element")

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
        sql.eachRow('SELECT * FROM STAGE_TREE WHERE STAGE_ID=:STAGEID', [STAGEID: elementId]) { stageRow ->

            String parentName = null

            final String label
            Long parentNodeId = stageRow.PARENT_NODE_ID
            if (parentNodeId) {
                sql.eachRow('SELECT STAGE FROM STAGE_TREE WHERE NODE_ID=:parentNode', [parentNode: parentNodeId]) { parentRow ->
                    parentName = parentRow.STAGE
                }
            }
            StageElement stageElement = StageElement.get(stageRow.STAGE_ID)
            Stage stage = new Stage(stageRow,parentName, stageElement?.label)
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
        sql.eachRow('SELECT * FROM RESULT_TYPE_TREE WHERE RESULT_TYPE_ID=:RESULT_TYPEID', [RESULT_TYPEID: elementId]) { resultTypeRow ->
            Long parentNodeId = resultTypeRow.PARENT_NODE_ID
            String parentResultTypeName = null
            if (parentNodeId) {
                sql.eachRow('SELECT RESULT_TYPE_NAME FROM RESULT_TYPE_TREE WHERE NODE_ID=:parentNode', [parentNode: parentNodeId]) { parentRow ->
                    parentResultTypeName = parentRow.parentRow.RESULT_TYPE_NAME
                }
            }
            ResultTypeElement resultTypeElement = ResultTypeElement.get(resultTypeRow.RESULT_TYPE_ID)
            ResultType resultType = new ResultType(resultTypeRow, parentResultTypeName, resultTypeElement?.label)
            dictionaryExportHelperService.generateResultType(xml, resultType)
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
public class ResultType {
   String resultTypeName
   String description
   String synonyms
   Long resultTypeId
   String abbreviation
   String baseUnit
   String resultTypeStatus
   String parentResultTypeName
   String resultTypeLabel

    public ResultType(){

    }
    public ResultType(row, final String parentResultTypeName, final String resultTypeLabel) {
        this.parentResultTypeName = parentResultTypeName
        this.resultTypeLabel = resultTypeLabel
        this.resultTypeId = row.RESULT_TYPE_ID
        this.resultTypeStatus = row.RESULT_TYPE_STATUS
        this.resultTypeName = row.RESULT_TYPE_NAME
        this.description = row.DESCRIPTION
        this.abbreviation = row.ABBREVIATION
        this.synonyms = row.SYNONYMS
        this.baseUnit = row.BASE_UNIT
    }

}