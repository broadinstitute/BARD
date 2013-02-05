package bard.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Close quietly
 * @author southern
 *
 */
public class SQLUtil {
	private static Logger log = LoggerFactory.getLogger(SQLUtil.class);

	public static void closeQuietly(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing connection.", e);
		}
	}

	public static void closeQuietly(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing statement.", e);
		}
	}

	public static void closeQuietly(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing result set.", e);
		}
	}
}