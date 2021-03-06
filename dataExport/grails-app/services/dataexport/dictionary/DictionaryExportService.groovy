/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultTypeTree
import bard.db.dictionary.StageTree
import bard.db.enums.ReadyForExtraction
import dataexport.registration.BardHttpResponse
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.sql.Sql
import groovy.xml.MarkupBuilder

import javax.sql.DataSource

/**
 * Top Level service for handling the
 * generation of dictionary and dictionary elements
 *
 * Please, familiarize yourself with the dictionary.xsd located in web-app/schemas/
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
    public BardHttpResponse update(final Long id, final Long clientVersion, final ReadyForExtraction latestStatus) {
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

        final Sql sql = new Sql(dataSource)
        //TODO review this seems not correct ot just pick the first one, there can be several
        def stageRow = sql.firstRow("SELECT * FROM STAGE_TREE WHERE STAGE_ID=?", [elementId])
        if (stageRow) {
            StageTree stageTree = StageTree.findById(stageRow.NODE_ID)
            this.dictionaryExportHelperService.generateStage(xml, stageTree)
            return 0
        }
        String errorMessage = "Element with id ${elementId} does not exists"
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
        //TODO review this seems odd, to selec the first one, a given element can appear several times
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
