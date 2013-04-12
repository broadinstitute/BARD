package bard.validation.ext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class RegisteringExternalOntologyFactory implements ExternalOntologyFactory {
	
	private static RegisteringExternalOntologyFactory instance;
	
	public static ExternalOntologyFactory getInstance() {
		return instance;
	}
	
	static {
		instance = new RegisteringExternalOntologyFactory();
		instance.getCreators().add( new ExternalOntologyNCBI.NCBICreator() );
//		instance.getCreators().add( new ExternalOntologyOLS.OLSCreator() );
		instance.getCreators().add( new ExternalOntologyGO.GOCreator() );
		instance.getCreators().add( new ExternalOntologyUniprot.UniprotCreator() );
		instance.getCreators().add( new ExternalOntologyATCC.ATCCCreator() );
	}

	private List<ExternalOntologyCreator> creators = new LinkedList<ExternalOntologyCreator>();

	public List<ExternalOntologyCreator> getCreators() {
		return creators;
	}

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException {
		return getExternalOntologyAPI(externalSite, new Properties());
	}

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException {
		URI uri = null;
		try {
			uri = new URI(externalSite);
		} catch (URISyntaxException ex) {
			throw new ExternalOntologyException(ex);
		}
		uri.normalize();
		return getExternalOntologyAPI(uri, props);
	}

	public ExternalOntologyAPI getExternalOntologyAPI(URI externalSite, Properties props) throws ExternalOntologyException {
		for (ExternalOntologyCreator creator : getCreators()) {
			ExternalOntologyAPI api = creator.create(externalSite, props);
			if (api != null)
				return api;
		}
		return null;
	}
}