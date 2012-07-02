hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
	cache.region.factory_class = 'org.hibernate.cache.RegionFactory'
}
// defaulting to using validate unless we specify something different on command line
// you can't always use the validation option for database migration dbm- commmands
// say bootstrapping a fresh schema, in this case you can turn off validation
dataSource.dbCreate = System.getProperty('dataSource.dbCreate')?:'validate'

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
			url = "jdbc:oracle:thin:@barddb:1521:bardqa"
			driverClassName = "oracle.jdbc.driver.OracleDriver"
			dialect = bard.SequencePerTableOracleDialect
			username = "ddurkin"
			password = "guest"
		}
	}
	mysql {
		dataSource {
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

			url = "jdbc:h2:mem:devDB"
			driverClassName = "org.h2.Driver"
			username = "sa"
			password = ""
		}
	}
	production {
		dataSource {
			url = "jdbc:mysql://dbserver:3306/mlbd"
			dialect = "org.hibernate.dialect.MySQLDialect"
		}
	}
}
