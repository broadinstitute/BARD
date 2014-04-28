/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.validation.ext;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NCBITest {

	public NCBITest() {
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
		System.out.println("testGetByName took (ms): " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testGetByNameFail() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("protein", "southern@scripps.edu", "BARD-CAP");
		try {
			ExternalItem item = eo.findByName("ppar");
			String.format("%s\t%s", item.getId(), item.getDisplay());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("Expected failure of generic term", 1 == 1, true);
		System.out.println("testGetByNameFail took (ms): " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testFindMatching() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("gene", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("ppar gamma");
        TestUtils.printItemsInfo(items);
        assertEquals("'ppar gamma' in the gene database should return items", items.size() > 0, true);
		System.out.println(String.format("%s items returned for query 'ppar gamma'", items.size()));
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
	}

    @Test
    public void testFindMatchingSupportsGoodId() throws ExternalOntologyException {
        long start = System.currentTimeMillis();
        ExternalOntologyAPI eo = new ExternalOntologyNCBI("protein", "southern@scripps.edu", "BARD-CAP");
        List<ExternalItem> items = eo.findMatching("7381449");
        TestUtils.printItemsInfo(items);
        assertEquals("'7381449' should return an item", "7381449", items.get(0).getId());
        System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testFindMatchingSupportsBadId() throws ExternalOntologyException {
        long start = System.currentTimeMillis();
        ExternalOntologyAPI eo = new ExternalOntologyNCBI("protein", "southern@scripps.edu", "BARD-CAP");
        final int limit = 10;
        List<ExternalItem> items = eo.findMatching("123", limit);
        TestUtils.printItemsInfo(items);
        assertTrue("shouldn't have found an id, expect the limit of matches", items.size() == limit);
        System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
    }

    @Test
	public void testFindMatchingMlpcn() throws ExternalOntologyException {
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("pcassay", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("\"NIH Molecular Libraries Program\"[SourceCategory]", 100);
		// for (ExternalItem item : items)
		// System.out.println(String.format("%s\t%s", item.getId(),
		// item.getDisplay()));
		assertEquals("'chembl' returns: " + items.size(), items.size() > 50, true);
	}

	@Test
	public void testFindMatchingLimitThree() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("gene", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("ppar", 3);
        TestUtils.printItemsInfo(items);
        assertEquals("'ppar gamma' in the gene database should return items", items.size() == 3, true);
		System.out.println(String.format("%s items returned for query 'ppar gamma'", items.size()));
		System.out.println("testFindMatchingLimitThree took (ms): " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testTaxonHomo() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("taxonomy", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("homo");
        TestUtils.printItemsInfo(items);
        assertEquals("> 1 items returned", items.size() > 0, true);
		System.out.println(String.format("%s items returned for query 'ppar gamma'", items.size()));
		System.out.println("testFindMatchingLimitThree took (ms): " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testNotFound() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		ExternalOntologyAPI eo = new ExternalOntologyNCBI("taxonomy", "southern@scripps.edu", "BARD-CAP");
		List<ExternalItem> items = eo.findMatching("sasslssci");
		assertEquals("expected 0 items returned", items.size() == 0, true);
		System.out.println("testNotFound took (ms): " + (System.currentTimeMillis() - start));
	}

}
