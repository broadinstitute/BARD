package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder

import javax.servlet.http.HttpServletResponse

class AssayExportService {
    AssayExportHelperService assayExportHelperService

    /**
     * Stream an assay document
     * @param markupBuilder
     * @param assayDocument
     */
    public Long generateAssayDocument(
            final MarkupBuilder markupBuilder, final Long assayDocumentId) {
        final AssayDocument assayDocument = AssayDocument.get(assayDocumentId)
        if (!assayDocument) {
            log.error("Assay Document with Id ${assayDocumentId} does not exists")
            throw new NotFoundException("Assay Document with Id ${assayDocumentId} does not exists")
        }

        this.assayExportHelperService.generateAssayDocument(markupBuilder, assayDocument, true)
        return assayDocument.version
    }
    /**
     * Set the ReadyForExtraction value on the element to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * @param lkatestStatus - should be one of ["Ready", "Started", "Complete"]
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(final Long assayId, final Long clientVersion, final String latestStatus) {

        final Assay assay = Assay.findById(assayId)
        if (!assay) { //we could not find the element
            throw new NotFoundException("Assay Document with ID: ${assayId}, could not be found")
        }
        if (assay.version > clientVersion) { //There is a conflict, supplied version is less than the current version
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_CONFLICT, ETag: assay.version)
        }
        if (assay.version != clientVersion) {//supplied version is not equal to the version in database
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_PRECONDITION_FAILED, ETag: assay.version)
        }

        //no-op if the status is already completed
        final String currentStatus = assay.readyForExtraction
        if (currentStatus != latestStatus) {
            assay.readyForExtraction = latestStatus
            assay.save(flush: true)
        }
        return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_OK, ETag: assay.version)
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
    public Long generateAssay(
            final MarkupBuilder markupBuilder,
            final Long assayId) {

        final Assay assay = Assay.get(assayId)
        if (!assay) {
            log.error("Assay with Id ${assayId} does not exists")
            throw new NotFoundException("Assay with Id ${assayId} does not exists")
        }
        this.assayExportHelperService.generateAssay(markupBuilder, assay)
        return assay.version
    }
}
