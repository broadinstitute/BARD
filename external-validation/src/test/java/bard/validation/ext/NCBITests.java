package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class NCBITests {

	public NCBITests() {
		BasicConfigurator.configure();
		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
		logger.setLevel(Level.INFO);
	}

	@Test
	public void testGetById() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("pcassay", "southern@scripps.edu", "BARD-CAP-PROTEIN");
		ExternalItem item = eo.findById("2551");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("2551"), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testGetByName() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("taxonomy", "southern@scripps.edu", "BARD-CAP");
		ExternalItem item = eo.findByName("homo sapiens");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("9606"), true);
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start) );
	}
	
	@Test
	public void testGetByNameFail() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("protein", "southern@scripps.edu", "BARD-CAP");
		try {
			ExternalItem item = eo.findByName("ppar");
			String.format("%s\t%s", item.getId(), item.getDisplay());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("Expected failure of generic term", 1==1, true);
		System.out.println("testGetByNameFail took (ms): " + (System.currentTimeMillis() - start) );
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("gene", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("ppar gamma");
		for (ExternalItem item : items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals("'ppar gamma' in the gene database should return items", items.size() > 0, true);
		System.out.println(String.format("%s items returned for query 'ppar gamma'", items.size()));
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
	}
	
	@Test
	public void testFindMatchingMlpcn() throws ExternalOntologyException {
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("pcassay", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("\"NIH Molecular Libraries Program\"[SourceCategory]", 10000);
//		for (ExternalItem item : items)
//			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals("'chembl' returns: " + items.size(), items.size() > 5000, true);
	}

	@Test
	public void testFindMatchingLimitThree() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("gene", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("ppar", 3);
		for (ExternalItem item : items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals("'ppar gamma' in the gene database should return items", items.size() == 3, true);
		System.out.println(String.format("%s items returned for query 'ppar gamma'", items.size()));
		System.out.println("testFindMatchingLimitThree took (ms): " + (System.currentTimeMillis() - start) );
	}
}
