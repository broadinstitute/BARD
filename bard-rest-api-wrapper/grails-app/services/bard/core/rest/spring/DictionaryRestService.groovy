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

import bard.core.rest.spring.util.CapDictionary

import org.springframework.cache.annotation.Cacheable

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.Node

public class DictionaryRestService extends AbstractRestService {

    def transactional=false

    @Cacheable(value = 'dictionaryElements')
    public CapDictionary getDictionary() {
        final URL url = new URL(resource.toString())
        final CapDictionary capDictionary = (CapDictionary) getForObject(url.toURI(), CapDictionary.class)
        capDictionary.loadElements()
        return capDictionary
    }
    /**
     * @param dictionaryId - Given a dictionary Id , return the element
     * @return the element
     */
    @Cacheable(value = 'dictionaryElements')
    Node findDictionaryElementById(final Long dictionaryId) {
        return getDictionary().findDictionaryElement(dictionaryId)

    }

    @Override
    public String getResourceContext() {
        return RestApiConstants.DICTIONARY_RESOURCE;
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
                toString();
    }


}
