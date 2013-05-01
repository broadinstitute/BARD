package bard.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Close quietly
 * 
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

	public static String inClauseParams(int size) {
		StringBuilder sb = new StringBuilder(size * 2);
		sb.append("?");
		for (int ii = 1; ii <= size - 1; ii++)
			sb.append(",?");
		return sb.toString();
	}

	public static void fillInClause(PreparedStatement ps, int startIndex, Collection<?> clauses, int sqlType) throws SQLException {
		fillInClause(ps, startIndex, clauses.size(), clauses.iterator(), sqlType);
	}
	
	public static void fillInClause(PreparedStatement ps, int startIndex, int maxSize, Iterator<?> clauses, int sqlType) throws SQLException {
		int counter = 0;
		for (; clauses.hasNext(); ++counter) {
			ps.setObject(startIndex++, clauses.next(), sqlType);
			if (counter >= maxSize)
				break;
		}
		for (int ii = maxSize; ii > counter; ii--)
			ps.setNull(startIndex++, sqlType);
	}
}