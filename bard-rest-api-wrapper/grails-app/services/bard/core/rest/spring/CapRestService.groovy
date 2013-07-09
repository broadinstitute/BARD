package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.AbstractRestService
import bard.core.util.SSLTrustManager
import org.apache.commons.lang.StringUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

public class CapRestService extends AbstractRestService {

    def transactional = false
    def grailsApplication


    @Cacheable(value = 'elementHierarchyPaths')
    public Map getDictionaryElementPaths() {
        String basicAuthHash = grailsApplication.config.bard.cap.basicAuthenticationHash
        final URL url = new URL(resource.toString())
        try {
            SSLTrustManager.enableSSL()//enable SSL so we can call the data export API
            final HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("Authorization", basicAuthHash);
            requestHeaders.set("Accept", 'text/html')
            final HttpEntity<String> entity = new HttpEntity<String>(requestHeaders);
            final HttpEntity<String> exchange = getExchange(url.toURI(), entity, String.class) as HttpEntity<String>
            final String elementPathsText = exchange.getBody()
            return ['assayFormat': extractSubTrees(elementPathsText, '/BARD/assay protocol/assay format/'),
                    'assayType': extractSubTrees(elementPathsText, '/BARD/assay protocol/assay type/'),
                    'biologicalProcess': extractSubTrees(elementPathsText, '/BARD External Ontology/biological process/')]
        } catch (Exception ee) {
            log.error(ee) //log the error and then continue
        }
    }

    List<String> extractSubTrees(String treeHTML, String basePath) {
        def xhtml = new XmlSlurper().parseText(treeHTML)
        def pathHtmlList = xhtml.text()
        List<String> pathList = StringUtils.split(pathHtmlList, System.getProperty('line.separator')).toList()
        List<String> pathListCleaned = pathList.collect { StringUtils.trimToNull(it) }
        pathListCleaned.removeAll([null])
        return pathListCleaned.findAll { String path -> path.startsWith(basePath) }.unique()
    }

    @Override
    public String getResourceContext() {
        return RestApiConstants.ELEMENT_RESOURCE;
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
        return new StringBuilder(externalUrlDTO.capUrl).
                append(resourceName).
                append('/list').
                toString();
    }

}
