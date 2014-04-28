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

package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.util.TargetClassification

class TargetRestService extends AbstractRestService {
    def transactional=false
    public String getResourceContext() {
        return RestApiConstants.TARGETS_RESOURCE
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        return null
    }

    @Override
    public String getResource() {
        String resourceName = RestApiConstants.TARGETS_RESOURCE
        return new StringBuilder(externalUrlDTO.ncgcUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }
    /**
     *  Get classifications from source for a given target
     *   targets/{acc}/classification/{source}*   Example http://bard.nih.gov/api/v15/targets/accession/P20393/classification/panther
     *
     * @param accessionNumber
     * @param source
     * @return list of target classifications
     *
     *
     */
    public List<TargetClassification> getClassificationsFromSourceWithTarget(final String source,final String targetAccessionNumber) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.ACCESSION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(targetAccessionNumber).
                    append(RestApiConstants.CLASSIFICATION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(source)

        final URL url = new URL(resource.toString())
        Map<String, List<TargetClassification>> targetClassificationsMap = getForObject(url.toURI(), HashMap.class)
        List<TargetClassification> targetClassifications = targetClassificationsMap.get(targetAccessionNumber) as List<TargetClassification>
        return targetClassifications
    }
    /**
     * Get target accessions for the specific classification id.
     * /targets/classification/{source}/{id}
     * Example http://bard.nih.gov/api/v15/targets/classification/panther/PC00169
     *
     * @param targetClassificationId
     * @return
     */
    public List<Target> getTargetsFromClassificationId(final String source,final String targetClassificationId) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.CLASSIFICATION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(source).
                     append(RestApiConstants.FORWARD_SLASH).
                    append(targetClassificationId)

        final URL url = new URL(resource.toString())
        List<Target> targets = getForObject(url.toURI(), Target[].class) as List<Target>
        return targets
    }
    /**
     * Get a target given an accession number
     *
     * Example http://bard.nih.gov/api/v15/targets/accession/P20393
     * @param accessionNumber
     * @return  Target
     */
    public Target getTargetByAccessionNumber(final String accessionNumber) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.ACCESSION).
                    append(RestApiConstants.FORWARD_SLASH).
                     append(accessionNumber)

        final URL url = new URL(resource.toString())
        Target target = getForObject(url.toURI(), Target.class)
        return target
    }
}
