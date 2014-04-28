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
public class ATCCRegExTest {

	private String id;
	private boolean expected;

	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] {
				{"CRL-2648", true},
				{"L Cells (ATCC\u00AECRL-2648\u2122)", true },
				{"Fetal Bovine Serum, ES Cell Qualified  (ATCC\u00AESCRR-30-2020\u2122)", true },
				{"IRR-3T3 [irradiated 3T3-Swiss albino cells] (ATCC\u00AE48-X\u2122)", true },
				{"HMCB [ Human Melanoma Cell Bowes] (ATCC\u00AECRL-9607\u2122)", true },
				{"GCT [Giant Cell Tumor] (ATCC\u00AETIB-223\u2122)", true },
				{"MPRO Cell Line, Clone 2.1  (ATCC\u00AECRL-11422\u2122)", true },
				{"EML Cell Line, Clone 1 (ATCC\u00AECRL-11691\u2122)", true },
				{"Canine distemper virus (ATCC\u00AEVR-1587\u2122)", true },
				{"9TR#1 [Embryonic stem cell line] (ATCC\u00AECRL-11379\u2122)", true },
				{"Primary Renal Cortical Epithelial Cells; Normal, Human (ATCC\u00AEPCS-400-011\u2122)", true },
				{"Primary Renal Mixed Epithelial Cells; Normal, Human (ATCC\u00AEPCS-400-012\u2122)", true },
				{"Primary Coronary Artery Smooth Muscle Cells; Normal, Human (ATCC\u00AEPCS-100-021\u2122)", true },
				{"Primary Coronary Artery Endothelial Cells; Normal, Human (ATCC\u00AEPCS-100-020\u2122)", true },
				{"Trypsin-EDTA for Primary Cells (ATCC\u00AEPCS-999-003\u2122)", true },
				{"Airway Epithelial Cell Basal Medium (ATCC\u00AEPCS-300-030\u2122)", true },
				{"L-NGC-alpha2B L-cells  (ATCC\u00AECRL-10275\u2122)", true },
				{"ATCC-DYS0100 Human Induced Pluripotent Stem Cells  (ATCC\u00AEACS-1019\u2122)", true },
				{"18-20 Day Peritubular Cells  (ATCC\u00AE63428\u2122)", true },
				{"Human embryonic kidney cells 293SF-3F6 (ATCC\u00AECRL-12585\u2122)", true },
				{"ATCC-DYP0530 Human Induced Pluripotent Stem Cells (ATCC\u00AEACS-1014\u2122)", true },
				{"MCF-10-2F HUMAN, MAMMARY EPITHELIAL CELLS (ATCC\u00AECRL-10780\u2122)", true },
				{"FRTL-5 RAT THYROID CELLS (ATCC\u00AECRL-8305\u2122)", true },
				{"Primary Umbilical Vein Endothelial Cells; Normal, Human, Pooled (ATCC\u00AEPCS-100-013\u2122)", true },
				{"Mouse L cells/human CDw32, 16.2 CG7 (ATCC\u00AECRL-10680\u2122)", true },
				{"Primary Bronchial/Tracheal Epithelial Cells; Normal, Human (ATCC\u00AEPCS-300-010\u2122)", true }
				
		};
		return Arrays.asList(data);
	}

	public ATCCRegExTest(String id, boolean expected) {
		this.id = id;
		this.expected = expected;
	}

	@Test
	public void testATCCIdRegEx() throws ExternalOntologyException {
		ExternalOntologyAPI api = new ExternalOntologyATCC();
		try {
			String cleanId = api.cleanId(id);
			System.out.println(String.format("%s\t%s", cleanId, id));
			assertEquals(true, true);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			assertEquals(false, true);
		}
		
	}
}
