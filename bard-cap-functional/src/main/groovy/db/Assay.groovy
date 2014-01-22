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
