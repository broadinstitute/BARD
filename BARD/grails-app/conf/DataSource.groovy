hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
    cache.region.factory_class = 'org.hibernate.cache.RegionFactory'
}
dataSource.dbCreate = System.getProperty('dataSource.dbCreate') ?: 'none'

/**
 * Set default connection pooling config
 */
dataSource {
    pooled = true
    properties {
        maxActive = 50
        maxIdle = 25
        minIdle = 5
        initialSize = 5
        minEvictableIdleTimeMillis = 60000
        timeBetweenEvictionRunsMillis = 60000
        maxWait = 10000
        testOnReturn = true
        validationQuery = "select 1 from dual"
    }
}
