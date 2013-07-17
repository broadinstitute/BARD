package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class DiseaseTests {
	
	private static ExternalOntologyAPI eo;
	
	public DiseaseTests() {
		BasicConfigurator.configure();
//		for(String name: new String[]{"org.apache.axis"}) {
//			Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger(name);
//			logger.setLevel(Level.INFO);
//		}
	}
	
	@BeforeClass
	public static void initialize() throws ExternalOntologyException {
		eo = new ExternalOntologyDisease();
	}
	
	
	@Test
	public void testFailId() throws ExternalOntologyException {
		ExternalItem item = eo.findById("DOID_9");
		assertEquals(item == null, true);
	}
	
	@Test
	public void testFindById() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findById("DOID_0050484");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("DOID_0050484"), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}
	
	@Test
	public void testGetByName() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findByName("Aneruptive Fever");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getDisplay().equalsIgnoreCase("aneruptive fever"), true);
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		List<ExternalItem> items = eo.findMatching("Fever");
		assertEquals("'fever' from the Disease Ontology should return multiple items", items.size() > 0, true);
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
		for(ExternalItem item: items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
	}
}