package bard.validation.ext;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

/**
 * Implementation for GO ontology via SQL. Default instantiation makes a
 * connection to a public mysql database at the EBI.
 * 
 * @author southern
 * 
 */
public class ExternalOntologyGOGeneProduct extends ExternalOntologyAPI {

	public static class GOCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			// http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=
			if (!"amigo.geneontology.org".equals(host))
				return null;
			String path = uri.getPath();
			if(path.endsWith("gp-details.cgi"))
				return new ExternalOntologyGOGeneProduct();
			return null;
		}

	}

	private DataSource dataSource;
	private int prefetchSize = 1000;
	private Pattern idPattern = Pattern.compile("^([^:]+):([^\\(]+).*$");

	public ExternalOntologyGOGeneProduct() {
		setDataSource(ExternalOntologyGO.GO_DATASOURCE);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological
	 * processes
	 */
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		id = cleanId(id);
		if (StringUtils.isBlank(id))
			return null;

		Matcher matcher = idPattern.matcher(id);
		if (!matcher.matches())
			throw new ExternalOntologyException(String.format("Could not determine Xref database and key from '%s'", id));

		String db = matcher.group(1);
		String key = matcher.group(2).trim();

		List<ExternalItem> items = DBUtils
				.runQuery(
						getDataSource(),
						"select concat(dbx.xref_dbname, ':', dbx.xref_key) as id, concat(dbx.xref_dbname, ':', dbx.xref_key, ' (', gp.full_name, ', ', gp.symbol, ', ', s.genus, ' ', s.species, ')') as name"
								+ " from gene_product gp, species s, dbxref dbx"
								+ " where gp.species_id = s.id and gp.dbxref_id = dbx.id"
								+ " and dbx.xref_dbname = ? and dbx.xref_key = ?", prefetchSize, prefetchSize, db, key);
		if (items.size() > 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique GO accesion", id));
		else if (items.size() == 0)
			return null;
		return items.get(0);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete biological
	 * processes
	 */
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		name = cleanName(name);
		if (StringUtils.isBlank(name))
			return null;
		return findById(name);
	}

	/**
	 * searches TERM table for names and accessions for non-obsolete terms. Uses
	 * a SQL LIKE.
	 */
	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
		name = cleanName(name);
		if (StringUtils.isBlank(name))
			return Collections.EMPTY_LIST;
		name= queryGenerator(name);
		List<ExternalItem> items = DBUtils
				.runQuery(getDataSource(),
				// find matching query.
				// OR drives mysql crazy slow so we use a union instead.
				// non-binary strings like is automatically case insensitive
						"select concat(dbx.xref_dbname, ':', dbx.xref_key) as id, concat(dbx.xref_dbname, ':', dbx.xref_key, ' (', gp.full_name, ', ', gp.symbol, ', ', s.genus, ' ', s.species, ')') as name"
								+ " from gene_product gp, gene_product_synonym gps, species s, dbxref dbx"
								+ " where gp.id = gps.gene_product_id and gp.species_id = s.id and gp.dbxref_id = dbx.id"
								+ " and dbx.xref_key like ?"
								+ " union"
								+ " select concat(dbx.xref_dbname, ':', dbx.xref_key) as id, concat(dbx.xref_dbname, ':', dbx.xref_key, ' (', gp.full_name, ', ', gp.symbol, ', ', s.genus, ' ', s.species, ')') as name"
								+ " from gene_product gp, gene_product_synonym gps, species s, dbxref dbx"
								+ " where gp.id = gps.gene_product_id and gp.species_id = s.id and gp.dbxref_id = dbx.id"
								+ " and gps.product_synonym like ?", prefetchSize, limit, name, name);
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
		return String.format("http://amigo.geneontology.org/cgi-bin/amigo/gp-details.cgi?gp=%s", cleanId(id));
	}

	/**
	 * Ensure Id is not null, trimmed. If it doesn
	 */
	public String cleanId(String id) {
		id = super.cleanId(id);
		return id;
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}