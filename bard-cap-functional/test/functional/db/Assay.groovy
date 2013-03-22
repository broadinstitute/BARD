package db

import java.util.Map;
import db.DatabaseConnectivity

class Assay extends DatabaseConnectivity{

	Map<String, String> getAssaySummaryById(def value) {
		def resultMap = [:]
		def sql = getSql()
		sql.eachRow("SELECT A.ASSAY_ID adid, A.ASSAY_STATUS status, A.ASSAY_SHORT_NAME sName, A.ASSAY_NAME name, A.ASSAY_VERSION aVersion, A.ASSAY_TYPE aType, A.DESIGNED_BY designedBy FROM ASSAY A WHERE A.ASSAY_ID=$value") { row ->
			resultMap = ['assayId': row.adid, 'assayName':row.name, 'shortName':row.sName, 'assayVersion':row.aVersion, 'assayType':row.aType, 'assayStatus':row.status, 'designedBy': row.designedBy]
		}
		return resultMap
	}

	Map<String, String> getAssaySummaryByName(def value) {
		def resultMap = [:]
		def sql = getSql()
		sql.eachRow("SELECT A.ASSAY_ID adid, A.ASSAY_STATUS status, A.ASSAY_SHORT_NAME sName, A.ASSAY_NAME name, A.ASSAY_VERSION aVersion, A.ASSAY_TYPE aType, A.DESIGNED_BY designedBy FROM ASSAY A WHERE A.ASSAY_NAME=$value") { row ->
			resultMap = ['assayId': row.adid, 'assayName':row.name, 'shortName':row.sName, 'assayVersion':row.aVersion, 'assayType':row.aType, 'assayStatus':row.status, 'designedBy': row.designedBy]
		}
		return resultMap
	}

	def getAssaySearchCount(def value) {
		def resultCount
		def sql = getSql()
		sql.eachRow("SELECT COUNT(A.ASSAY_NAME) Count from ASSAY A where A.ASSAY_NAME LIKE ?", ['%'+value+'%']){ row->
			resultCount = 	row.Count
		}
		return resultCount
	}

	List<String> getAssayContext(def assayId, def contextGroup) {
		def resultList = []
		def sql = getSql()
		sql.eachRow("SELECT AC.CONTEXT_NAME CName FROM ASSAY_CONTEXT AC WHERE AC.ASSAY_ID = ${assayId} AND AC.CONTEXT_GROUP = ${contextGroup}") { row ->
			resultList.add(row.CName)
		}
		return resultList
	}
	
	List<String> getAssayContextItem(def assayId, def contextGroup, def contextName) {
		def resultMap = [:]
		def resultList = []
		def sql = getSql()
		sql.eachRow("SELECT E.LABEL AttributeLable, ACI.VALUE_DISPLAY ValueDisplay FROM ASSAY_CONTEXT_ITEM ACI, ELEMENT E WHERE ACI.ATTRIBUTE_ID = E.ELEMENT_ID AND ACI.ASSAY_CONTEXT_ID IN(SELECT AC.ASSAY_CONTEXT_ID ContextID FROM ASSAY_CONTEXT AC WHERE AC.ASSAY_ID = ${assayId} AND AC.CONTEXT_GROUP = ${contextGroup} AND AC.CONTEXT_NAME = ${contextName})") { row ->
			resultMap = ['attributeLable':row.AttributeLable, 'valueDisplay':row.ValueDisplay]
			resultList.add(resultMap) 
		}
		return resultList
	}
}
