package db

import java.util.Map;
import db.DatabaseConnectivity
import db.CapQueries;

class Assay extends DatabaseConnectivity implements CapQueries{

	/**
	 * @param assayId
	 * @return assay summary information
	 */
	Map<String, String> getAssaySummaryById(def assayId) {
		def assaySummaryInfo = [:]
		def sql = getSql()
		sql.eachRow(ASSAY_SUMMARY_BYID, [assayId]) { row ->
			assaySummaryInfo = ['assayId': row.adid, 'assayName':row.name, 'shortName':row.sName, 'assayVersion':row.aVersion, 'assayType':row.aType, 'assayStatus':row.status, 'designedBy': row.designedBy]
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
	List<String> getAssayContextItem(def assayId, def contextGroup, def contextName) {
		def contextITemMap = [:]
		def contextItemsList = []
		def sql = getSql()
		sql.eachRow(ASSAY_CONTEXT_ITEMS, [assayId, contextGroup, contextName]) { row ->
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
}
