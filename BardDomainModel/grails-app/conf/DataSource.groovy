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
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
        }
    }
    oracledev {
        dataSource {
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "bard_dev"
            password = "bard_dev"
        }
    }
    oracleqa {
        dataSource {
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "bard_qa"
            password = "bard_qa"
        }
    }
    ddurkin {
        dataSource {
            //dbCreate = "validate"
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "ddurkin"
            password = "guest"
        }
    }
    jacob {
        dataSource {
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "JASIEDU"
            password= "JASIEDU"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE"
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
}
