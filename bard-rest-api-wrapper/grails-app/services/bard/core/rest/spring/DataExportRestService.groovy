package bard.core.rest.spring

import bard.core.rest.spring.util.CapDictionary
import bard.core.rest.spring.util.DictionaryElement
import bard.core.util.SSLTrustManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

public class DataExportRestService extends AbstractRestService {
    String dataExportApiKey
    String dictionaryAcceptType
    String dataExportDictionaryURL
    final String SYNC_LOCK = ""
    //we keep the key value pair in a map so we can look them up easily
    Map<Long, DictionaryElement> dictionaryElementMap = [:]

    private void loadDictionary(CapDictionary capDictionary) {
        final List<DictionaryElement> dictionaryElements = capDictionary.elements ?: []
        for (DictionaryElement dictionaryElement : dictionaryElements) {
            dictionaryElementMap.put(dictionaryElement.elementId, dictionaryElement)
        }
    }

    public DataExportRestService() {

    }
    /**
     * This is called from bootstrap.groovy when the application loads.
     *
     * There aren't many dictionary elements (less than 2K at the time of writing) so we keep all of them in memory
     *
     * However, we should use a cache if this because a bottle neck
     *
     * In some cases the web application starts up before the data export api, or sometimes the data export api
     *
     * is down, so we are adding a conditional here, so we can force a reload if it evaluates to false
     */
    @Cacheable(value = 'dictionaryElements')
    public CapDictionary getDictionary() {
        try {
            SSLTrustManager.enableSSL()//enable SSL so we can call the data export API
            final HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("APIKEY", this.dataExportApiKey);
            requestHeaders.set("Accept", this.dictionaryAcceptType)
            final HttpEntity<CapDictionary> entity = new HttpEntity<CapDictionary>(requestHeaders);
            final URL url = new URL(this.dataExportDictionaryURL)
            final HttpEntity<CapDictionary> exchange = getExchange(url.toURI(), entity, CapDictionary.class) as HttpEntity<CapDictionary>
            final CapDictionary capDictionary = exchange.getBody()
            loadDictionary(capDictionary)
            return capDictionary
        } catch (Exception ee) {
            log.error(ee) //log the error and then continue
        }
        return new CapDictionary()

    }
    /**
     * @param dictionaryId - Given a dictionary Id , return the element
     * @return the element
     */
    @Cacheable(value = 'dictionaryElements')
    DictionaryElement findDictionaryElementById(final Long dictionaryId) {

        if (this.dictionaryElementMap.isEmpty()) {
            //this should force a reload of the cache, if the map is empty and the data export was down
            synchronized (SYNC_LOCK) {
               getDictionary()
            }
        }
        final DictionaryElement dictionaryElement = dictionaryElementMap.get(dictionaryId)
        return dictionaryElement
    }

    @Override
    public String getResourceContext() {
        return null
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
        return null
    }


}
enum ReloadCache {
    YES,
    NO
}
