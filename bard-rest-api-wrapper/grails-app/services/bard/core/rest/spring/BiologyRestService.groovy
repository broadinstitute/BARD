package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.biology.BiologyEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class BiologyRestService  extends AbstractRestService  {
    def transactional = false
    public String getResourceContext() {
        return RestApiConstants.BIOLOGY_RESOURCE;
    }

    public void  convertBiologyId() {

    }


    public List<BiologyEntity> convertBiologyId(final List<Long> bids) {
        if (bids) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("bids", bids.join(","));
            final String urlString = buildURLToBiologyData(true)
            final URL url = new URL(urlString)
            final List<BiologyEntity> biologyEntityList = this.postForObject(url.toURI(), BiologyEntity[].class, map) as List<BiologyEntity>;
            return biologyEntityList
        }
        return null

    }


    String buildURLToBiologyData(Boolean expand) {
        final StringBuilder resource =  new StringBuilder(getResource())

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
        return new StringBuilder(externalUrlDTO.baseUrl).
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
        return (new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH)).
                toString();
    }




}
