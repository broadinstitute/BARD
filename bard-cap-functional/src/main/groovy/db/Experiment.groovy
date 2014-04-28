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

package db

/**
 * This class includes all the database query functions to fetch data from database for experiments.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/25
 */
class Experiment extends DatabaseConnectivity {

    /**
     * this method fetches the summary information by experiment id
     * @param experimentId
     * @return summary information including EID, Name, Description, Status, Run dates, create/update dates etc.
     */
    public static def getExperimentSummaryById(def experimentId) {
        def summaryInfo = [:]
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_SUMMARY_BYID, [experimentId]) {
                summaryInfo = it.toRowResult()
            }
        }
        return summaryInfo
    }

    /**
     * this method fetches the context cards of specific context group or context type.
     * @param experimentId
     * @param contextGroup
     * @return list of experiment context cards of specific group
     */
    public static def getExperimentContext(def contextType, def experimentId) {
        def contextCards = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_CONTEXTS, [experimentId, contextType]) { row ->
                contextCards.add(row.ContextName)
            }
        }
        return contextCards
    }

    /**
     * this method fetched the context items of specific context name and group.
     * @param experimentId
     * @param contextGroup
     * @param contextName
     * @return list of experiment context items present in a specific experiment context card
     */
    public static def getExperimentContextItem(def experimentId, def contextType, def contextName) {
        def contextITemMap = [:]
        def contextItemsList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_CONTEXT_ITEMS, [experimentId, contextType, contextName]) { row ->
                contextITemMap = ['attributeLabel': row.AttributeLabel, 'valueDisplay': row.ValueDisplay]
                contextItemsList.add(contextITemMap)
            }
        }
        return contextItemsList
    }

    public static def getExperimentResultType(def experimentId) {
        def resultTypeList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_RESULTS, [experimentId]) { row ->
                resultTypeList.add(row.ResultType)
            }
        }
        return resultTypeList
    }
    /**
     * this method fetched the documents name and context for specific document type.
     * @param experimentId
     * @param documentType
     * @return documents list associated with specific document type
     */
    public static def getExperimentDocuments(def experimentId, def documentType) {
        def documentList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_DOCUMENT, [experimentId, documentType]) { row ->
                documentList.add(row.Name.trim())
                //documentList.add(documentMap)
            }
        }
        return documentList
    }

    /**
     * @param name
     * @return created experiment summary information
     */
    public static def getCreatedExperimentSummary(String name) {
        def experimentSummary = [:]
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ExperimentQueries.EXPERIMENT_SUMMARY_CREATED, [name]) {
                experimentSummary = it.toRowResult()
            }
        }
        return experimentSummary
    }
}
