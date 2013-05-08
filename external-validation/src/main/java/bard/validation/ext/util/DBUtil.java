package bard.validation.ext.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import bard.util.SQLUtil;
import bard.validation.ext.ExternalItem;
import bard.validation.ext.ExternalOntologyException;

public class DBUtil {

	public static List<ExternalItem> runQuery(DataSource dataSource, String sql, int prefetchSize, int limit, Object... terms) throws ExternalOntologyException {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for(int ii = 0; ii < terms.length; ii++)
				ps.setObject(ii+1, terms[ii]);
			ps.setFetchSize(Math.max(prefetchSize, limit));
			rs = ps.executeQuery();
			List<ExternalItem> items = processResultSet(rs, limit);
			return items;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ExternalOntologyException(ex);
		} finally {
			SQLUtil.closeQuietly(rs);
			SQLUtil.closeQuietly(conn);
		}
	}
	
	public static List<ExternalItem> processResultSet(ResultSet rs, int limit) throws SQLException {
		List<ExternalItem> items = limit > 0 ? new ArrayList<ExternalItem>(limit) : new ArrayList<ExternalItem>();
		int counter = 0;
		while (rs.next()) {
			ExternalItem item = new ExternalItem(rs.getString(1), rs.getString(2));
			items.add(item);
			if (limit > 0 & counter++ >= limit)
				break;
		}
		return items;
	}
}