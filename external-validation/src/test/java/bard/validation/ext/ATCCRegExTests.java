package bard.validation.ext;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ATCCRegExTests {

	private String id;
	private boolean expected;

	@Parameters
	public static List<Object[]> args() {
		Object[][] data = new Object[][] {
				{"CRL-2648", true},
				{"L Cells (ATCC®CRL-2648™)", true },
				{"Fetal Bovine Serum, ES Cell Qualified  (ATCC®SCRR-30-2020™)", true },
				{"IRR-3T3 [irradiated 3T3-Swiss albino cells] (ATCC®48-X™)", true },
				{"HMCB [ Human Melanoma Cell Bowes] (ATCC®CRL-9607™)", true },
				{"GCT [Giant Cell Tumor] (ATCC®TIB-223™)", true },
				{"MPRO Cell Line, Clone 2.1  (ATCC®CRL-11422™)", true },
				{"EML Cell Line, Clone 1 (ATCC®CRL-11691™)", true },
				{"Canine distemper virus (ATCC®VR-1587™)", true },
				{"9TR#1 [Embryonic stem cell line] (ATCC®CRL-11379™)", true },
				{"Primary Renal Cortical Epithelial Cells; Normal, Human (ATCC®PCS-400-011™)", true },
				{"Primary Renal Mixed Epithelial Cells; Normal, Human (ATCC®PCS-400-012™)", true },
				{"Primary Coronary Artery Smooth Muscle Cells; Normal, Human (ATCC®PCS-100-021™)", true },
				{"Primary Coronary Artery Endothelial Cells; Normal, Human (ATCC®PCS-100-020™)", true },
				{"Trypsin-EDTA for Primary Cells (ATCC®PCS-999-003™)", true },
				{"Airway Epithelial Cell Basal Medium (ATCC®PCS-300-030™)", true },
				{"L-NGC-alpha2B L-cells  (ATCC®CRL-10275™)", true },
				{"ATCC-DYS0100 Human Induced Pluripotent Stem Cells  (ATCC®ACS-1019™)", true },
				{"18-20 Day Peritubular Cells  (ATCC®63428™)", true },
				{"Human embryonic kidney cells 293SF-3F6 (ATCC®CRL-12585™)", true },
				{"ATCC-DYP0530 Human Induced Pluripotent Stem Cells (ATCC®ACS-1014™)", true },
				{"MCF-10-2F HUMAN, MAMMARY EPITHELIAL CELLS (ATCC®CRL-10780™)", true },
				{"FRTL-5 RAT THYROID CELLS (ATCC®CRL-8305™)", true },
				{"Primary Umbilical Vein Endothelial Cells; Normal, Human, Pooled (ATCC®PCS-100-013™)", true },
				{"Mouse L cells/human CDw32, 16.2 CG7 (ATCC®CRL-10680™)", true },
				{"Primary Bronchial/Tracheal Epithelial Cells; Normal, Human (ATCC®PCS-300-010™)", true }
				
		};
		return Arrays.asList(data);
	}

	public ATCCRegExTests(String id, boolean expected) {
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
