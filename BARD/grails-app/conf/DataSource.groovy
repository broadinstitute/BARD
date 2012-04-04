dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	username = "username"
	password = "password"
	dialect = "org.hibernate.dialect.MySQLDialect"
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
	cache.region.factory_class = 'org.hibernate.cache.RegionFactory'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://dbserver:3306/mlbd"
		}
	}
    oracle {
        dataSource {
            dbCreate = 'validate'
            url = "jdbc:oracle:thin:@dbnode01:1521:bcbdev"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = org.hibernate.dialect.Oracle10gDialect
            username = "mlbd_dev"
            password = "guest"
        }
    }
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://dbserver:3306/mlbd"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://dbserver:3306/mlbd"
		}
	}
}
