package db

import common.ConfigHelper
import groovy.sql.Sql

class DatabaseConnectivity {


    public static Sql getSql(){
        final Map dbInfoMap = ConfigHelper.config.dbInfoMap
        Sql.newInstance(dbInfoMap.url, dbInfoMap.username, dbInfoMap.password, dbInfoMap.driver)
    }


}