package bard.validation.ext;

import java.net.URI;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: ddurkin Date: 3/19/13 Time: 3:05 PM To
 * change this template use File | Settings | File Templates.
 */
public interface ExternalOntologyFactory {
	@Deprecated	String NCBI_EMAIL = "ncbi.email";
	@Deprecated String NCBI_TOOL = "ncbi.tool";

	/**
	 * 
	 * @param externalSite
	 * @return
	 * @throws ExternalOntologyException
	 */
	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException;

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException;
	
	public ExternalOntologyAPI getExternalOntologyAPI(URI externalSite, Properties props) throws ExternalOntologyException;
	
	public List<ExternalOntologyCreator> getCreators();
}
