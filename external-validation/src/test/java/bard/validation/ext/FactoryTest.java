package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FactoryTest {

	private String uri;
	private Boolean expected;

	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] {
//				{ "http://www.ncbi.nlm.nih.gov/gquery/?term=", false }, not a valid ncbi database
				{ "http://disease-ontology.org", true },
				{ "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", true },
				{ "http://amigo.geneontology.org/cgi-bin/amigo/gp-details.cgi?gp=FB:FBgn", true },
				{ "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=", true },
				{ "http://www.atcc.org/ATCCAdvancedCatalogSearch/ProductDetails/tabid/452/Default.aspx?ATCCNum=", true },
				{ "http://cas.org/", false },
				{ "http://omim.org/entry/", true},
				{ "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=", true },
				{ "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=", true },
				{ "http://regid.org/find", false },
				{ "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=", true },
				{ "http://www.ncbi.nlm.nih.gov/biosystems/", true },
				{ "http://www.ncbi.nlm.nih.gov/gene/", true },
				{ "http://www.ncbi.nlm.nih.gov/mesh/", true },
				{ "http://www.ncbi.nlm.nih.gov/nuccore/", true },
				{ "http://www.ncbi.nlm.nih.gov/protein/", true },
				{ "http://www.ncbi.nlm.nih.gov/pubmed/", true },
				{ "http://www.ncbi.nlm.nih.gov/structure/?term=", true },
				{ "http://www.uniprot.org/uniprot/", true },
				{ "https://mli.nih.gov/mli/?dl_id=", false } };
		return Arrays.asList(data);
	}

	public FactoryTest(String uri, Boolean expected) {
		this.uri = uri;
		this.expected = expected;
	}

	@Test
	public void testUri() throws ExternalOntologyException {
		Properties props = new Properties();
		props.setProperty(ExternalOntologyNCBI.NCBI_EMAIL, "southern@scripps.edu");
		props.setProperty(ExternalOntologyNCBI.NCBI_TOOL, "BARD-CAP");
		ExternalOntologyFactory factory = RegisteringExternalOntologyFactory.getInstance(); 
		ExternalOntologyAPI api = factory.getExternalOntologyAPI(uri, props);
		System.out.println(String.format("%s\t%s", api != null, uri));
		assertEquals(String.format("%s\t%s", api != null, uri), api != null, expected);
	}

}
