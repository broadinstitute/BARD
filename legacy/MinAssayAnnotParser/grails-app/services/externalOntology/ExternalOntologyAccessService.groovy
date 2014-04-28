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

package externalOntology

import bard.validation.ext.ExternalOntologyException

import static bard.validation.ext.ExternalOntologyFactory.NCBI_TOOL
import static bard.validation.ext.ExternalOntologyFactory.NCBI_EMAIL
import groovy.transform.TypeChecked
import bard.validation.ext.ExternalItem
import org.springframework.util.Assert
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyFactory

/**
 * This is part of the implementation copied from BARD project, we want to do the same thing as it has been done there.
 */
class ExternalOntologyAccessService {
    ExternalOntologyFactory externalOntologyFactory
    private static final Properties externalOntologyProperites = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'default@bard.nih.gov'])
    private static final int DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE = 20

    /**
     * Not all external ontologies have a supported search functionality. This allows checking given an externalUrl.
     *
     * @param externalUrl
     * @return true if an ExternalOntologyAPI implementation is found for the given externalUrl
     */
    @TypeChecked
    boolean externalOntologyHasIntegratedSearch(String externalUrl) {
        boolean hasSupport = false
        try {
            if (externalOntologyFactory.getExternalOntologyAPI(externalUrl, externalOntologyProperites)) {
                hasSupport = true
            }
        }
        catch (ExternalOntologyException e) {
            log.error("Exception when calling getExternalOntologyAPI with externalUrl: $externalUrl", e)
        }
        hasSupport
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for ExternalItems containing that term
     *
     * This uses a default page size of 20 to limit the number of matches returned
     *
     * @param externalUrl cannot be blank
     * @param term cannot be blank
     * @return a List<ExternalItem> empty if no matches, items are sorted case-insensitive by display
     */
    @TypeChecked
    List<ExternalItem> findExternalItemsByTerm(String externalUrl, String term) {
        findExternalItemsByTerm(externalUrl, term, DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE)
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for ExternalItems containing that term
     *
     * @param externalUrl cannot be blank
     * @param term cannot be blank
     * @param limit
     * @return a List<ExternalItem> empty if no matches, items are sorted case-insensitive by display
     * @throws ExternalOntologyException
     */
    @TypeChecked
    List<ExternalItem> findExternalItemsByTerm(String externalUrl, String term, int limit) throws ExternalOntologyException {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(term, "term cannot be blank")
        final List<ExternalItem> externalItems = []
        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, externalOntologyProperites)
            if (externalOntology) {
                externalItems.addAll(externalOntology.findMatching(term, limit))
            }
        } catch (ExternalOntologyException e) {
            log.error("Exception when calling externalOntology.findMatching() with externalUrl: $externalUrl term: $term", e)
            throw e
        }
        externalItems.sort(true) { ExternalItem a, ExternalItem b -> a.display?.toLowerCase() <=> b.display?.toLowerCase() }
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for an ExternalItem by it's id
     * @param externalUrl cannot be blank
     * @param id cannot be blank
     * @return an ExternalItem or null if no match is found
     * @throws ExternalOntologyException
     */
    @TypeChecked
    ExternalItem findExternalItemById(String externalUrl, String id) throws ExternalOntologyException {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(id, "id cannot be blank")
        ExternalItem externalItem

        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, externalOntologyProperites)
            if (externalOntology) {
                externalItem = externalOntology.findById(id)
            }
        } catch (ExternalOntologyException e) {
            log.error("Exception when calling externalOntology.findById() with externalUrl: $externalUrl term: $id", e)
            throw e
        }
        externalItem
    }
}
