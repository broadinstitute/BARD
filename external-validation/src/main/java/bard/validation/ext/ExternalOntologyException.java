package bard.validation.ext;

/**
 * Exception wrapper hiding propagation of exceptions from specific
 * implementations
 * 
 * @author southern
 * 
 */
public class ExternalOntologyException extends Exception {

	public ExternalOntologyException(Exception ex) {
		super(ex);
	}

	public ExternalOntologyException(String message) {
		super(message);
	}

	public ExternalOntologyException(String message, Exception ex) {
		super(message, ex);
	}
}