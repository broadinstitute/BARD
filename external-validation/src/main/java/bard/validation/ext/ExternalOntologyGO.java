package bard.validation.ext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bard.util.SQLUtil;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * Implementation for GO ontology via SQL. Default instantiation makes a connection to a public mysql database at the EBI.
 * @author southern
 *
 */
@Deprecated
public class ExternalOntologyGO extends ExternalOntologyAPI {

	private BoneCP connectionPool;

	public ExternalOntologyGO() throws ExternalOntologyException {
		this("com.mysql.jdbc.Driver", "jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest", "go_select", "amigo");
	}
	
	public ExternalOntologyGO(String driverClass, String jdbcUrl, String user, String pwd) throws ExternalOntologyException {
		try {
			Class.forName(driverClass);
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(jdbcUrl); 
			config.setUsername(user);
			config.setPassword(pwd);
			connectionPool = new BoneCP(config);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	/**
	 * Ensure connection pool is shutdown 
	 */
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		connectionPool.shutdown();
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological processes  
	 */
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		List<ExternalItem> items = runQuery(
				"SELECT acc, name FROM term WHERE acc = upper(?) and term_type = 'biological_process' and is_obsolete = 0", id);
		if (items.size() > 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique GO accesion", id));
		return items.get(0);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological processes  
	 */
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		List<ExternalItem> items = runQuery(
				"SELECT acc, name FROM term WHERE name=lower(?) and term_type = 'biological_process' and is_obsolete = 0", name);
		if (items.size() > 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique GO term name", name));
		return items.get(0);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological processes. Uses a SQL LIKE.
	 */
	@Override
	public List<ExternalItem> findMatching(String name) throws ExternalOntologyException {
		List<ExternalItem> items = runQuery(
				"SELECT acc, name FROM term WHERE name like lower(?) and term_type = 'biological_process' and is_obsolete = 0",
				queryGenerator(name));
		return items;
	}

	/**
	 * Gene Ontology URL
	 */
	@Override
	public String getExternalURL(String id) {
		return String.format("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=%s", id);
	}

	protected List<ExternalItem> processResultSet(ResultSet rs) throws SQLException {
		List<ExternalItem> items = new ArrayList();
		while (rs.next()) {
			ExternalItem item = new ExternalItem(rs.getString(1), rs.getString(2));
			items.add(item);
		}
		return items;
	}

	/**
	 * trims and adds a % to the end of the term if it doesn't already have one.
	 */
	@Override
	public String queryGenerator(String term) {
		term = term.trim();
		if (!term.endsWith("%"))
			term += "%";
		return term;
	}

	protected List<ExternalItem> runQuery(String sql, String term) throws ExternalOntologyException {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = connectionPool.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, term);
			rs = ps.executeQuery();
			List<ExternalItem> items = processResultSet(rs);
			return items;
		} catch (SQLException ex) {
			throw new ExternalOntologyException(ex);
		}
		finally {
			SQLUtil.closeQuietly(rs);
			SQLUtil.closeQuietly(conn);
		}
	}
}