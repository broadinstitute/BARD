package bard.validation.ext;

import java.util.List;

/**
 * Contract for External Ontology implementations
 * @author southern
 *
 */
public interface ExternalOntologyAPI {

	/**
	 * given name is searched against the underlying external ontology 
	 */
	public ExternalItem findByName(String name) throws ExternalOntologyException;
	
	/**
	 * given id is searched against the underlying external ontology 
	 */
	public ExternalItem findById(String id) throws ExternalOntologyException;
	
	/**
	 * given term is searched against the underlying external ontology system
	 * the term is first run through the <b>queryGenerator</b> method so it is suitable for the given system 
	 */
	public List<ExternalItem> findMatching(String term) throws ExternalOntologyException;
	
	/**
	 * formats a query term. E.g. adds % for SQL LIKE, trims the term etc. 
	 */
	public String queryGenerator(String term);
	
	/**
	 * returns the URL of the external system where the user can search for terms. 
	 */
	public String getExternalURL();
}