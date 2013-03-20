package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class CASTests {

	public CASTests() {
		BasicConfigurator.configure();
	}

	@Test
	public void testGetById() throws ExternalOntologyException {
		ExternalOntologyAPI eo = new ExternalOntologyCAS();
		ExternalItem item = eo.findById("107-07-3");
		assertEquals(String.format("%s\t%s", item.getId(), item.getDisplay()), item != null, true);
	}
	
	@Test
	public void testGetByIdInvalid() throws ExternalOntologyException {
		ExternalOntologyAPI eo = new ExternalOntologyCAS();
		ExternalItem item = eo.findById("1-1-1");
		assertEquals("No item returned", item == null, true);
	}
	
}