package bard.util.dbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class OracleSequenceIterator implements Iterator<Long> {

	private Connection conn;
	private String sequenceName;
	private int prefetchSize;
	private ResultSet rs;

	public OracleSequenceIterator(Connection conn, String sequenceName, int prefetchSize) {
		this.conn = conn;
		this.sequenceName = sequenceName;
		this.prefetchSize = prefetchSize;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Long next() {
		try {
			if (rs == null || !rs.next()) {
				if( rs != null )
					rs.close();
				PreparedStatement ps = conn.prepareStatement(String.format("select %s.nextval from dual connect by level <= %s", sequenceName, prefetchSize),
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ps.setFetchSize(prefetchSize);
				rs = ps.executeQuery();
				rs.next();
			}
			return rs.getLong(1);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void remove() {

	}

	@Override
	public void finalize() throws Throwable {
		rs.close();
	}

}