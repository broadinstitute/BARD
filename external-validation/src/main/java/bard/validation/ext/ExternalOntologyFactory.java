package bard.validation.ext;

import java.net.URI;
import java.net.URISyntaxException;

public class ExternalOntologyFactory {

	public static ExternalOntologyAPI getExternalOntologyAPI(String url) throws URISyntaxException {
		return getExternalOntologyAPI(new URI(url));
	}
	
	public static ExternalOntologyAPI getExternalOntologyAPI(URI uri) {
		uri.getHost().equals("www.nlm.ncbi.nih.gov");
		return null;
	}

}
