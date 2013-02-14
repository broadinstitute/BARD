package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class OLSTests {
	
	private static ExternalOntologyAPI eo;
	
	public OLSTests() {
		BasicConfigurator.configure();
		BasicConfigurator.configure();
		for(String name: new String[]{"org.apache.axis"}) {
			Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger(name);
			logger.setLevel(Level.INFO);
		}
	}
	
	@BeforeClass
	public static void initialize() throws ExternalOntologyException {
		eo = new ExternalOntologyOLS("GO");
	}
	
	@Test
	public void testInvalidOntology() {
		boolean thrown = false;
		try {
			new ExternalOntologyOLS("QWERTY");
		}
		catch(ExternalOntologyException ex) {
			ex.printStackTrace();
			thrown = true;
		}
		assertEquals(thrown, true);
	}
	
	@Test
	public void testFindById() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findById("GO:0009987");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("GO:0009987"), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}
	
	@Test
	public void testGetByName() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findByName("cellular process");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("GO:0009987"), true);
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		List<ExternalItem> items = eo.findMatching("mitochondrial");
		assertEquals("'%cellular%' in the GO database should return multiple items", items.size() > 0, true);
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
		for(ExternalItem item: items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}
}
