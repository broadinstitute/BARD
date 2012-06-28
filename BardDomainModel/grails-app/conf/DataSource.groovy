dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
    // defaulting to using validate unless we specify something different on command line
    // you can't always use the validation option for database migration dbm- commmands
    // say bootstrapping a fresh schema, in this case you can turn off validation
    dbCreate = System.getProperty('dataSource.dbCreate')?:"validate"
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
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "ddurkin"
            password = "guest"
        }
    }
    ben {
        dataSource {
            //dbCreate = "validate"
            url = "jdbc:oracle:thin:@barddb:1521:bardqa"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "balexand"
            password = "bardqa "
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
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
        dataSource {
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
