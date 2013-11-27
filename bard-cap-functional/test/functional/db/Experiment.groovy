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
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(ExperimentQueries.EXPERIMENT_SUMMARY_BYID, [experimentId]) {
			summaryInfo = it.toRowResult()
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
		def sql = getSql()
		sql.eachRow(ExperimentQueries.EXPERIMENT_CONTEXTS, [experimentId, contextType]) { row ->
			contextCards.add(row.ContextName)
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
		def sql = getSql()
		sql.eachRow(ExperimentQueries.EXPERIMENT_CONTEXT_ITEMS, [experimentId, contextType, contextName]) { row ->
			contextITemMap = ['attributeLabel':row.AttributeLabel, 'valueDisplay':row.ValueDisplay]
			contextItemsList.add(contextITemMap)
		}
		return contextItemsList
	}
		
	/**
	 * @param projectId
	 * @param documentType
	 * @return documents list associated with specific document type
	 */
	public static def getProjectDocuments(def projectId, def documentType) {
		//def documentMap = [:]
		def documentList = []
		def sql = getSql()
		sql.eachRow(ProjectQueries.PROJECT_DOCUMENT, [projectId, documentType]) { row ->
			documentList.add(row.Name.trim())
			//documentList.add(documentMap)
		}
		return documentList
	}
}
