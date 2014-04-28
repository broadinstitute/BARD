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

import bard.core.exceptions.RestApiException
import bard.core.interfaces.RestApiConstants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import bard.core.rest.spring.etags.ETags

class ETagRestService extends AbstractRestService {

    def transactional=false
    public String getResourceContext() {
        return RestApiConstants.ETAGS_RESOURCE;
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
        String resourceName = getResourceContext()
        return new StringBuilder(externalUrlDTO.ncgcUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }
    /**
     *  Get the URL to get a Compound. This is  url template so replace {id} with the
     *  real ID
     * @return the url
     */
    @Override
    public String buildEntityURL() {
        return new StringBuilder(getResource()).
                append("{etag}").
                toString();
    }
    public ETags getComponentETags(final String compositeETag){
        final String url = buildEntityURL()
        final Map map = [etag: compositeETag]
        ETags etags = (ETags) getForObject(url, ETags.class, map)
        return etags;
    }
    public String newCompositeETag(final String name, final List<String> compositeETags) {
        final String url = this.buildURLToCreateETag()
        try {
            final Map<String, Long> etags = [:]
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            if (compositeETags) {
                map.add("etagids", compositeETags.join(","));
            }
            map.add("name", name)

            final HttpEntity entity = new HttpEntity(map, new HttpHeaders());


            final HttpEntity exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            HttpHeaders headers = exchange.getHeaders()

            this.extractETagsFromResponseHeader(headers, 0, etags)

            // there should only be one ETag returned
            return firstETagFromMap(etags)
        } catch (HttpClientErrorException httpClientErrorException) { //throws a 4xx exception
            log.error(url.toString(), httpClientErrorException)
            throw httpClientErrorException
        } catch (RestClientException restClientException) {
            log.error(url.toString(), restClientException)
            throw new RestApiException(restClientException)


        }
    }
}
