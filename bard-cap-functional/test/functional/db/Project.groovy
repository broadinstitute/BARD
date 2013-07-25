package db

import common.ProjectQueries;

class Project extends DatabaseConnectivity {

	/**
	 * @param projectId
	 * @return project summary information
	 */
	public static def getProjectSummaryById(def projectId) {
		def projectSummaryInfo = [:]
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(ProjectQueries.PROJECT_SUMMARY_BYID, [projectId]) {
			projectSummaryInfo = it.toRowResult()
		}
		return projectSummaryInfo
	}
	/**
	 * @param projectName
	 * @return project summary information 
	 */
	def getProjectSummaryByName(def projectName) {
		def projectSummaryInfo = [:]
		def sql = getSql()
		sql.eachRow(PROJECT_SUMMARY_BYNAME, [projectName]) { row ->
			projectSummaryInfo = ['projectId': row.pid, 'projectName':row.pname, 'projectVersion':row.pversion, 'projectGroup':row.pgroup, 'projectStatus':row.pstatus, 'projectReady': row.ready, 'projectDescription':row.pdesc]
		}
		return projectSummaryInfo
	}
	/**
	 * @param searchStr
	 * @return searched result count
	 */
	def getProjectSearchCount(def searchStr) {
		def searchResultCount
		def sql = getSql()
		sql.eachRow(PROJECT_SEARCH_COUNT, ['%'+searchStr+'%']){ row->
			searchResultCount = 	row.pcount
		}
		return searchResultCount
	}
	/**
	 * @param projectId
	 * @param contextGroup
	 * @return list of project context cards of specific group
	 */
	public static def getProjectContext(def contextGroup, def projectId) {
		def contextCards = []
		def sql = getSql()
		sql.eachRow(ProjectQueries.PROJECT_CONTEXTS, [projectId, contextGroup]) { row ->
			contextCards.add(row.ContextName)
		}
		return contextCards
	}
	/**
	 * @param projectId
	 * @param contextGroup
	 * @param contextName
	 * @return list of project context items present in a specific project context card
	 */
	public static def getProjectContextItem(def projectId, def contextGroup, def contextName) {
		def contextITemMap = [:]
		def contextItemsList = []
		def sql = getSql()
		sql.eachRow(ProjectQueries.PROJECT_CONTEXT_ITEMS, [projectId, contextGroup, contextName]) { row ->
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
			documentList.add(row.Name)
			//documentList.add(documentMap)
		}
		return documentList
	}
}
