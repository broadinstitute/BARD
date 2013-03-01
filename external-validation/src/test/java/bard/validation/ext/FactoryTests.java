package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class FactoryTests {

	public FactoryTests() {
		BasicConfigurator.configure();
	}
	
	@Test
	public void testPubChem() throws ExternalOntologyException {
		ExternalOntologyAPI api = ExternalOntologyFactory.getExternalOntologyAPI("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=");
		assertEquals(String.format("%s", api.getClass()), api.getClass().equals(ExternalOntologyNCBI.class));
	}
	
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.setProperty(ExternalOntologyFactory.NCBI_EMAIL, "southern@scripps.edu");
		props.setProperty(ExternalOntologyFactory.NCBI_TOOL, "BARD-CAP");
		args = new String[]{
				"http://amigo.geneontology.org/cgi-bin/amigo/gp-details.cgi?gp=FB:FBgn",
				"http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=",
				"http://cas.org/",
				"http://omim.org/entry/",
				"http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=",
				"http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=",
				"http://regid.org/find",
				"http://www.atcc.org/ATCCAdvancedCatalogSearch/ProductDetails/tabid/452/Default.aspx?ATCCNum=",
				"http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=",
				"http://www.ncbi.nlm.nih.gov/biosystems/",
				"http://www.ncbi.nlm.nih.gov/gene/",
				"http://www.ncbi.nlm.nih.gov/gquery/?term=",
				"http://www.ncbi.nlm.nih.gov/mesh/",
				"http://www.ncbi.nlm.nih.gov/nuccore/",
				"http://www.ncbi.nlm.nih.gov/protein/",
				"http://www.ncbi.nlm.nih.gov/pubmed/",
				"http://www.ncbi.nlm.nih.gov/structure/?term=",
				"http://www.uniprot.org/uniprot/",
				"https://mli.nih.gov/mli/?dl_id="
		};
		for(String arg: args) {
			try {
				ExternalOntologyAPI api = ExternalOntologyFactory.getExternalOntologyAPI(arg, props);
				System.out.println(String.format("%s\t%s", arg, api.getClass()));
			}
			catch(ExternalOntologyException ex) {
				System.err.println(String.format("%s\t%s", arg, ex.getMessage()));
			}
		}
	}
}
