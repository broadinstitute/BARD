package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.ols.soap.Query;
import uk.ac.ebi.ols.soap.QueryService;
import uk.ac.ebi.ols.soap.QueryServiceLocator;

public class ExternalOntologyOLS extends ExternalOntologyAPI {

	public static class OLSCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			if (host.endsWith("geneontology.org"))
				return new ExternalOntologyOLS("GO");
			return null;
		}
	}

	private static QueryService locator;

	private static Map<String, String> ontologyNames;

	private String ontology;

	public ExternalOntologyOLS(String ontology) throws ExternalOntologyException {
		getLocator(); // initialize
		ontology = ontology.toUpperCase();
		if (!ontologyNames.keySet().contains(ontology))
			throw new ExternalOntologyException(String.format("Ontology '%s' not known by Ontology Lookup Service", ontology));
		this.ontology = ontology;
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			id = cleanId(id);
			if (StringUtils.isBlank(id))
				return null;
			Query qs = getLocator().getOntologyQuery();
			String term = qs.getTermById(id, ontology);
			if (term.equals(id)) // couldn't find it
				return null;
			return new ExternalItem(id, term);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		try {
			name = cleanName(name);
			if (StringUtils.isBlank(name))
				return null;
			Query qs = getLocator().getOntologyQuery();
			Map<String, String> map = qs.getTermsByExactName(name, ontology);
			if (map.size() == 0)
				return null;
			return getExternalItems(map, 1).get(0);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
		try {
			term = cleanName(term);
			if (StringUtils.isBlank(term))
				return Collections.EMPTY_LIST;
			term = queryGenerator(term);
			Query qs = getLocator().getOntologyQuery();
			Map<String, String> map = qs.getTermsByName(term, ontology, false);
			return getExternalItems(map, limit);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	protected List<ExternalItem> getExternalItems(Map<String, String> map, int limit) {
		List<ExternalItem> items = new ArrayList(map.size());
		int count = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			ExternalItem item = new ExternalItem(entry.getKey(), entry.getValue());
			items.add(item);
			if (limit > 0 && ++count >= limit)
				break;
		}
		return items;
	}

	public String getExternalURL(String id) {
		id = cleanId(id);
		if ("GO".equals(ontology))
			return String.format("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=%s", id);
		else
			return String.format("http://www.ebi.ac.uk/ontology-lookup/?termId=", id);
	}

	protected QueryService getLocator() throws ExternalOntologyException {
		if (locator == null) {
			synchronized (ExternalOntologyOLS.class) { // 1
				if (locator == null) {
					synchronized (ExternalOntologyOLS.class) { // 3
						try {
							locator = new QueryServiceLocator();
						} catch (Exception ex) {
							throw new ExternalOntologyException("Could not create Ontology Lookup Service", ex);
						}
						try {
							ontologyNames = locator.getOntologyQuery().getOntologyNames();
						} catch (Exception ex) {
							throw new ExternalOntologyException("Could not obtain Ontology Names from Ontology Lookup Service", ex);
						}
					}
				}
			}
		}
		return locator;
	}

	public String cleanId(String id) {
		id = super.cleanId(id);
		if ("GO".equals(ontology) && id.matches("^\\d+$"))
			id = "GO:" + id;
		return id;
	}
}
