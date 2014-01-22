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
