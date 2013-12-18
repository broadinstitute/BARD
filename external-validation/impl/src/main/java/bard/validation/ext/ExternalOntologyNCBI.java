package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import bard.util.TreeUtil;
import edu.scripps.fl.entrez.EUtilsException;
import edu.scripps.fl.entrez.EUtilsWeb;
import edu.scripps.fl.entrez.transformer.EntrezTransformerFactory;

public class ExternalOntologyNCBI extends AbstractExternalOntology {

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

	private static Set<String> databases = null;
	public static String NCBI_EMAIL = "ncbi.email";
	public static String NCBI_TOOL = "ncbi.tool";

	protected int chunkSize = 100000;
	protected String database;
	protected EUtilsWeb eutils;
	protected Transformer transformer;

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
		init(database, tool, email);
		this.database = database;
		if (!databases.contains(database))
			throw new ExternalOntologyException("Unknown NCBI database " + database);
		this.transformer = EntrezTransformerFactory.getTransformer(database);
		if (transformer == null)
			throw new ExternalOntologyException("Cannot find Transformer for NCBI database " + database);
		eutils = new EUtilsWeb(tool, email);
	}

	private void init(String databaase, String tool, String email) throws ExternalOntologyException {
		try {
			if (databases == null)
				synchronized (ExternalOntologyNCBI.class) {
					if (databases == null)
						databases = new EUtilsWeb(tool, email).getDatabases();
				}
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	/**
	 * Finds Id and Display name for provided NCBI GI number
	 */
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			id = cleanId(id);
			if (StringUtils.isBlank(id)){
                return null;
            }
			Document doc = eutils.getSummary(id, database);
			List<ExternalItem> items = processSummaries(doc);
			if (items.size() > 1){
                throw new ExternalOntologyException(String.format("'%s' is not unique for NCBI %s database", id, database));
            }
            else if (items.size() == 1 ){
                return items.get(0);
            }
            else {
                return null;
            }
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
			name = cleanName(name);
			if (StringUtils.isBlank(name))
				return null;
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
            final ArrayList<ExternalItem> externalItems = new ArrayList<ExternalItem>();
			final String cleanTerm = cleanName(term);
            if(matchesId(cleanTerm)){
                final ExternalItem itemById = findById(cleanTerm);
                if(itemById!=null){
                    externalItems.add(itemById);
                }
            }
            if(StringUtils.isNotBlank(cleanTerm) && externalItems.isEmpty()){
                int chunk = limit > 0 & chunkSize > limit ? limit : chunkSize;
                List<Long> ids = (List<Long>) eutils.getIds(queryGenerator(cleanTerm), database, new ArrayList<Long>(), chunk, limit);
                if (ids.size() > 0){
                    Document doc = eutils.getSummariesAsDocument(ids, database);
                    if (database.equals("mesh")){
                        externalItems.addAll(processMeSHSummaries(doc));
                    }
                    else {
                        externalItems.addAll(processSummaries(doc));
                    }
                }

            }
            return externalItems;
		} catch (EUtilsException ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	@Override
	public String queryGenerator(String term) {
		term = StringUtils.defaultString(term);
		term = term.replaceAll("^\\s+", "");
		if (!term.endsWith("*"))
			term += "*";
		return term;
	}

    @Override
    public boolean matchesId(String potentialId) {
        return NumberUtils.isDigits(potentialId);
    }

    /**
	 * URL of specific NCBI Entrez Database
	 */
	@Override
	public String getExternalURL(String id) {
		if ("taxonomy".equals(database))
			return String.format("http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=%s", id);
		return String.format("http://www.ncbi.nlm.nih.gov/%s/%s", database, id);
	}

	protected List<ExternalItem> processSummaries(Document doc) {
		List<ExternalItem> list = new ArrayList<ExternalItem>();
		for (Node node : (List<Node>) doc.selectNodes("/eSummaryResult/DocumentSummarySet/DocumentSummary")) {
			String name = (String) transformer.transform(node);
			String uid = node.valueOf("@uid");
			ExternalItem item = new ExternalItemImpl(uid, name);
			list.add(item);
		}
		return list;
	}

	protected List<ExternalItem> processMeSHSummaries(Document doc) {
		List<Node> nodes = doc.selectNodes("/eSummaryResult/DocumentSummarySet/DocumentSummary");
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		final Map<String, String> idToNameMap = new HashMap<String, String>();
		Map<String, DefaultMutableTreeNode> idToNodeMap = new HashMap<String, DefaultMutableTreeNode>();
		for (Node node : nodes) {
			String id = node.valueOf("@uid");
			// use the first Mesh Term as the name
			String name = node.valueOf("DS_MeshTerms/string[1]/text()");
			idToNameMap.put(id, name);
			DefaultMutableTreeNode currentTerm = null;
			if (idToNodeMap.containsKey(id))
				currentTerm = idToNodeMap.get(id);
			else {
				currentTerm = new DefaultMutableTreeNode(id);
				idToNodeMap.put(id, currentTerm);
				root.add(currentTerm);
			}
			List<Node> children = node.selectNodes("DS_IdxLinks/LinksType/Children/int");
			for (Node child : children) {
				String childId = child.getText();
				if (Pattern.matches("^\\s+$", childId))
					continue;
				DefaultMutableTreeNode childTerm = null;
				if (idToNodeMap.containsKey(childId)) {
					childTerm = idToNodeMap.get(childId);
				} else {
					childTerm = new DefaultMutableTreeNode(childId);
					idToNodeMap.put(childId, childTerm);
				}
				currentTerm.add(childTerm);
			}
		}
		TreeUtil.sortTreeDepthFirst(root, new Comparator<DefaultMutableTreeNode>() {
			public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
				String s1 = idToNameMap.get(o1.getUserObject());
				String s2 = idToNameMap.get(o2.getUserObject());
				// null check as some terms may not have a name as they found
				// via Children/int rather than a DocumentSummary
				return ObjectUtils.compare(StringUtils.lowerCase(s1), StringUtils.lowerCase(s2));
			}
		});
		List<ExternalItem> list = new ArrayList<ExternalItem>();
		for (Enumeration<DefaultMutableTreeNode> enu = root.preorderEnumeration(); enu.hasMoreElements();) {
			DefaultMutableTreeNode node = enu.nextElement();
			if (node == root)
				continue;
			TreeNode[] path = node.getPath();
			StringBuffer sb = new StringBuffer();
			boolean skip = false;
			for (int ii = 1; ii < path.length; ii++) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path[ii];
				if (ii > 1)
					sb.append("> ");
				String name = idToNameMap.get(n.getUserObject());
				// final node may not have a name if it was seen via a
				// Children/int node rather than a DocumentSummary
				if (name == null)
					skip = true; // in which case we don't want to display it
				sb.append(name);
			}
			if (!skip)
				list.add(new ExternalItemImpl(node.getUserObject().toString(), sb.toString()));
		}
		return list;
	}
}