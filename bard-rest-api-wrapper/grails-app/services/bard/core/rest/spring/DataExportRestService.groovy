package bard.core.rest.spring

import bard.core.rest.spring.util.DictionaryElement
import bard.core.util.SSLTrustManager
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

class DataExportRestService extends AbstractRestService {
    String dataExportApiKey
    String dictionaryElementAcceptType
    String dataExportElementBaseURL


    String findDictionaryElementLabelById(final Long dictionaryId){
        final DictionaryElement dictionaryElement = findDictionaryElementById(dictionaryId)
        if(dictionaryElement){
            return dictionaryElement.label
        }
        return null
    }
    /**
     *
     * @param dictionaryId - Given a dictionary Id , return the element
     * @return the element
     */
    DictionaryElement findDictionaryElementById(final Long dictionaryId) {
        SSLTrustManager.enableSSL()//enable SSL so we can call the data export API
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("APIKEY", this.dataExportApiKey);
        requestHeaders.set("Accept", this.dictionaryElementAcceptType)
        final HttpEntity<DictionaryElement> entity = new HttpEntity<DictionaryElement>(requestHeaders);


        final URL url = new URL(this.dataExportElementBaseURL + dictionaryId.toString())
        final HttpEntity<DictionaryElement> exchange = getExchange(url.toURI(), entity, DictionaryElement.class) as HttpEntity<DictionaryElement>
        final DictionaryElement dictionaryElement = exchange.getBody()
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
