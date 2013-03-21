/**
 * NOTE all database connection info is externalized
 */
dataSource {
    pooled = true
    // defaulting to using validate unless we specify something different on command line
    // you can't always use the validation option for database migration dbm- commmands
    // say bootstrapping a fresh schema, in this case you can turn off validation
    dbCreate = System.getProperty('dataSource.dbCreate') ?: "no-validate"
    properties {
        maxActive = 50
        maxIdle = 25
        minIdle = 5
        initialSize = 5
        minEvictableIdleTimeMillis = 60000
        timeBetweenEvictionRunsMillis = 60000
        maxWait = 10000
        testOnReturn = true
        validationQuery = "select bard_context.clear_username() from dual"
    }
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
    format_sql = true
    use_sql_comments = true
}
