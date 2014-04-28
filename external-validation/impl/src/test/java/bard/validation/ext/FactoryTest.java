/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
