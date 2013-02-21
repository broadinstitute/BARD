package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class GOTests {
	
	private ExternalOntologyAPI eo;
	
	public GOTests() {
		BasicConfigurator.configure();
		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
		logger.setLevel(Level.INFO);
	}
	
	@BeforeClass
	public void initialize() throws ExternalOntologyException {
		eo = new ExternalOntologyGO();
	}
	
	@Test
	public void testFindById() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findById("GO:0009987");
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("GO:0009987"), true);
		System.out.println("testGetById took (ms): " + (System.currentTimeMillis() - start) );
	}
	
	@Test
	public void testGetByName() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalItem item = eo.findByName("cellular process");
		System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item.getId().equals("GO:0009987"), true);
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start) );
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		List<ExternalItem> items = eo.findMatching("cellular");
		for(ExternalItem item: items)
			System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
		assertEquals("'%cellular%' in the GO database should return multiple items", items.size() > 0, true);
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
	}
}