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
	 * @param assayId
	 * @param contextGroup
	 * @return list of assay context cards of sepecific group
	 */
	List<String> getAssayContext(def assayId, def contextGroup) {
		def contextCardsList = []
		def sql = getSql()
		sql.eachRow(ASSAY_CONTEXT_CARDS, [assayId, contextGroup]) { row ->
			contextCardsList.add(row.CName)
		}
		return contextCardsList
	}
	/**
	 * @param assayId
	 * @param contextGroup
	 * @param contextName
	 * @return list of assay context items present in a specific assay context card
	 */
	public static def getProjectContextItem(def projectId, def contextName) {
		def contextITemMap = [:]
		def contextItemsList = []
		def sql = getSql()
		sql.eachRow(ProjectQueries.PROJECT_CONTEXT_ITEMS, [projectId, contextName]) { row ->
			contextITemMap = ['attributeLabel':row.AttributeLabel, 'valueDisplay':row.ValueDisplay]
			contextItemsList.add(contextITemMap)
		}
		return contextItemsList
	}
	/**
	 * @param assayId
	 * @return measure added in a specific assay
	 */
	def getMeasureAdded(def assayId) {
		def assayMeasures
		def sql = getSql()
		sql.eachRow(ASSAY_MEASURE, [assayId]) { row ->
			assayMeasures = row.measure+" ("+row.label+")"
		}
		return assayMeasures
	}
	/**
	 * @param assayId
	 * @return list of assay measures
	 */
	List<String> getAssayMeasures(def assayId) {
		def assayMeasuresList = []
		def sql = getSql()
		sql.eachRow(ASSAY_MEASURES_LIST, [assayId]) { row ->
			assayMeasuresList.add(row.measure)
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
		def sql = getSql()
		sql.eachRow(ASSAY_ASSOCIATED_MEASURE_CONTEXT, [assayId, measureName]) { row ->
			associatedContextMeasureMap = ['measure':row.measure, 'context':row.context]
		}
		return associatedContextMeasureMap
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
