package db

import grails.plugin.remotecontrol.RemoteControl
import groovy.sql.Sql
import groovyx.remote.RemoteControlException;

import java.sql.SQLRecoverableException

import oracle.net.ns.NetException

import org.springframework.beans.factory.BeanCreationException

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

		RemoteControl remote = new RemoteControl()
		def applicationURL = remote { ctx.grailsApplication.config.grails.serverURL }
//		def resultURL = applicationURL.substring(applicationURL.indexOf("//")+Constants.index_2, applicationURL.indexOf('.'))
		if(applicationURL.indexOf("qa") > -1){
			dbDatasource = Constants.qaDatasource
		}else if(applicationURL.indexOf("dev") > -1){
			dbDatasource = Constants.devDatasource
		}
		//		if(resultURL.equalsIgnoreCase(Constants.dbInstance.qa)){
		//			dbDatasource = Constants.qaDatasource
		//		}else if(resultURL.equalsIgnoreCase(Constants.dbInstance.dev)){
		//			dbDatasource = Constants.devDatasource
		//		}
		return dbDatasource
	}
}