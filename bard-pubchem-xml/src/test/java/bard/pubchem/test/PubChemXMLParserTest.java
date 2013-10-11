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
