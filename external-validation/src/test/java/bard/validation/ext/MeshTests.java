package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MeshTests {

	public MeshTests() {
		BasicConfigurator.configure();
		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
		logger.setLevel(Level.INFO);
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		String term = "breast neoplasms*";
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("mesh", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching(term);
		for (ExternalItem item : items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(term + " in the gene database should return items", items.size() > 0, true);
		System.out.println(String.format("%s items returned for query '%s'", term, items.size()));
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
	}
	
	@Test
	public void testFindMatchingQuoted() throws ExternalOntologyException {
		String term = "\"breast neoplasms\"*";
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("mesh", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching(term);
		for (ExternalItem item : items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(term + " in the gene database should return items", items.size() > 0, true);
		System.out.println(String.format("%s items returned for query '%s'", term, items.size()));
		System.out.println("testFindMatchingQuoted took (ms): " + (System.currentTimeMillis() - start));
	}
}