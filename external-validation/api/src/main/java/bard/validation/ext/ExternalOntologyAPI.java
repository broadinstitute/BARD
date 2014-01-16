package bard.validation.ext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/18/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExternalOntologyAPI {
    /**
     * given id is searched against the underlying external ontology
     */
    ExternalItem findById(String id) throws ExternalOntologyException;

    /**
     * given name is searched against the underlying external ontology
     */
    ExternalItem findByName(String name) throws ExternalOntologyException;

    List<ExternalItem> findMatching(String term) throws ExternalOntologyException;

    List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException;

    /**
     * returns the URL of the external system where the user can search for
     * terms.
     */
    String getExternalURL(String id);

    String queryGenerator(String term);

    String cleanId(String id);

    String cleanName(String name);

    /**
     * @param potentialId Note, this have already been cleaned if needed
     * @return true if the string looks like an reasonable id for this resource, otherwise false
     */
    boolean matchesId(String potentialId);

    boolean validate(String name, String id) throws ExternalOntologyException;
}
