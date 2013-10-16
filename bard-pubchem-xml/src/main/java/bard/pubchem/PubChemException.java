package bard.pubchem;

public class PubChemException extends Exception {
	
	public PubChemException(String message) {
		super(message);
	}
	
	public PubChemException(String message, Exception ex) {
		super(message, ex);
	}
	
	public PubChemException(Exception ex) {
		super(ex);
	}
}
