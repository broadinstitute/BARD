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

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import groovy.json.JsonSlurper
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.lang.StringUtils
import org.springframework.web.client.RestTemplate

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/19/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
class BardDelegatingExternalOntology implements ExternalOntologyAPI {

    static final String FIND_BY_ID_ACTION = 'findExternalItemById'
    static final String FIND_BY_TERM_ACTION = 'findExternalItemsByTerm'

    final String externalOntolgoyProxyUrlBase

    final String externalUrl;

    final RestTemplate restTemplate

    /**
     *
     * @param externalOntolgoyProxyUrlBase URL pointing to the base of the proxy housing the external ontology lookup
     *                                      e.g. http://localhost:8080/external-ontology-proxy/externalOntology
     * @param externalUrl the externalUrl associated with the element, act as a key to determine which ontology to look up
     * @param restTemplate
     */
    public BardDelegatingExternalOntology(final String externalOntolgoyProxyUrlBase,
                                          final String externalUrl,
                                          final RestTemplate restTemplate) {
        this.externalOntolgoyProxyUrlBase = externalOntolgoyProxyUrlBase
        this.externalUrl = externalUrl
        this.restTemplate = restTemplate
    }

    @Override
    ExternalItem findById(String id) throws ExternalOntologyException {
        try {
            final String url = buildUrl(externalOntolgoyProxyUrlBase, FIND_BY_ID_ACTION, [externalUrl: externalUrl, id: id])
            final String jsonText = restTemplate.getForObject(url, String.class)
            def jsonObject = new JsonSlurper().parseText(jsonText)
            if (jsonObject && jsonObject.id) {
                return new ExternalItemImpl(id: jsonObject.id, display: jsonObject.display)
            }
        }
        catch (Exception e) {
            throw new ExternalOntologyException("trouble delegating findById request for ${this.externalUrl}", e)
        }
        return null
    }

    private String buildUrl(final String baseUrl, final String action, final Map<String, String> params) {
        String queryParams = params.collect { k, v -> "${k}=${v.encodeAsURL()}" }.join('&')
        "${baseUrl}/${action}?${queryParams}"
    }

    @Override
    ExternalItem findByName(String name) throws ExternalOntologyException {
        throw new NotImplementedException()
    }

    @Override
    List<ExternalItem> findMatching(String term) throws ExternalOntologyException {
        findMatching(term, 50)
    }

    @Override
    List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
        final List<ExternalItem> externalItems = []
        try{
            final String url = buildUrl(externalOntolgoyProxyUrlBase, FIND_BY_TERM_ACTION, [externalUrl: externalUrl, term: term, limit: limit])
            final String jsonText = restTemplate.getForObject(url, String.class)
            def jsonObject = new JsonSlurper().parseText(jsonText)
            jsonObject.externalItems.each { externalItemJson ->
                externalItems.add(new ExternalItemImpl(id: externalItemJson.id, display: externalItemJson.display))
            }
        }
        catch (Exception e){
            throw new ExternalOntologyException("trouble delegating findMatching request for ${this.externalUrl}", e)
        }
        externalItems
    }

    @Override
    String getExternalURL(String id) {
        throw new NotImplementedException()
    }

    /**
     * formats a query term. E.g. adds % for SQL LIKE, trims the term etc.
     */
    @Override
    public String queryGenerator(String term) {
        return StringUtils.trimToEmpty(term);
    }

    @Override
    public String cleanId(String id) {
        return StringUtils.trimToEmpty(id);
    }

    @Override
    public String cleanName(String name) {
        return StringUtils.trimToEmpty(name);
    }

    @Override
    boolean matchesId(String potentialId) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validate(String name, String id) throws ExternalOntologyException {
        ExternalItem item = findByName(cleanName(name));
        ExternalItem item2 = findById(cleanId(id));
        return item.equals(item2);
    }
}
