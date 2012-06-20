package barddataexport.registration

import groovy.xml.MarkupBuilder

class AssayExportService {
    /**
     * Stream an assay document given an assay document Id
     * @param xml
     * @param assayDocumentId
     */
    public void generateAssayDocument(
            final MarkupBuilder xml, final BigDecimal assayDocumentId) {

    }
    /**
     * Stub for generating assays with status of Ready
     * @param xml
     */
    public void generateNewAssays(
            final MarkupBuilder xml) {

    }
    /**
     * Generate all assays, regardless of status
     * @param xml
     */
    public void generateAssays(
            final MarkupBuilder xml) {

    }
    /**
     * Generate an assay given an assayId
     *
     * @param xml
     * @param assayId
     */
    public void generateAssay(
            final MarkupBuilder xml,
            final BigDecimal assayId) {

    }
}
