dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
            pooled = true
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis = 1800000
                timeBetweenEvictionRunsMillis = 1800000
                numTestsPerEvictionRun = 3
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = true
                validationQuery = "SELECT 1"
            }
        }
    }
    gwalzer_barddev {
        dataSource {
            pooled = true
            dbCreate = "update"
            url = "jdbc:oracle:thin:@vmbarddev:1521:barddev"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = org.hibernate.dialect.Oracle10gDialect
            username = "gwalzer_bard"
            password = "ch3mb10"
//            properties {
//                maxActive = 100
//                maxIdle = 25
//                minIdle = 5
//                initialSize = 5
//                minEvictableIdleTimeMillis = 60000
//                timeBetweenEvictionRunsMillis = 60000
//                maxWait = 10000
//            }
        }
    }
}
