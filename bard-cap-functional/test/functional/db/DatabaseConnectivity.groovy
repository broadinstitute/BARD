package db

import groovy.sql.Sql

class DatabaseConnectivity{

	def getSql = {
		Sql.newInstance("jdbc:oracle:thin:@vmbarddev:1521:barddev",
				"bard_qa_cap",
				"Ze3eqe2T",
				"oracle.jdbc.OracleDriver")
	}

	//	def getDataSource( ) {
	//
	//		def sql = getSql()
	//		sql.eachRow("select * from ASSAY where ASSAY_ID=77") {
	//			println "${it.assay_name}"
	//		}
	//	}
}
