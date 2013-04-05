package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import edu.scripps.fl.entrez.EUtilsException;
import edu.scripps.fl.entrez.EUtilsWeb;
import edu.scripps.fl.entrez.transformer.EntrezTransformerFactory;

public class ExternalOntologyNCBI extends ExternalOntologyAPI {

	public static class NCBICreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			String[] path = uri.getPath().split("/");
			if ("www.ncbi.nlm.nih.gov".equals(host)) {
				return initNCBI(path[1].toLowerCase(), props);
			} else if (host.endsWith("omim.org")) {
				return initNCBI("omim", props);
			} else if ("pubchem.ncbi.nlm.nih.gov".equals(host)) {
				MultiValueMap params = getUriParameters(uri);
				if (params.containsKey("cid"))
					return initNCBI("pccompound", props);
				if (params.containsKey("sid"))
					return initNCBI("pcsubstance", props);
				if (params.containsKey("aid"))
					return initNCBI("pcassay", props);
			}
			return null;
		}

		private MultiValueMap getUriParameters(URI uri) {
			MultiValueMap ret = new MultiValueMap();
			for (NameValuePair param : URLEncodedUtils.parse(uri, "UTF-8")) {
				ret.put(param.getName(), param.getValue());
			}
			return ret;
		}

		private ExternalOntologyNCBI initNCBI(String db, Properties props) throws ExternalOntologyException {
			if (!props.containsKey(NCBI_EMAIL))
				throw new ExternalOntologyException(String.format("%s property required for ExternalOntologyNCBI creation", NCBI_EMAIL));
			if (!props.containsKey(NCBI_TOOL))
				throw new ExternalOntologyException(String.format("%s property required for ExternalOntologyNCBI creation", NCBI_TOOL));
			return new ExternalOntologyNCBI(db, props.getProperty(NCBI_TOOL), props.getProperty(NCBI_EMAIL));
		}
	}
	private static Set<String> databases = new HashSet<String>();
	public static String NCBI_EMAIL = "ncbi.email";
	public static String NCBI_TOOL = "ncbi.tool";
	static {
		try {
			EUtilsWeb web = new EUtilsWeb("BARD-CAP", "anonymous@bard.nih.gov");
			databases.addAll(web.getDatabases());
		} catch (EUtilsException ex) {
			throw new RuntimeException(ex);
		}
	}

	private int chunkSize = 100000;
	private String database;

	private EUtilsWeb eutils;

	private Transformer transformer;

	/**
	 * 
	 * @param database
	 *            - valid string for NCBI Entrez database e.g. protein, gene.
	 *            See: http://eutils.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi
	 * @param tool
	 *            - unique string to identify code calling the entrez web
	 *            service
	 * @param email
	 *            - email address of user for whom the code is run.
	 * @throws ExternalOntologyException
	 */
	public ExternalOntologyNCBI(String database, String tool, String email) throws ExternalOntologyException {
		this.database = database;
		if (!databases.contains(database))
			throw new ExternalOntologyException("Unknown NCBI database " + database);
		this.transformer = EntrezTransformerFactory.getTransformer(database);
		if (transformer == null)
			throw new ExternalOntologyException("Cannot find Transformer for NCBI database " + database);
		eutils = new EUtilsWeb(tool, email);
	}

	/**
	 * Finds Id and Display name for provided NCBI GI number
	 */
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			Document doc = eutils.getSummary(id, database);
			List<ExternalItem> items = processSummaries(doc);
			if (items.size() > 1)
				throw new ExternalOntologyException(String.format("'%s' is not unique for NCBI %s database", id, database));
			return items.get(0);
		} catch (EUtilsException ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	/**
	 * finds id and display name for provided search term. Throws an
	 * ExternalOntologyException if the name is not a unique term.
	 */
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		try {
			List<Long> ids = (List<Long>) eutils.getIds(name, database, new ArrayList<Long>(), 2, 2);
			if (ids.size() > 1)
				throw new ExternalOntologyException(String.format("Name '%s' is not unique for NCBI %s database", name, database));
			Document doc = eutils.getSummary(ids.get(0), database);
			return processSummaries(doc).get(0);
		} catch (EUtilsException ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	/**
	 * finds id and display name pairs for given search term. Term can be any
	 * valid NCBI Entrez query.
	 */
	@Override
	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
		try {
			int chunk = limit > 0 & chunkSize > limit ? limit : chunkSize;
			List<Long> ids = (List<Long>) eutils.getIds(queryGenerator(term), database, new ArrayList<Long>(), chunk, limit);
			Document doc = eutils.getSummariesAsDocument(ids, database);
			return processSummaries(doc);

		} catch (EUtilsException ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	/**
	 * URL of specific NCBI Entrez Database
	 */
	@Override
	public String getExternalURL(String id) {
		return String.format("http://www.ncbi.nlm.nih.gov/%s/%s", database, id);
	}

	protected List<ExternalItem> processSummaries(Document doc) {
		List<ExternalItem> list = new ArrayList<ExternalItem>();
		for (Node node : (List<Node>) doc.selectNodes("/eSummaryResult/DocumentSummarySet/DocumentSummary")) {
			String name = (String) transformer.transform(node);
			String uid = node.valueOf("@uid");
			ExternalItem item = new ExternalItem(uid, name);
			list.add(item);
		}
		return list;
	}

	/**
	 * default operation. Trims the string but nothing else
	 */
	@Override
	public String queryGenerator(String term) {
		term = term.trim();
		return term;
	}
}