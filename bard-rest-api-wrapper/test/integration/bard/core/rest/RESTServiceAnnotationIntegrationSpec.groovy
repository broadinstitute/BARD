package bard.core.rest;


import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.*

import static org.junit.Assert.*
import bard.core.interfaces.SearchResult
import bard.core.rest.AbstractRESTServiceSpec

/**
 * @author Rajarshi Guha
 */
public class RESTServiceAnnotationIntegrationSpec extends AbstractRESTServiceSpec {


    void "test getSingleCompoundAnnotations"() {
        //given:
        when:
        Compound c = restCompoundService.get(2722);
        then:
        assertNotNull("Compounds with ID [2722]", c);
        CompoundAdapter ca = new CompoundAdapter(c);
        assertNotNull("Compounds with ID [2722]", ca);

        Collection<Value> annots = ca.getAnnotations();
        assertNotNull(annots);
        assert !annots.isEmpty()
        assertFalse("Annotations for the Compound with CID " + ca.getPubChemCID() + " should not be empty", annots.isEmpty());
        for (Value annot : annots) {
            String key = annot.getId();
            String value = (String) annot.getValue();
            assertNotNull("Annotation Key for CID " + ca.getPubChemCID() + " must not be null", key);
            assertNotNull("Annotation Value for CID " + ca.getPubChemCID() + " must not be null", value);
        }
    }

    void "test getMultipleCompoundAnnotations"() {
        given:
        List<Long> ids = [2722L, 5394L]
        when:
        Collection<Compound> cmpds = restCompoundService.get(ids)
        then:
        assertNotNull("Compounds with ID [2722L, 5394L]", cmpds);
        assertEquals(2, cmpds.size());
        for (Compound c : cmpds) {
            CompoundAdapter ca = new CompoundAdapter(c);
            assertNotNull(ca);
            Collection<Value> annots = ca.getAnnotations();
            assertNotNull("Annotations CID " + ca.getPubChemCID() + " must not be null", annots);
            assertFalse("Annotations for the Compound with CID " + ca.getPubChemCID() + " should not be empty", annots.isEmpty());

            for (Value annot : annots) {
                String key = annot.getId();
                String value = (String) annot.getValue();
                assertNotNull("Annotation Key for CID " + ca.getPubChemCID() + " must not be null", key);
                assertNotNull("Annotation Value for CID " + ca.getPubChemCID() + " must not be null", value);
            }
        }
    }

    void "test getCompoundAnnotationsFromTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair");
        when:
        SearchResult<Compound> csi = restCompoundService.search(searchParams);
        then:
        assertTrue("CompoundService result must not be null", csi != null);
        final List<Compound> compounds = csi.searchResults
        assert compounds, "CompoundService result must not be empty"
        assert  csi.count,"CompoundService result must have at least one element"

        for (Compound c : compounds) {
            CompoundAdapter ca = new CompoundAdapter(c);
            Collection<Value> annots = ca.getAnnotations();
            assertNotNull("Annotations for the Compound with CID " + ca.getPubChemCID() + " should not be null", annots);
            assertFalse("Annotations for the Compound with CID " + ca.getPubChemCID() + " should not be empty", annots.isEmpty());
        }
    }

    void "test getCompoundAnnotationsFromStructureSearch()"() {
        given:
        final String smiles = "O=S(*C)(Cc1ccc2ncc(CCNC)c2c1)=O";
        StructureSearchParams structureSearchParams = new StructureSearchParams(smiles, StructureSearchParams.Type.Superstructure);
        structureSearchParams.setSkip((long) 0).setTop((long) 10);
        when:
        SearchResult<Compound> csi = restCompoundService.search(structureSearchParams);
        then:

        assertTrue("CompoundService result must not be null", csi != null);
        final List<Compound> compounds = csi.searchResults
        assert compounds, "CompoundService result must not be empty"
        assert csi.getCount(), "CompoundService result must have at least one element"

        for (Compound c : compounds) {
            CompoundAdapter ca = new CompoundAdapter(c);
            Collection<Value> annots = ca.getAnnotations();
            assertNotNull("Annotations from structure with smiles '" + smiles + "' must not be null", annots);
            assertFalse("Annotations for the Compound with CID " + ca.getPubChemCID() + " should not be empty", annots.isEmpty());
        }
    }

    void "getAssayAnnotationFromIds"() {
        given:
        List<Long> ids = [777L, 2868L]
        when:
        Collection<Assay> assays = restAssayService.get(ids);
        then:
        assertNotNull("Assays with IDs 777 and 2868 must exist", assays);
        assert !assays.isEmpty()
        for (Assay a : assays) {
            AssayAdapter ad = new AssayAdapter(a);
            Collection<Value> annos = ad.getAnnotations();
            assertNotNull("Assay must have annotations", annos);
            assertFalse("Annotations for the Assay with ID " + a.getId() + " should not be empty", annos.isEmpty());


            boolean hasGobp = false, hasGomf = false, hasGocc = false, hasCap = false;
            for (Value anno : annos) {
                String n = anno.getSource().getName();
                if (n.contains("CAPAnnotationDataSource")) hasCap = true;
                if (n.contains("GOBPAnnotationDataSource")) hasGobp = true;
                if (n.contains("GOMFAnnotationDataSource")) hasGomf = true;
                if (n.contains("GOCCAnnotationDataSource")) hasGocc = true;
            }
            assertTrue("Assays [777, 2868] Must have CAP annotation", hasCap);
            assertTrue("Assays [777, 2868] Must have a GO Biological Process Annotation", hasGobp);
            assertTrue("Assays [777, 2868] Must have a GO Molecular Function annotation", hasGomf);
            assertTrue("Assays [777, 2868] Must have a GO Cellular Component Annotation", hasGocc);
        }
    }

    void "getAssayAnnotationFromId"() {
        when:
        Assay a = restAssayService.get(2868);
        then:
        assertNotNull("Assay with ID 2868 must exist", a);
        AssayAdapter ad = new AssayAdapter(a);
        Collection<Value> annos = ad.getAnnotations();
        assertNotNull("Assay with ID 2868 must have annotations", annos);
        assertFalse("Annotations for the Assay with ID 2868 should not be empty", annos.isEmpty());

        boolean hasGobp = false, hasGomf = false, hasGocc = false, hasCap = false;
        for (Value anno : annos) {
            String n = anno.getSource().getName();
            if (n.contains("CAPAnnotationDataSource")) hasCap = true;
            if (n.contains("GOBPAnnotationDataSource")) hasGobp = true;
            if (n.contains("GOMFAnnotationDataSource")) hasGomf = true;
            if (n.contains("GOCCAnnotationDataSource")) hasGocc = true;
        }
        assertTrue("Assays [2868]  Must have CAP annotation", hasCap);
        assertTrue("Assays [2868]  Must have a GO Biological Process Annotation", hasGobp);
        assertTrue("Assays [2868]  Must have a GO Molecular Function annotation", hasGomf);
        assertTrue("Assays [2868]  Must have a GO Cellular Component", hasGocc);
    }

    void "getAssayAnnotationFromSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair");
        when:
        SearchResult<Assay> csi = restAssayService.search(searchParams);

        then:
        assert csi, "AssayService result for 'dna repair' query must not be null"
        final List<Assay> assays = csi.searchResults
        assert assays, "AssayService result for 'dna repair' query must not be empty"
        assert csi.getCount(), "AssayService result for 'dna repair' query must have at least one element"

        for (Assay a : assays) {
            AssayAdapter ca = new AssayAdapter(a);
            Collection<Value> annots = ca.getAnnotations();
            assertNotNull("Assay from 'dna repair query' must have annotations", annots);
            // assay 3248 (cap assay 2438) doesn't have any annotations!
            assertFalse("Annotations for the Assay from 'dna repair query' should not be empty", annots.isEmpty() && !a.getId().equals(3248l));

        }
    }

}
