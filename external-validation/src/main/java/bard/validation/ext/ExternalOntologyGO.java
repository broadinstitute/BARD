package bard.validation.ext;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

import bard.util.SQLUtil;

/**
 * Implementation for GO ontology via SQL. Default instantiation makes a
 * connection to a public mysql database at the EBI.
 * 
 * @author southern
 * 
 */
public class ExternalOntologyGO extends ExternalOntologyAPI {

	public final static String TYPE_BIOLOGICAL_PROCESS = "biological_process";
	public final static String TYPE_CELLULAR_COMPONENT = "cellular_component";
	public final static String TYPE_MOLECULAR_FUNCTION = "molecular_function";

	public static ExternalOntologyGO COMPONENT_INSTANCE, FUNCTION_INSTANCE, PROCESS_INSTANCE;

	static {
		try {
			DataSource dataSource = configureDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest",
					"go_select", "amigo");
			COMPONENT_INSTANCE = new ExternalOntologyGO(TYPE_CELLULAR_COMPONENT);
			COMPONENT_INSTANCE.setDataSource(dataSource);
			FUNCTION_INSTANCE = new ExternalOntologyGO(TYPE_MOLECULAR_FUNCTION);
			FUNCTION_INSTANCE.setDataSource(dataSource);
			PROCESS_INSTANCE = new ExternalOntologyGO(TYPE_BIOLOGICAL_PROCESS);
			PROCESS_INSTANCE.setDataSource(dataSource);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static DataSource configureDataSource(String driverClass, String jdbcUrl, String user, String pwd)
			throws ClassNotFoundException {
		Class.forName(driverClass);
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(user);
		config.setPassword(pwd);
		DataSource dataSource = new BoneCPDataSource(config);
		return dataSource;
	}

	public static class GOCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			// http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=
			if (!"amigo.geneontology.org".equals(host))
				return null;
			String path = uri.getPath();
			if(!path.endsWith("term_details"))
				return null;
			MultiValueMap<String, List<String>> params = getUriParameters(uri);
			List<String> subtree = (List<String>) params.get("subtree");
			if (subtree == null)
				return PROCESS_INSTANCE;

			String sub = subtree.get(0);
			if (TYPE_MOLECULAR_FUNCTION.equals(sub))
				return FUNCTION_INSTANCE;
			else if (TYPE_CELLULAR_COMPONENT.equals(sub))
				return COMPONENT_INSTANCE;
			else if (TYPE_BIOLOGICAL_PROCESS.equals(sub))
				return PROCESS_INSTANCE;

			return null;
		}

		private MultiValueMap<String, List<String>> getUriParameters(URI uri) {
			MultiValueMap<String, List<String>> ret = new MultiValueMap<String, List<String>>();
			for (NameValuePair param : URLEncodedUtils.parse(uri, "UTF-8")) {
				ret.put(param.getName(), param.getValue());
			}
			return ret;
		}
	}

	private DataSource dataSource;
	private int prefetchSize = 1000;
	private String type;

	public ExternalOntologyGO(String type) {
		this.type = type;
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological
	 * processes
	 */
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		// check for GO:
		id = cleanId(id);
		if( StringUtils.isBlank(id))
			return null;
		List<ExternalItem> items = runQuery("SELECT acc, name FROM term WHERE acc = upper(?) and term_type = ? and is_obsolete = 0",
				prefetchSize, id, type);
		if (items.size() > 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique GO accesion", id));
		return items.get(0);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological
	 * processes
	 */
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		name = cleanName(name);
		if( StringUtils.isBlank(name))
			return null;
		List<ExternalItem> items = runQuery("SELECT acc, name FROM term WHERE name=lower(?) and term_type = ? and is_obsolete = 0",
				prefetchSize, name, type);
		if (items.size() > 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique GO term name", name));
		return items.get(0);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete terms. Uses
	 * a SQL LIKE.
	 */
	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
		name = cleanName(name);
		if( StringUtils.isBlank(name))
			return Collections.EMPTY_LIST;
		List<ExternalItem> items = runQuery("SELECT acc, name FROM term WHERE name like lower(?) and term_type = ? and is_obsolete = 0",
				limit, queryGenerator(name), type);
		return items;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Gene Ontology URL
	 */
	@Override
	public String getExternalURL(String id) {
		return String.format("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=%s", cleanId(id));
	}

	public String cleanId(String id) {
		id = super.cleanId(id);
		if (id.matches("^\\d+$"))
			id = "GO:" + id;
		return id;
	}

	protected List<ExternalItem> processResultSet(ResultSet rs, int limit) throws SQLException {
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

	/**
	 * trims and adds a % to the end of the term if it doesn't already have one.
	 */
	@Override
	public String queryGenerator(String term) {
		term = super.queryGenerator(term);
		if (!term.endsWith("%"))
			term += "%";
		return term;
	}

	protected List<ExternalItem> runQuery(String sql, int limit, String term, String type) throws ExternalOntologyException {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = getDataSource().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, type);
			ps.setFetchSize(Math.max(prefetchSize, limit));
			rs = ps.executeQuery();
			List<ExternalItem> items = processResultSet(rs, limit);
			return items;
		} catch (SQLException ex) {
			throw new ExternalOntologyException(ex);
		} finally {
			SQLUtil.closeQuietly(rs);
			SQLUtil.closeQuietly(conn);
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}