package db

import groovy.sql.Sql

import common.Constants

class DatabaseConnectivity{
	static def dataSource = getDatasource()
	static def _url = dataSource.url
	static def _username = dataSource.username
	static def _password = dataSource.password
	static def _driver = dataSource.driver

	public static def getSql = {
		Sql.newInstance(_url, _username, _password, _driver)
	}

	static def getDatasource(){
		def dbDatasource

        def config = new ConfigSlurper().parse(new File('localConfig.groovy').toURI().toURL())
		def applicationURL = config.server.url
		if(applicationURL.indexOf("qa") > -1){
			dbDatasource = Constants.qaDatasource
		}else if(applicationURL.indexOf("dev") > -1){
			dbDatasource = Constants.devDatasource
		}
		return dbDatasource
	}
}