package bard.validation.ext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import bard.validation.ext.util.GOUtil;

public class RegisteringExternalOntologyFactory implements ExternalOntologyFactory {

	private static RegisteringExternalOntologyFactory instance;

	public static ExternalOntologyFactory getInstance() throws ExternalOntologyException {
		if( instance == null )
			synchronized(RegisteringExternalOntologyFactory.class) {
				if( instance == null ) {
					instance = new RegisteringExternalOntologyFactory();
					instance.initialize();
				}
			}
		return instance;
	}

	protected void initialize() throws ExternalOntologyException {
		getCreators().add(new ExternalOntologyNCBI.NCBICreator());
		// instance.getCreators().add( new ExternalOntologyOLS.OLSCreator() );
		getCreators().add(new ExternalOntologyGO.GOCreator(GOUtil.getEBIDataSource()));
		getCreators().add(new ExternalOntologyGOGeneProduct.GOCreator(GOUtil.getEBIDataSource()));
		getCreators().add(new ExternalOntologyUniprot.UniprotCreator());
		getCreators().add(new ExternalOntologyATCC.ATCCCreator());
		getCreators().add(new ExternalOntologyDisease.DiseaseCreator());
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