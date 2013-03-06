package bard.validation.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.ols.soap.Query;
import uk.ac.ebi.ols.soap.QueryService;
import uk.ac.ebi.ols.soap.QueryServiceLocator;

public class ExternalOntologyOLS extends ExternalOntologyAPI {
	
	private static QueryService locator;
	private static Map<String,String> ontologyNames;
	
	private String ontology;
	
	public ExternalOntologyOLS(String ontology) throws ExternalOntologyException {
		getLocator(); // initialize
		ontology = ontology.toUpperCase();
		if( ! ontologyNames.keySet().contains(ontology) )
			throw new ExternalOntologyException(String.format("Ontology '%s' not known by Ontology Lookup Service", ontology));
		this.ontology = ontology;
	}
	
	protected QueryService getLocator() throws ExternalOntologyException {
		if (locator == null) {
			synchronized (ExternalOntologyOLS.class) { // 1
				if (locator == null) {
					synchronized (ExternalOntologyOLS.class) { // 3
						try {
							locator = new QueryServiceLocator();
						}
						catch(Exception ex) {
							throw new ExternalOntologyException("Could not create Ontology Lookup Service", ex);
						}
						try {
							ontologyNames = locator.getOntologyQuery().getOntologyNames();
						}
						catch(Exception ex) {
							throw new ExternalOntologyException("Could not obtain Ontology Names from Ontology Lookup Service", ex);
						}
					}
				}
			}
		}
		return locator;
	}
	
	protected List<ExternalItem> getExternalItems(Map<String, String> map, int limit) {
		List<ExternalItem> items = new ArrayList(map.size());
		int count = 0;
		for (Map.Entry<String, String> entry: map.entrySet()) {
			ExternalItem item = new ExternalItem(entry.getKey(), entry.getValue());
			items.add(item);
			if( limit > 0 && ++count >= limit )
				break;
		}
		return items;
	}
	
	public String idGenerator(String id) {
		if( id.matches("^\\d+$"))
			id = "GO:" + id;
		return id;
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			id = idGenerator(id);
			Query qs = getLocator().getOntologyQuery();
			String term = qs.getTermById(id, ontology);
			if( term.equals(id) ) // couldn't find it
				return null;
			return new ExternalItem(id, term);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		try {
			Query qs = getLocator().getOntologyQuery();
			Map<String, String> map = qs.getTermsByExactName(name, ontology);
			if( map.size() == 0 )
				return null;
			return getExternalItems(map, 1).get(0);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
		try {
			Query qs = getLocator().getOntologyQuery();
			Map<String, String> map = qs.getTermsByName(queryGenerator(term), ontology, false);
			return getExternalItems(map, limit);
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public String getExternalURL(String id) {
		if( "GO".equals( ontology ) )
			return String.format("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=%s", id);
		else 
			return String.format("http://www.ebi.ac.uk/ontology-lookup/?termId=",id);
	}

	public String queryGenerator(String term) {
		term = term.trim();
		return term;
	}

}
