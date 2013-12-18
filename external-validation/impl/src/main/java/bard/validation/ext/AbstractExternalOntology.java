package bard.validation.ext;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Contract for External Ontology implementations
 * 
 * @author southern
 * 
 */
public abstract class AbstractExternalOntology implements ExternalOntologyAPI {

    /**
	 * given term is searched against the underlying external ontology system
	 * the term is first run through the <b>queryGenerator</b> method so it is
	 * suitable for the given system
	 */
	@Override
    public List<ExternalItem> findMatching(String term) throws ExternalOntologyException {
		return findMatching(term, -1);
	}

    /**
	 * formats a query term. E.g. adds % for SQL LIKE, trims the term etc.
	 */
	@Override
    public String queryGenerator(String term) {
		return StringUtils.trimToEmpty(term);
	}
	
	@Override
    public String cleanId(String id) {
		return StringUtils.trimToEmpty(id);
	}
	
	@Override
    public String cleanName(String name) {
		return StringUtils.trimToEmpty(name);
	}

    @Override
    public boolean validate(String name, String id) throws ExternalOntologyException {
		ExternalItem item = findByName(cleanName(name));
		ExternalItem item2 = findById(cleanId(id));
		return item.equals(item2);
	}
}