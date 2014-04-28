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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ATCCTest {

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

	public ATCCTest(String id, boolean expected) {
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
