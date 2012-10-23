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

    test {
        dataSource {
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
}
