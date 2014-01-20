package db

import com.jolbox.bonecp.BoneCPDataSource
import common.ConfigHelper
import groovy.sql.Sql

import javax.sql.DataSource

class DatabaseConnectivity {

    private static final DataSource dataSource = getDataSource()

    public static Sql getSql() {
        Sql.newInstance(dataSource)
    }

    public static def withSql(Closure closure) {
        final Sql sql = getSql()
        try {
            return closure.call(sql)
        }
        finally {
            sql.close()
        }
    }

    private static DataSource getDataSource() {
        final Map dbInfoMap = ConfigHelper.config.dbInfoMap
        final BoneCPDataSource ds = new BoneCPDataSource()
        ds.setDriverClass(dbInfoMap.driver)
        ds.setUsername(dbInfoMap.username)
        ds.setPassword(dbInfoMap.password)
        ds.setJdbcUrl(dbInfoMap.url)
        return ds
    }

}