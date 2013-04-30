package db

import groovy.sql.Sql
import javax.sql.DataSource
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DatabaseConnectivity{
	def _url = ConfigurationHolder.config.dataSource.url
	def _username = ConfigurationHolder.config.dataSource.username
	def _password = ConfigurationHolder.config.dataSource.password
	def _driver = ConfigurationHolder.config.dataSource.driverClassName
	def getSql = {
		Sql.newInstance(_url, _username, _password, _driver)
	}


	/*	def getSql = {
	 Sql.newInstance("jdbc:oracle:thin:@vmbarddev:1521:barddev",
	 "bard_qa_cap",
	 "Ze3eqe2T",
	 "oracle.jdbc.OracleDriver")
	 }
	 */
}
