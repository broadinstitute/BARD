package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.AssayAnnotation
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.assays.FreeTextAssayResult
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class AssayRestService extends AbstractRestService {

    public List<AssayAnnotation> findAnnotations(final Long adid) {
        final String resource = getResource(adid.toString() + RestApiConstants.ANNOTATIONS)
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
        if (adids) {
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
            this.extractETagsFromResponseHeader(headers, 0, etags)
            int nhits = assays.size();
            final ExpandedAssayResult assayResult = new ExpandedAssayResult()
            assayResult.setAssays(assays)
            assayResult.setEtags(etags)
            final MetaData metaData = new MetaData()
            metaData.nhit = nhits
            assayResult.setMetaData(metaData)
            return assayResult
        }
        return null

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
        return RestApiConstants.ASSAYS_RESOURCE;
    }
    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = RestApiConstants.ASSAYS_RESOURCE
        return new StringBuilder(baseUrl).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.SEARCH).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.QUESTION_MARK).
                toString();
    }

    @Override
    public String getResource() {
        String resourceName = RestApiConstants.ASSAYS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public List<ExperimentSearch> findExperimentsByAssayId(final Long adid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(adid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())

        List<ExperimentSearch> experiments = (List<ExperimentSearch>) this.restTemplate.getForObject(url.toURI(), ExperimentSearch[].class)
        return experiments
    }

    public ProjectResult findProjectsByAssayId(final Long adid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(adid.toString())).
                    append(RestApiConstants.PROJECTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        projectResult

    }
}
