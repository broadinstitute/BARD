package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ATCCTests {

	private String id;
	private boolean expected;

	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] {
				{ "ATCC BAA-2162-XXX", false },
				{ "cells", false },
				{ "ATCC BAA-2162", true },
				{ "ATCC700668", true },
				{ "BAA-2146", true },
				{ "BAA-2128", true },
				{ "BAA-2127", true },
				{ "BAA-1706", true },
				{ "BAA-1705", true },
				{ "30-4500K", true },
				{ "51858", true },
				{ "49610", true },
				{ "49609", true },
				{ "49533", true },
				{ "49532", true },
				{ "23839", true },
				{ "25025", true },
				{ "14089", true },
				{ "48-X", true },
				{ "55-X", true },
				{ "CRL-1934", true },
				{ "SCRC-1010", true },
				{ "SCRC-1036", true },
				{ "SCRC-1037", true },
				{ "SCRC-1038", true },
				{ "SCRC-1039", true },
				{ "SCRC-2002", true } };
		return Arrays.asList(data);
	}

	public ATCCTests(String id, boolean expected) {
		this.id = id;
		this.expected = expected;
	}

	@Test
	public void testATCCId() throws ExternalOntologyException {
		ExternalOntologyAPI api = new ExternalOntologyATCC();
		System.out.println(id);
		ExternalItem item = api.findById(id);
		assertEquals(item != null, expected);
	}
}
