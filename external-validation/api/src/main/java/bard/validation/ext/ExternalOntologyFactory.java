package bard.validation.ext;

import java.net.URI;
import java.util.List;
import java.util.Properties;

public interface ExternalOntologyFactory {
	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException;

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException;
	
	public ExternalOntologyAPI getExternalOntologyAPI(URI externalSite, Properties props) throws ExternalOntologyException;
	
	public List<ExternalOntologyCreator> getCreators();
}
