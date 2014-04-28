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

package bard.pubchem.test;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import bard.pubchem.model.PCAssay;
import bard.pubchem.model.PCAssayXRef;
import bard.pubchem.model.XRef;
import bard.pubchem.xml.PubChemXMLParserFactory;

@RunWith(Parameterized.class)
public class PubChemXMLParserTest {

	private int aid;

	@BeforeClass
	public static void initialize() {
		BasicConfigurator.configure();
	}
	
	
	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] { { 467 } };
		return Arrays.asList(data);
	}

	public PubChemXMLParserTest(int aid) {
		this.aid = aid;
	}

	@Test
	public void testXmlParser() throws Exception {
		PCAssay assay = PubChemXMLParserFactory.getInstance().parsePublicAssayDescription(aid);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("AID: %s%n", assay.getAID()));
		sb.append(String.format("\tName: %s%n", assay.getName()));
		sb.append(String.format("\tDescription: %s%n", assay.getDescription().substring(0, 76) + "..."));
		sb.append(String.format("\tProtocol: %s%n", assay.getProtocol().substring(0, 76) + "..."));
		sb.append(String.format("\tComment: %s%n", assay.getComment().substring(0, 76) + "..."));
		for(PCAssayXRef axref: assay.getAssayXRefs()) {
			XRef xref = axref.getXRef();
			if( xref.getDatabase().equals("pubmed")) {
				sb.append(String.format("\tPMID: %s%n", xref.getXRefId()));
			}
		}
		System.out.println(sb.toString());
	}
}
