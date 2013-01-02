package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.exceptions.RestApiException
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.ETagCollection
import bard.core.rest.spring.util.Facet
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.StopWatch
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

abstract class AbstractRestService {
    String baseUrl
    String promiscuityUrl
    RestTemplate restTemplate
    final static int multiplier = 5;
    LoggerService loggerService

    /**
     * @param params
     * @return String
     * @throws UnsupportedEncodingException
     */
    protected String buildSuggestQuery(SuggestParams params) throws UnsupportedEncodingException {
        return new StringBuilder(baseUrl).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.SEARCH).
                append(getResourceContext()).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.SUGGEST).
                append(RestApiConstants.QUESTION_MARK).
                append(RestApiConstants.SOLR_QUERY_PARAM_NAME).
                append(URLEncoder.encode(params.getQuery(), RestApiConstants.UTF_8)).
                append(RestApiConstants.TOP).append(params.getNumSuggestion()).toString();
    }

    String buildExperimentQuery(final Long experimentId, final String etag, final Integer top, final Integer skip) {
        final StringBuilder resource = new StringBuilder(getResource(experimentId.toString()));

        if (etag) {
            resource.append(RestApiConstants.FORWARD_SLASH)
            resource.append(RestApiConstants.ETAG).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(etag);
        }
        resource.append(RestApiConstants.EXPTDATA_RESOURCE).
                append(RestApiConstants.QUESTION_MARK);
        if (top) {
            resource.append(RestApiConstants.SKIP).
                    append(skip).
                    append(RestApiConstants.TOP).
                    append(top).
                    append(RestApiConstants.AMPERSAND)
        }
        resource.append(RestApiConstants.EXPAND_TRUE);
        return resource.toString();
    }

    /**
     * @param top
     * @param skip
     * @return String
     */
    protected String buildQueryForCollectionOfETags(long top, long skip) {
        return new StringBuilder(getResource()).
                append(RestApiConstants.ETAG).
                append(RestApiConstants.QUESTION_MARK).
                append(RestApiConstants.SKIP).
                append(skip).
                append(RestApiConstants.TOP).
                append(top).
                append(RestApiConstants.AMPERSAND).
                append(RestApiConstants.EXPAND_TRUE).
                toString();
    }

    /**
     * @param etag
     * @return String
     */
    protected final String buildETagQuery(final String etag) {
        return new StringBuilder(RestApiConstants.ETAG).
                append(RestApiConstants.FORWARD_SLASH).
                append(etag).
                toString();
    }

    /**
     *  Get the URL to get a Compound. This is  url template so replace {id} with the
     *  real ID
     * @return the url
     */
    public String buildEntityURL() {
        return new StringBuilder(getResource()).
                append("{id}").
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public String buildURLToCreateETag() {
        return new StringBuilder(getResource()).append(RestApiConstants.ETAG).toString();
    }

    public String buildURLToPutETag() {
        return new StringBuilder(buildURLToCreateETag()).append(RestApiConstants.FORWARD_SLASH).append().toString()
    }

    public String buildURLToPostIds() {
        StringBuilder url = new StringBuilder(getResource())
        url.append(RestApiConstants.QUESTION_MARK).append(RestApiConstants.EXPAND_TRUE)
        return url.toString()
    }

    protected String getResource(final String resource) {
        return new StringBuilder(getResource()).append(resource).toString();
    }
    // TODO: This method no longer needed. We need to pass top and skip to all
    //resources. So no need to CAP
    protected int findNextTopValue(long skip, int ratio) {
        ///cap this at 1000
        if (skip > 1000) {
            return 1000;
        }
        return ratio;
    }

    /**
     * @param resource
     * @param expand
     * @param top
     * @param skip
     * @return
     */
    protected String addTopAndSkip(final String resource,
                                   final boolean expand,
                                   final long top = 10,
                                   final long skip = 0) {
        return new StringBuilder(resource).
                append(!resource.contains(RestApiConstants.QUESTION_MARK) ? RestApiConstants.QUESTION_MARK : RestApiConstants.AMPERSAND).
                append(RestApiConstants.SKIP).
                append(skip).
                append(RestApiConstants.TOP).
                append(top).
                append(expand ? (RestApiConstants.AMPERSAND + RestApiConstants.EXPAND_TRUE) : "").toString();
    }


    static String getParentETag(Map<String, Long> etags) {
        String mintag = "";
        Long minval = null;
        for (String key : etags.keySet()) {
            final long value = etags.get(key)
            if (!minval || minval > value) {
                mintag = key;
                minval = value;
            }
        }
        return mintag;
    }

    public void extractETagsFromResponseHeader(final HttpHeaders headers, final long skip, final Map<String, Long> etags) {
        if (headers.containsKey(RestApiConstants.E_TAG) && etags != null) {
            final String etag = headers.getFirst(RestApiConstants.E_TAG)
            String e = etag.replaceAll("\"", "")
            etags.put(e, skip);
        }
    }

    public void addETagsToHTTPHeader(HttpHeaders requestHeaders, final Map<String, Long> etags) {
        if (etags) {
            String etag = getParentETag(etags);
            requestHeaders.set("If-Match", "\"" + etag + "\"");
        }
    }
    /**
     * Build a search url from the params
     * @param searchParams
     * @return a fully encoded search url
     */
    public String buildSearchURL(SearchParams searchParams) {
        final StringBuilder urlBuilder = new StringBuilder()
        urlBuilder.append(getSearchResource()).
                append(RestApiConstants.SOLR_QUERY_PARAM_NAME).
                append(URLEncoder.encode(searchParams.getQuery(), RestApiConstants.UTF_8));
        urlBuilder.append(buildFilters(searchParams));
        if (searchParams.getFilters()) {
            urlBuilder.append(RestApiConstants.COMMA);
        }

        if (searchParams.getSkip() || searchParams.getTop()) {
            return addTopAndSkip(urlBuilder.toString(), true, searchParams.getTop(), searchParams.getSkip())
        }
        return urlBuilder.toString();

    }
    /**
     * @param params
     * @return String
     */
    protected String buildFilters(SearchParams params) {
        final StringBuilder f = new StringBuilder("");
        if (params.getFilters()) {
            f.append(RestApiConstants.FILTER);
            String sep = "";
            for (String[] entry : params.getFilters()) {
                f.append(sep).
                        append(RestApiConstants.FQ).append(RestApiConstants.LEFT_PAREN).
                        append(URLEncoder.encode(entry[0], RestApiConstants.UTF_8)).
                        append(RestApiConstants.COLON)
                        .append(URLEncoder.encode(entry[1], RestApiConstants.UTF_8))
                        .append(RestApiConstants.RIGHT_PAREN);
                sep = RestApiConstants.COMMA;
            }
        }
        return f.toString();
    }

    public Map<String, List<String>> suggest(SuggestParams params) {
        final String resource = buildSuggestQuery(params)
        final URL url = new URL(resource)
        final Map<String, List<String>> suggestions = (Map) getForObject(url.toURI(), Map.class)
        return suggestions;
    }

    public List<ETag> findAllETagsForResource() {
        List<ETag> etags = new ArrayList<ETag>()
        int top = multiplier * multiplier;
        int ratio = multiplier;
        int skip = 0;
        while (true) {
            etags.addAll(getETags(top, skip));
            skip += etags.size();
            ratio *= multiplier;
            if (etags.size() < top) {
                break;
            }
            top = findNextTopValue(skip, ratio);
        }
        return etags;
    }

    public List<ETag> getETags(long top, long skip) {
        final String resource = buildQueryForCollectionOfETags(top, skip);
        final URL url = new URL(resource)
        //Using ETag[] to get around issue reported here : https://jira.springsource.org/browse/SPR-7002
        ETagCollection eTagCollection = (ETagCollection) getForObject(url.toURI(), ETagCollection.class)

        return eTagCollection.etags
    }


    public List<Facet> getFacetsByETag(String etag) {
        final String resource = buildETagQuery(etag) + RestApiConstants.FORWARD_SLASH + RestApiConstants.FACETS

        final String urlString = getResource(resource);
        final URL url = new URL(urlString)
        //Using Facte[] to get around issue reported here : https://jira.springsource.org/browse/SPR-7002
        final List<Facet> facets = (getForObject(url.toURI(), Facet[].class)) as List<Facet>

        return facets;
    }

    public String newETag(final String name, final List<Long> ids) {
        final Map<String, Long> etags = [:]
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        if (ids) {
            map.add("ids", ids.join(","));
        }
        map.add("name", name)

        final HttpEntity entity = new HttpEntity(map, new HttpHeaders());
        final String url = this.buildURLToCreateETag()

        final HttpEntity exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        HttpHeaders headers = exchange.getHeaders()

        this.extractETagsFromResponseHeader(headers, 0, etags)

        // there should only be one ETag returned
        return firstETagFromMap(etags)
    }
    /**
     *   We will only get the first etag from the map, actually right now only one is returned
     *
     */
    protected String firstETagFromMap(final Map<String, Long> etags) {
        if (!etags.isEmpty()) {
            return etags.keySet().iterator().next().toString()
        }
        return null
    }

    protected void validatePutETag(final String etag, final List<Long> ids) {
        if (StringUtils.isBlank(etag) || !ids) {
            final String message = "etag value and id list is expected";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public int putETag(final String etag, final List<Long> ids) {
        validatePutETag(etag, ids)
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("ids", ids.join(","));

        final HttpEntity<Integer> entity = new HttpEntity(map, new HttpHeaders());
        final String url = this.buildURLToPutETag() + etag
        final HttpEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        Integer count = Integer.parseInt(exchange.getBody())
        return count;
    }

    /**
     * Get a count of entities making up a resource
     * @return the number of  entities
     */
    public long getResourceCount() {
        final String resource = getResource(RestApiConstants._COUNT);
        return getResourceCount(resource);
    }
    /**
     * Get a count of entities making up a resource
     * @return the number of  entities
     */
    public long getResourceCount(final String resource) {

        final URL url = new URL(resource)
        final String countString = (String) getForObject(url.toURI(), String.class)
        Long count = Long.parseLong(countString)
        return count;
    }
    /**
     * Get a count of entities making up a resource
     * @return the number of  entities
     */
    public long getResourceCount(final SearchParams searchParams) {
        final StringBuilder resource = new StringBuilder(getResource(RestApiConstants._COUNT));
        if (searchParams.getTop()) {
            resource.append(RestApiConstants.QUESTION_MARK);
            resource.append(RestApiConstants.SKIP).
                    append(searchParams.getSkip()).
                    append(RestApiConstants.TOP).
                    append(searchParams.getTop())
        }
        resource.append(buildFilters(searchParams))
        return getResourceCount(resource.toString())
    }

    public Object getForObject(URI uri, Class clazz) {
        try {
            StopWatch sw = this.loggerService.startStopWatch()
            def result = this.restTemplate.getForObject(uri, clazz)
            this.loggerService.stopStopWatch(sw, "method=getForObject(URI uri, Class clazz); uri='${uri}'; class=${clazz}")
            return result
        } catch (RestClientException restClientException) {
            log.error(uri.toString(), restClientException)
            throw new RestApiException(restClientException)
        }

    }

    public Object getForObject(final String uri, final Class clazz, Map map = [:]) {
        try {
            StopWatch sw = this.loggerService.startStopWatch()
            def result = this.restTemplate.getForObject(uri, clazz, map)
            this.loggerService.stopStopWatch(sw, "method=getForObject(final String uri, final Class clazz, Map map = [:]); uri='${uri}'; class=${clazz}; map=${map}")
            return result
        } catch (RestClientException restClientException) {
            final String uriString = uri + map
            log.error(uriString, restClientException)
            throw new RestApiException(restClientException)
        }

    }

    public Object postForObject(final URI uri, final Class clazz, Map map = [:]) {
        try {
            StopWatch sw = this.loggerService.startStopWatch()
            def result = this.restTemplate.postForObject(uri, map, clazz)
            this.loggerService.stopStopWatch(sw, "method=postForObject(final URI uri, final Class clazz, Map map = [:]); uri='${uri}'; class=${clazz}; map=${map}")
            return result
        } catch (RestClientException restClientException) {
            final String uriString = uri.toString() + map
            log.error(uriString, restClientException)
            throw new RestApiException(restClientException)
        }
    }

    public Object postExchange(String url, HttpEntity<List> entity, Class clazz) {
        try {
            StopWatch sw = this.loggerService.startStopWatch()
            def result = restTemplate.exchange(url, HttpMethod.POST, entity, clazz);
            this.loggerService.stopStopWatch(sw, "method=postExchange(String url, HttpEntity<List> entity, Class clazz); url='${url}'; entity=${entity?.dump()}; class=${clazz}")
            return result
        } catch (RestClientException restClientException) {
            log.error(url.toString(), restClientException)
            throw new RestApiException(restClientException)
        }
    }

    public Object getExchange(URI uri, HttpEntity<List> entity, Class clazz) {
        try {
            StopWatch sw = this.loggerService.startStopWatch()
            def result = restTemplate.exchange(uri, HttpMethod.GET, entity, clazz);
            this.loggerService.stopStopWatch(sw, "method=getExchange(URI uri, HttpEntity<List> entity, Class clazz); uri='${uri}'; entity=${entity?.dump()}; class=${clazz}")
            return result
        } catch (RestClientException restClientException) {
            log.error(uri.toString(), restClientException)
            throw new RestApiException(restClientException)
        }
    }


    public abstract String getResource();

    public abstract String getSearchResource();

    public abstract String getResourceContext();
}
