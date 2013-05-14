package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import bard.validation.ext.util.GOUtil;

public class GOGeneProductTests {

	private static ExternalOntologyGOGeneProduct eo;

	public GOGeneProductTests() {
		BasicConfigurator.configure();
//		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
//		logger.setLevel(Level.INFO);
	}
	
	@BeforeClass
	public static void initialize() throws Exception {
		eo = new ExternalOntologyGOGeneProduct(GOUtil.getEBIDataSource());
		eo.findMatching("PPARG", 20); // force initialization of pool
	}
	
	@Test
	public void testFindById() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findById("UniProtKB:Q9I878");
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("UniProtKB:Q9I878"), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start) );
	}
	
	@Test
	public void testFindByIdDbInKey() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		String id = "MGI:MGI:1343132";
		ExternalItem item = eo.findById(id);
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals(id), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start) );
	}
	
	@Test
	public void testFindByName() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findByName("UniProtKB:H2RTM2 (Uncharacterized protein, PPARGC1B (2 of 3), Takifugu rubripes)");
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("UniProtKB:H2RTM2"), true);
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start) );
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		List<ExternalItem> items = eo.findMatching("PPARG");
		for(ExternalItem item: items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals("'PPARG' in the GO Gene Product database should return multiple items", items.size() > 0, true);
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("returned %s items", items.size()));
	}
}
