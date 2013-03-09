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

class ETagRestService extends AbstractRestService {


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
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }
//    public ETag getCompositeETag(String compositeETag){
//
//        getForObject()
//    }
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