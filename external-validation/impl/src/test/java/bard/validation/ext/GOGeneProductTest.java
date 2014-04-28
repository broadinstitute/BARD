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
import org.junit.BeforeClass;
import org.junit.Test;

import bard.validation.ext.util.GOUtil;

public class GOGeneProductTest {

	private static ExternalOntologyGOGeneProduct eo;

	public GOGeneProductTest() {
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
