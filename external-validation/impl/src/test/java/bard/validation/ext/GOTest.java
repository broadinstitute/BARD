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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import bard.validation.ext.util.GOUtil;

public class GOTest {

	private static ExternalOntologyGO eo;

	public GOTest() {
		BasicConfigurator.configure();
		Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.http");
		logger.setLevel(Level.INFO);
	}
	
	@BeforeClass
	public static void initialize() throws Exception {
		eo = new ExternalOntologyGO(ExternalOntologyGO.TYPE_BIOLOGICAL_PROCESS, GOUtil.getEBIDataSource());
		ExternalItem item = eo.findById("GO:0009987"); // force initialization of pool
	}
	
	@Test
	public void testIdPrependGO() {
		String id = "GO9987";
		String clean = eo.cleanId(id);
		assertEquals(true, "GO:0009987".equals(clean));
	}
	
	@Test
	public void testFailingId() throws ExternalOntologyException {
		String id = "ABC";
		ExternalItem item = eo.findById(id);
		assertEquals(null, item);
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
		List<ExternalItem> items = eo.findMatching("cellular response");
		TestUtils.printItemsInfo(items);
		assertEquals("'%cellular%' in the GO database should return multiple items", items.size() > 0, true);
		System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start) );
		System.out.println(String.format("returned %s items", items.size()));
	}

    @Test
     public void testFindMatchingGoodId() throws ExternalOntologyException {
        long start = System.currentTimeMillis();
        List<ExternalItem> items = eo.findMatching("GO:0009987");
        TestUtils.printItemsInfo(items);
        assertEquals("should be 1 item", 1, items.size());
        assertEquals("id of found item should match", "GO:0009987", items.get(0).getId());
        System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
        System.out.println(String.format("returned %s items", items.size()));
    }

    @Test
    public void testFindMatchingBadId() throws ExternalOntologyException {
        long start = System.currentTimeMillis();
        List<ExternalItem> items = eo.findMatching("GO:0000000");
        TestUtils.printItemsInfo(items);
        assertEquals("should be 1 item", 0, items.size());
        System.out.println("testFindMatching took (ms): " + (System.currentTimeMillis() - start));
        System.out.println(String.format("returned %s items", items.size()));
    }
	
	@Test
    @Ignore
	public void testLongRunning() throws ExternalOntologyException {
		long start = System.currentTimeMillis();
		testFindById();
		try {
			Thread.currentThread();
			Thread.sleep(3600*1000);
		}
		catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		testFindById();
	}
}
