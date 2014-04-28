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

package bard.validation.extext

import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyCreator
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import grails.plugin.cache.Cacheable
import groovy.json.JsonSlurper
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.logging.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate
import org.apache.commons.logging.LogFactory

import java.util.concurrent.ConcurrentHashMap

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/18/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
class BardExternalOntologyFactory implements ExternalOntologyFactory {

    private final List<ExternalOntologyCreator> creators = [new PersonCreator()]

    private static Log log = LogFactory.getLog(this.getClass())

    RestTemplate restTemplate

    Map<String,BardExternalOntologyFactory> delegateMap = new ConcurrentHashMap<String,BardExternalOntologyFactory>()

    /**
     * URL pointing to the base of the proxy housing the external ontology lookup
     * e.g. http://localhost:8080/external-ontology-proxy/externalOntology
     */
    @Value('${bard.externalOntologyProxyUrlBase}')
    String externalOntologyProxyUrlBase

    @Override
    @Cacheable('getExternalOntologyAPI')
    ExternalOntologyAPI getExternalOntologyAPI(String externalUrl) throws ExternalOntologyException {
        return getExternalOntologyAPI(externalUrl, new Properties())
    }

    @Override
    @Cacheable('getExternalOntologyAPI')
    ExternalOntologyAPI getExternalOntologyAPI(String externalUrl, Properties properties) throws ExternalOntologyException {
        URI uri = null;
        try {
            uri = new URI(externalUrl);
        } catch (URISyntaxException ex) {
            throw new ExternalOntologyException(ex);
        }
        uri.normalize();
        return getExternalOntologyAPI(uri, properties);
    }

    @Override
    @Cacheable('getExternalOntologyAPI')
    ExternalOntologyAPI getExternalOntologyAPI(URI uri, Properties properties) throws ExternalOntologyException {
        for (ExternalOntologyCreator creator : getCreators()) {
            ExternalOntologyAPI api = creator.create(uri, properties);
            if (api != null) {
                return api;
            }

            try {
                final URL url = new URL("${externalOntologyProxyUrlBase}/externalOntologyHasIntegratedSearch?externalUrl=${uri}")
                final String text = restTemplate.getForObject(url.toURI(), String.class)
                final String hasSupportString = new JsonSlurper().parseText(text).hasSupport
                if (BooleanUtils.toBoolean(hasSupportString)) {
                    log.info(println("creating new BardDelegatingExternalOntology for ${uri.toString()}"))
                    return new BardDelegatingExternalOntology(this.externalOntologyProxyUrlBase, uri.toString(), this.restTemplate)
                }
            }
            catch (Exception e) {
                throw new ExternalOntologyException("trouble with externalOntologyProxy", e)
            }
        }
        return null;
    }

    @Override
    List<ExternalOntologyCreator> getCreators() {
        return creators
    }
}
