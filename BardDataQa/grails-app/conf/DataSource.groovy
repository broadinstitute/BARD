dataSource {
    url = "jdbc:oracle:thin:@bardprod:1521:bardprod"
    driverClassName = "oracle.jdbc.driver.OracleDriver"
//    username = "bard_viewer"
//    password = "hzI0l5vvN6Z54oZ4"
    username = "bard_data_qa_dashboard"
    password = "RoseByAnyOtherName2013"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

