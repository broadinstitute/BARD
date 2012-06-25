package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder

class AssayExportService {
    AssayExportHelperService assayExportHelperService

    /**
     * Stream an assay document
     * @param markupBuilder
     * @param assayDocument
     */
    public void generateAssayDocument(
            final MarkupBuilder markupBuilder, final Long assayDocumentId) {
        final AssayDocument assayDocument = AssayDocument.get(assayDocumentId)
        if (!assayDocument) {
            log.error("Assay Document with Id ${assayDocumentId} does not exists")
            throw new NotFoundException("Assay Document with Id ${assayDocumentId} does not exists")
        }

        this.assayExportHelperService.generateAssayDocument(markupBuilder, assayDocument, true)
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
     * @param markupBuilder
     * @param assay
     */
    public void generateAssay(
            final MarkupBuilder markupBuilder,
            final Long assayId) {

        final Assay assay = Assay.get(assayId)
        if (!assay) {
            log.error("Assay with Id ${assayId} does not exists")
            throw new NotFoundException("Assay with Id ${assayId} does not exists")
        }
        this.assayExportHelperService.generateAssay(markupBuilder, assay)
    }
}
