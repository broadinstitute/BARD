package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ATCCMatchingTests {

	private ExternalOntologyAPI api = new ExternalOntologyATCC();
	
	public ATCCMatchingTests() throws Exception {
		BasicConfigurator.configure();
		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
		logger.setLevel(Level.INFO);
		api.findMatching("cells"); // initialize
	}

	@Test
	public void testATCCMatching() throws ExternalOntologyException {
		api = new ExternalOntologyATCC();
		List<ExternalItem> items = api.findMatching("cells");
		System.out.println(String.format("%s items returned", items.size()));
		for(ExternalItem item: items)
			System.out.println(item);
		assertEquals(items.size() > 0, true);
	}
}
