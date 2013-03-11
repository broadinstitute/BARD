package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.CapDictionary
import bard.core.rest.spring.util.Node
import org.springframework.cache.annotation.Cacheable

public class DataExportRestService extends RestService {
   def transactional=false



    @Cacheable(value = 'dictionaryElements')
    public CapDictionary getDictionary() {
        final URL url = new URL(getResource())
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
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                toString();
    }


}

