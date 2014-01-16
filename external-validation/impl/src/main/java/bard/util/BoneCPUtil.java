package bard.util;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class BoneCPUtil {

	public static DataSource configureDataSource(String driverClass, String jdbcUrl, String user, String pwd) throws ClassNotFoundException {
		Class.forName(driverClass);
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(user);
		config.setPassword(pwd);
		config.setPartitionCount(3);
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(5);
		config.setAcquireIncrement(5);
		config.setCloseConnectionWatch(true);
		config.setConnectionTestStatement("SELECT 1");
		DataSource dataSource = new BoneCPDataSource(config);
		return dataSource;
	}

}
