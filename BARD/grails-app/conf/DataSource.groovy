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
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "YCRUZ"
			password = "YCRUZ"
		}
	}
	oracledev {
		dataSource {
			dbCreate = 'validate'
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "bard_dev"
			password = "bard_dev"
		}
	}
	oracleqa {
		dataSource {
			dbCreate = 'validate'
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "bard_qa"
			password = "bard_qa"
		}
	}
	ycruz {
		dataSource {
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "YCRUZ"
			password = "YCRUZ"
		}
	}
	ddurkin {
		dataSource {
		//	dbCreate = 'validate'
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "ddurkin"
			password = "guest"
		}
	}
	mysql {
		dataSource {
//        	dbCreate = "update"
//        	dialect = "org.hibernate.dialect.MySQLDialect"
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			url = "jdbc:mysql://dbserver:3306/schema"
			username = "user"
			password = "pwd"
		}
	}
	test {
		dataSource {
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "YCRUZ"
			password = "YCRUZ"
			
//			dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
//			url = "jdbc:h2:mem:devDB"
//			driverClassName = "org.h2.Driver"
//			username = "sa"
//			password = ""
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
