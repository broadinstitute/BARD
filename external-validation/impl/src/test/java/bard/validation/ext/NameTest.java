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

import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

@RunWith(Parameterized.class)
public class NameTest {

	private String uri;
	private String name;
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

	public NameTest(String uri, String name, Class clazz) {
		this.uri = uri;
		this.name = name;
		this.clazz = clazz;
	}

	@Test
	public void testName() throws ExternalOntologyException {
		Properties props = new Properties();
		props.setProperty(ExternalOntologyNCBI.NCBI_EMAIL, "southern@scripps.edu");
		props.setProperty(ExternalOntologyNCBI.NCBI_TOOL, "BARD-CAP");
		ExternalOntologyAPI api = RegisteringExternalOntologyFactory.getInstance().getExternalOntologyAPI(uri, props);
		try {
			ExternalItem item = api.findById(name);
			assertEquals(item == null, true);
		}
		catch(Exception ex) {
			boolean result = ex.getClass().equals(clazz);
			String msg = String.format("%s\t%s\t%s\t%s", result, uri, name, ex.getMessage());
			System.out.println(msg);
			assertEquals(msg, result, true);			
		}
	}
}
