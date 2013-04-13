package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

@RunWith(Parameterized.class)
public class IdTests {

	private String uri;
	private String id;
	private Class clazz;

	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] {
				{ "http://www.ncbi.nlm.nih.gov/gene/", null, ExternalOntologyException.class },
				{ "http://www.ncbi.nlm.nih.gov/gene/", " ", ExternalOntologyException.class },
				{ "http://www.ncbi.nlm.nih.gov/gene/", "", ExternalOntologyException.class },
				{ "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", null, ExternalOntologyException.class },
				{ "http://www.uniprot.org/uniprot/", null, ExternalOntologyException.class },
				{ "http://www.uniprot.org/uniprot/", " ", RemoteDataAccessException.class },
				{ "http://www.uniprot.org/uniprot/", "", RemoteDataAccessException.class }

		};
		return Arrays.asList(data);
	}

	public IdTests(String uri, String id, Class clazz) {
		this.uri = uri;
		this.id = id;
		this.clazz = clazz;
	}

	@Test
	public void testId() throws ExternalOntologyException {
		Properties props = new Properties();
		props.setProperty(ExternalOntologyNCBI.NCBI_EMAIL, "southern@scripps.edu");
		props.setProperty(ExternalOntologyNCBI.NCBI_TOOL, "BARD-CAP");
		ExternalOntologyAPI api = RegisteringExternalOntologyFactory.getInstance().getExternalOntologyAPI(uri, props);
		try {
			ExternalItem item = api.findById(id);
			assertEquals(item == null, true);
		}
		catch(Exception ex) {
			boolean result = ex.getClass().equals(clazz);
			String msg = String.format("%s\t%s\t%s\t%s", result, uri, id, ex.getMessage());
			System.out.println(msg);
			assertEquals(msg, result, true);			
		}
	}
}
