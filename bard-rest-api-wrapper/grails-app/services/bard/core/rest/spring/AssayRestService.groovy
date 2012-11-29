package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.EntityService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.assays.AssayAnnotation
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.assays.FreeTextAssayResult
import bard.core.rest.spring.util.MetaData

class AssayRestService extends AbstractRestService {

    public List<AssayAnnotation> findAnnotations(final Long adid) {
        final String resource = getResource(adid.toString() + EntityService.ANNOTATIONS)
        final URL url = new URL(resource)
        final List<AssayAnnotation> annotations = (this.restTemplate.getForObject(url.toURI(), AssayAnnotation[].class)) as List<AssayAnnotation>
        return annotations;
    }
    /**
     *
     * @param adid
     * @return {@link bard.core.rest.spring.assays.ExpandedAssay}
     */
    public ExpandedAssay getAssayById(final Long adid) {
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: adid, expand: "true"]
        ExpandedAssay assay = this.restTemplate.getForObject(url, ExpandedAssay.class, map)
        return assay;
    }
    /**
     *
     * @param list of pids
     * @return {@link bard.core.rest.spring.project.ExpandedProjectResult}
     */
    public ExpandedAssayResult searchAssaysByIds(final List<Long> adids) {
        final Map<String, Long> etags = [:]
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("ids", adids.join(","));

        HttpHeaders headers = new HttpHeaders();
        this.addETagsToHTTPHeader(headers, etags)
        HttpEntity<List> entity = new HttpEntity<List>(map, headers);
        final String url = this.buildURLToPostIds()
        final HttpEntity<List> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, ExpandedAssay[].class);
        final List<ExpandedAssay> assays = exchange.getBody()
        headers = exchange.getHeaders()
        this.extractETagFromResponseHeader(headers, 0, etags)
        int nhits = assays?.size();

        if (nhits == 0) {
            nhits = assays.size();
        }
        final ExpandedAssayResult assayResult = new ExpandedAssayResult()
        assayResult.setAssays(assays)
        assayResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        assayResult.setMetaData(metaData)
        return assayResult

    }
    /**
     *
     * @param searchParams
     * @return {@link bard.core.rest.spring.assays.FreeTextAssayResult}
     */
    public FreeTextAssayResult findAssaysByFreeTextSearch(SearchParams searchParams) {
        final String urlString = this.buildSearchURL(searchParams)
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlString)
        final FreeTextAssayResult assaySearchResult = this.restTemplate.getForObject(url.toURI(), FreeTextAssayResult.class)
        return assaySearchResult
    }

    public String getResourceContext() {
        return EntityService.ASSAYS_RESOURCE;
    }
    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = EntityService.ASSAYS_RESOURCE
        return new StringBuilder(baseUrl).
                append(EntityService.FORWARD_SLASH).
                append(EntityService.SEARCH).
                append(resourceName).
                append(EntityService.FORWARD_SLASH).
                append(EntityService.QUESTION_MARK).
                toString();
    }

    @Override
    public String getResource() {
        String resourceName = EntityService.ASSAYS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(EntityService.FORWARD_SLASH).
                toString();
    }
}
