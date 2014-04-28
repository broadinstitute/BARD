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


class Assay extends DatabaseConnectivity {

    /**
     * @param assayId
     * @return assay summary information
     */
    public static def getAssaySummaryById(def assayId) {
        def assaySummaryInfo = [:]
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_SUMMARY_BYID, [assayId]) {
                assaySummaryInfo = it.toRowResult()
            }
        }
        return assaySummaryInfo
    }

    /**
     * @param searchQuery
     * @return assay searched results, only ids are returned
     */
    public static def getAssaySearchResults(def searchQuery) {
        def searchResult = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_SEARCH_RSULTS, ['%' + searchQuery + '%']) { row ->
                searchResult.add(row.AID.toString())
            }
        }
        return searchResult
    }
    /**
     * @param searchQuery
     * @return searched result count
     */
    public static def getAssaySearchCount(def searchQuery) {
        def searchResultCount
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_SEARCH_COUNT, ['%' + searchQuery + '%']) { row ->
                searchResultCount = row.AssayCount
            }
        }
        return searchResultCount
    }
    /**
     * @param assayId
     * @param contextGroup
     * @return list of assay context cards of specific group
     */
    public static def getAssayContext(def contextType, def assayId) {
        def contextCards = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_CONTEXTS, [assayId, contextType]) { row ->
                contextCards.add(row.ContextName)
            }
        }
        return contextCards
    }
    /**
     * @param assayId
     * @param contextGroup
     * @param contextName
     * @return list of assay context items present in a specific assay context card
     */
    public static def getAssayContextItem(def assayId, def contextType, def contextName) {
        def contextITemMap = [:]
        def contextItemsList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_CONTEXT_ITEMS, [assayId, contextType, contextName]) { row ->
                contextITemMap = ['attributeLabel': row.AttributeLabel, 'valueDisplay': row.ValueDisplay.toString()]
                contextItemsList.add(contextITemMap)
            }
        }
        return contextItemsList
    }
    /**
     * @param assayId
     * @return measure added in a specific assay
     */
    def getMeasureAdded(def assayId) {
        def assayMeasures
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ASSAY_MEASURE, [assayId]) { row ->
                assayMeasures = row.measure + " (" + row.label + ")"
            }
        }
        return assayMeasures
    }
    /**
     * @param assayId
     * @return list of assay measures
     */
    List<String> getAssayMeasures(def assayId) {
        def assayMeasuresList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ASSAY_MEASURES_LIST, [assayId]) { row ->
                assayMeasuresList.add(row.measure)
            }
        }
        return assayMeasuresList
    }
    /**
     * @param assayId
     * @param measureName
     * @return measures associated with contexts
     */
    Map<String, String> getContextMeasures(def assayId, def measureName) {
        def associatedContextMeasureMap = [:]
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(ASSAY_ASSOCIATED_MEASURE_CONTEXT, [assayId, measureName]) { row ->
                associatedContextMeasureMap = ['measure': row.measure, 'context': row.context]
            }
        }
        return associatedContextMeasureMap
    }
    /**
     *
     * @param assayId
     * @param documentType
     * @return documents list associated with specific document type
     */
    public static def getAssayDocuments(def assayId, def documentType) {
        //def documentMap = [:]
        def documentList = []
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_DOCUMENT, [assayId, documentType]) { row ->
                documentList.add(row.Name)
                //documentList.add(documentMap)
            }
        }
        return documentList
    }

    /**
     * @param name
     * @return created assay summary information
     */
    public static def getCreatedAssaySummary(String name) {
        def assaySummary = [:]
        DatabaseConnectivity.withSql { sql ->
            sql.eachRow(AssayQueries.ASSAY_SUMMARY_CREATED, [name]) {
                assaySummary = it.toRowResult()
            }
        }
        return assaySummary
    }
}
