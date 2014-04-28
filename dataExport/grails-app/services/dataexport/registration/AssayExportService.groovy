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

package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder

import dataexport.util.UtilityService
import bard.db.enums.ReadyForExtraction

class AssayExportService {
    AssayExportHelperService assayExportHelperService
    UtilityService utilityService
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

        this.assayExportHelperService.generateDocument(markupBuilder, assayDocument)
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
    public BardHttpResponse update(final Long assayId, final Long clientVersion, final ReadyForExtraction latestStatus) {
       return utilityService.update(Assay.get(assayId),assayId,clientVersion,latestStatus,"Assay")
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
