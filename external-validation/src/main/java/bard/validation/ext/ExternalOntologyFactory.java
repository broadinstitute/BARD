package bard.validation.ext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class ExternalOntologyFactory {

	public static final String NCBI_EMAIL = "ncbi.email";
	public static final String NCBI_TOOL = "ncbi.tool";

	public static ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException {
		return getExternalOntologyAPI(externalSite, new Properties());
	}

	private static MultiValueMap getUriParameters(URI uri) {
		MultiValueMap ret = new MultiValueMap();
		for (NameValuePair param : URLEncodedUtils.parse(uri, "UTF-8")) {
			ret.put(param.getName(), param.getValue());
		}
		return ret;
	}

	private static ExternalOntologyNCBI initNCBI(String db, Properties props) throws ExternalOntologyException {
		if (!props.containsKey(NCBI_EMAIL))
			throw new ExternalOntologyException(String.format("%s property required for ExternalOntologyNCBI implementation", NCBI_EMAIL));
		if (!props.containsKey(NCBI_TOOL))
			throw new ExternalOntologyException(String.format("%s property required for ExternalOntologyNCBI implementation", NCBI_TOOL));
		return new ExternalOntologyNCBI(db, props.getProperty(NCBI_TOOL), props.getProperty(NCBI_EMAIL));
	}

	public static ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException {
		URI uri = null;
		try {
			uri = new URI(externalSite);
		} catch (URISyntaxException ex) {
			throw new ExternalOntologyException(ex);
		}
		uri.normalize();
		String host = uri.getHost();
		String[] path = uri.getPath().split("/");
		if ("www.ncbi.nlm.nih.gov".equals(host)) {
			return initNCBI(path[1].toLowerCase(), props);
		} else if (host.endsWith("omim.org")) {
			return initNCBI("omim", props);
		} else if ("pubchem.ncbi.nlm.nih.gov".equals(host)) {
			MultiValueMap params = getUriParameters(uri);
			if (params.containsKey("cid"))
				return initNCBI("pccompound", props);
			if (params.containsKey("sid"))
				return initNCBI("pcsubstance", props);
			if (params.containsKey("aid"))
				return initNCBI("pcassay", props);
		} else if (host.endsWith("geneontology.org")) {
			return new ExternalOntologyOLS("GO");
		} else if (host.endsWith("uniprot.org")) {
			return new ExternalOntologyUniprot();
		}
		throw new ExternalOntologyException("Cannot determine External Ontology API from: " + uri);
	}

}