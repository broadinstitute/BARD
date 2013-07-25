package db

import common.AssayQueries;

class Assay extends DatabaseConnectivity {

	/**
	 * @param assayId
	 * @return assay summary information
	 */
	public static def getAssaySummaryById(def assayId) {
		def assaySummaryInfo = [:]
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(AssayQueries.ASSAY_SUMMARY_BYID, [assayId]) {
			assaySummaryInfo = it.toRowResult()
		}
		return assaySummaryInfo
	}
	
	/**
	 * @param assayName
	 * @return assay summary information 
	 */
	Map<String, String> getAssaySummaryByName(def assayName) {
		def assaySummaryInfo = [:]
		def sql = getSql()
		sql.eachRow(ASSAY_SUMMARY_BYNAME, [assayName]) { row ->
			assaySummaryInfo = ['assayId': row.adid, 'assayName':row.name, 'shortName':row.sName, 'assayVersion':row.aVersion, 'assayType':row.aType, 'assayStatus':row.status, 'designedBy': row.designedBy]
		}
		return assaySummaryInfo
	}
	/**
	 * @param searchStr
	 * @return searched result count
	 */
	def getAssaySearchCount(def searchStr) {
		def searchResultCount
		def sql = getSql()
		sql.eachRow(ASSAY_SEARCH_NAME_STR, ['%'+searchStr+'%']){ row->
			searchResultCount = 	row.Count
		}
		return searchResultCount
	}
	/**
	 * @param assayId
	 * @param contextGroup
	 * @return list of assay context cards of specific group
	 */
	public static def getAssayContext(def contextGroup, def assayId) {
		def contextCards = []
		def sql = getSql()
		sql.eachRow(AssayQueries.ASSAY_CONTEXTS, [assayId, contextGroup]) { row ->
			contextCards.add(row.ContextName)
		}
		return contextCards
	}
	/**
	 * @param assayId
	 * @param contextGroup
	 * @param contextName
	 * @return list of assay context items present in a specific assay context card
	 */
	public static def getAssayContextItem(def assayId, def contextGroup, def contextName) {
		def contextITemMap = [:]
		def contextItemsList = []
		def sql = getSql()
		sql.eachRow(AssayQueries.ASSAY_CONTEXT_ITEMS, [assayId, contextGroup, contextName]) { row ->
			contextITemMap = ['attributeLabel':row.AttributeLabel, 'valueDisplay':row.ValueDisplay.toString()]
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
	 * 
	 * @param assayId
	 * @param documentType
	 * @return documents list associated with specific document type
	 */
	public static def getAssayDocuments(def assayId, def documentType) {
		//def documentMap = [:]
		def documentList = []
		def sql = getSql()
		sql.eachRow(AssayQueries.ASSAY_DOCUMENT, [assayId, documentType]) { row ->
			documentList.add(row.Name)
			//documentList.add(documentMap)
		}
		return documentList
	}
}
