dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	username = "username"
	password = "password"
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
			dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDB"
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
		}
	}
    oracle {
        dataSource {
            dbCreate = 'validate'
            url = "jdbc:oracle:thin:@dbnode01:1521:bcbdev"
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            dialect = bard.SequencePerTableOracleDialect
            username = "bard_dev"
            password = "guest"
        }
    }
    mysql {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://dbserver:3306/mlbd"
            dialect = "org.hibernate.dialect.MySQLDialect"
        }
    }
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://dbserver:3306/mlbd"
            dialect = "org.hibernate.dialect.MySQLDialect"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://dbserver:3306/mlbd"
            dialect = "org.hibernate.dialect.MySQLDialect"
		}
	}
}
