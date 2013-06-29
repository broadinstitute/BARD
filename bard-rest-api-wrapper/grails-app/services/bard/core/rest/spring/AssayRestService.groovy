package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants

import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.assays.Assay

class AssayRestService extends AbstractRestService {
    def transactional=false
    /**
     *
     * @param searchParams
     * @param map of etags
     * @return list of assays
     */
    public List<Assay> searchAssaysByCapIds(final SearchParams searchParams, Map<String, Long> etags) {
        if (etags) {
            final String etag = firstETagFromMap(etags)
            final String urlString = buildQueryForETag(searchParams, etag)
            final URL url = new URL(urlString)
            final List<Assay> assays = getForObject(url.toURI(), List.class) as List<Assay>
            return assays
        }
        return []
    }

    /**
     *
     * @param list of cap assay ids
     * @param searchParams
     * @param map of etags
     * @return {@link AssayResult}
     */
    public AssayResult searchAssaysByCapIds(final List<Long> capIds, final SearchParams searchParams) {
        if (capIds) {
            final Map<String, Long> etags = [:]
            final long skip = searchParams.getSkip()
            HttpHeaders requestHeaders = new HttpHeaders();
            HttpEntity<List> entity = new HttpEntity<List>(requestHeaders);


            final String urlString = buildSearchByCapIdURLs(capIds, searchParams, "capAssayId:")
            final URL url = new URL(urlString)
            final HttpEntity<AssayResult> exchange = getExchange(url.toURI(), entity, AssayResult.class) as HttpEntity<AssayResult>
            final AssayResult assaySearchResult = exchange.getBody()

            final HttpHeaders headers = exchange.getHeaders()
            extractETagsFromResponseHeader(headers, skip, etags)
            assaySearchResult.setEtags(etags)
            return assaySearchResult
        }

        return null

    }


    public BardAnnotation findAnnotations(final Long adid) {
        final String resource = getResource(adid.toString() + RestApiConstants.ANNOTATIONS)
        final URL url = new URL(resource)

        final BardAnnotation annotations = (BardAnnotation) getForObject(url.toURI(), BardAnnotation.class)
        annotations.populateContextMeasureRelationships()

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
        ExpandedAssay assay = (ExpandedAssay) getForObject(url, ExpandedAssay.class, map)
        return assay;
    }
    /**
     *
     * @param list of pids
     * @return {@link bard.core.rest.spring.assays.ExpandedAssayResult}
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
            final HttpEntity<List> exchange = postExchange(url, entity, ExpandedAssay[].class) as HttpEntity<List>
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
     * @return {@link bard.core.rest.spring.assays.AssayResult}
     */
    public AssayResult findAssaysByFreeTextSearch(SearchParams searchParams) {
        final String urlString = this.buildSearchURL(searchParams)
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlString)
        final AssayResult assayResult = (AssayResult) getForObject(url.toURI(), AssayResult.class)
        return assayResult
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
        return new StringBuilder(externalUrlDTO.ncgcUrl).
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
        return new StringBuilder(externalUrlDTO.ncgcUrl).
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

        List<ExperimentSearch> experiments = (List<ExperimentSearch>) getForObject(url.toURI(), ExperimentSearch[].class)
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
        ProjectResult projectResult = (ProjectResult) getForObject(url.toURI(), ProjectResult.class)
        projectResult

    }
}
