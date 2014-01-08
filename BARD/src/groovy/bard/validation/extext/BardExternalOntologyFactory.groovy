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
