package bard.validation.ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.Transformer;
import org.dom4j.Document;
import org.dom4j.Node;

import edu.scripps.fl.entrez.EUtilsException;
import edu.scripps.fl.entrez.EUtilsWeb;
import edu.scripps.fl.entrez.transformer.EntrezTransformerFactory;

public class ExternalOntologyNCBI extends ExternalOntologyAPI {

	private String database;
	private EUtilsWeb eutils;
	private Transformer transformer;
	private static Set<String> databases = new HashSet();

	static {
		try {
			EUtilsWeb web = new EUtilsWeb("BARD-CAP", "anonymous@bard.nih.gov");
			databases.addAll(web.getDatabases());
		}
		catch(EUtilsException ex) {
			throw new RuntimeException(ex);
		}
	}

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
		if( ! databases.contains(database) )
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
			List<Long> ids = eutils.getIds(name, database);
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
	public List<ExternalItem> findMatching(String term) throws ExternalOntologyException {
		try {
			List<Long> ids = eutils.getIds(queryGenerator(term), database);
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