package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;

public class ExternalOntologyDisease extends ExternalOntologyAPI {

	public static class DiseaseCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			if (host.endsWith("disease-ontology.org"))
				return new ExternalOntologyDisease();
			return null;
		}
	}

	private static OntologyService service;

	public ExternalOntologyDisease() throws ExternalOntologyException {
		service = getService();
	}
	
	protected OntologyService getService() throws ExternalOntologyException {
		if (service == null) {
			synchronized (ExternalOntologyDisease.class) { // 1
				if (service == null) {
					synchronized (ExternalOntologyDisease.class) { // 3
						try {
							service = new FileOntologyService(new URI("http://www.berkeleybop.org/ontologies/doid.obo"));
						} catch (Exception ex) {
							throw new ExternalOntologyException("Could not create Disease Ontology OntoCat service", ex);
						}
					}
				}
			}
		}
		return service;
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			id = cleanId(id);
			if (StringUtils.isBlank(id))
				return null;
			OntologyTerm term = service.getTerm(id);
			if( term == null )
				return null;
			return new ExternalItem(term.getAccession(), term.getLabel());
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		try {
			name = cleanName(name);
			if (StringUtils.isBlank(name))
				return null;
			List<OntologyTerm> terms = service.searchAll(name, SearchOptions.EXACT);
			if( terms == null || terms.size() != 1)
				return null;
			return new ExternalItem(terms.get(0).getAccession(), terms.get(0).getLabel());
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
		try {
			term = cleanName(term);
			if (StringUtils.isBlank(term))
				return Collections.EMPTY_LIST;
			term = term.toLowerCase();
			List<OntologyTerm> terms = service.searchAll(term, SearchOptions.INCLUDE_PROPERTIES);
			List<ExternalItem> items = new ArrayList<ExternalItem>(terms.size());
			for(OntologyTerm oterm: terms) {
				if( oterm.getLabel().toLowerCase().contains(term) )
					items.add( new ExternalItem(oterm.getAccession(), oterm.getLabel()) );
			}
			return items;
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	protected List<ExternalItem> getExternalItems(Map<String, String> map, int limit) {
		List<ExternalItem> items = new ArrayList<ExternalItem>(map.size());
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
		return "http://www.disease-ontology.org/";
	}

	public String cleanId(String id) {
		id = super.cleanId(id);
		return id;
	}
}