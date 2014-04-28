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
import bard.core.rest.spring.biology.BiologyEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class BiologyRestService extends AbstractRestService {
    def transactional = false

    public String getResourceContext() {
        return RestApiConstants.BIOLOGY_RESOURCE;
    }

    public List<BiologyEntity> convertBiologyId(final List<Long> bids) {
        try {
            if (bids) {
                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("bids", bids.join(","));
                final String urlString = buildURLToBiologyData(true)
                final URL url = new URL(urlString)
                final List<BiologyEntity> biologyEntityList = this.postForObject(url.toURI(), BiologyEntity[].class, map) as List<BiologyEntity>;
                return biologyEntityList
            }
        } catch (Exception ee) {
            log.error(ee,ee)
        }
        return null

    }


    String buildURLToBiologyData(Boolean expand) {
        final StringBuilder resource = new StringBuilder(getResource())

        if (expand) {
            resource.append(RestApiConstants.QUESTION_MARK)
            resource.append(RestApiConstants.EXPAND_TRUE);
        }
        return resource.toString()
    }



    @Override
    public String getSearchResource() {
        // Not relevant for our purposes right now
        final String resourceName = getResourceContext()
        return new StringBuilder(externalUrlDTO.ncgcUrl).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.SEARCH).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.QUESTION_MARK).
                toString();
    }

    @Override
    public String getResource() {
        final String resourceName = getResourceContext()
        return (new StringBuilder(externalUrlDTO.ncgcUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH)).
                toString();
    }


}
