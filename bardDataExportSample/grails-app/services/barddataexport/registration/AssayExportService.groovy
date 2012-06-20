package barddataexport.registration

import groovy.xml.MarkupBuilder

class AssayExportService {
    AssayExportHelperService assayExportHelperService
    /**
     * Stream an assay document given an assay document Id
     * @param xml
     * @param assayDocumentId
     */
    public void generateAssayDocument(
            final MarkupBuilder markupBuilder, final BigDecimal assayDocumentId) {
        this.assayExportHelperService.generateAssayDocument(markupBuilder,assayDocumentId)
    }
    /**
     * Stub for generating assays with status of Ready
     * @param markupBuilder
     */
    public void generateAssays(
            final MarkupBuilder markupBuilder) {
        this.assayExportHelperService.generateAssays(markupBuilder)

    }

    /**
     * Generate an assay given an assayId
     *
     * @param xml
     * @param assayId
     */
    public void generateAssay(
            final MarkupBuilder markupBuilder,
            final BigDecimal assayId) {
        this.assayExportHelperService.generateAssay(markupBuilder,assayId)

    }
}
