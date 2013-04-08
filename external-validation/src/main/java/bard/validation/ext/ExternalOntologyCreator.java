package bard.validation.ext;

import java.net.URI;
import java.util.Properties;

public interface ExternalOntologyCreator {

	public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException;
}
