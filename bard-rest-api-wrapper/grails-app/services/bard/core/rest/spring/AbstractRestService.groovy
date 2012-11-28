package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.interfaces.EntityService
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.ETagCollection
import bard.core.rest.spring.util.EntityType
import bard.core.rest.spring.util.Facet

abstract class AbstractRestService {
    String baseUrl
    String promiscuityUrl
    RestTemplate restTemplate
    final static int multiplier = 5;

    /**
     * @param params
     * @return String
     * @throws UnsupportedEncodingException
     */
    protected String buildSuggestQuery(SuggestParams params) throws UnsupportedEncodingException {
        return new StringBuilder(baseUrl).
                append(EntityService.FORWARD_SLASH).
                append(EntityService.SEARCH).
                append(getResourceContext()).
                append(EntityService.FORWARD_SLASH).
                append(EntityService.SUGGEST).
                append(EntityService.QUESTION_MARK).
                append(EntityService.SOLR_QUERY_PARAM_NAME).
                append(URLEncoder.encode(params.getQuery(), EntityService.UTF_8)).
                append(EntityService.TOP).append(params.getNumSuggestion()).toString();
    }

    public Map<String, List<String>> suggest(SuggestParams params) {
        final String resource = buildSuggestQuery(params)
        final URL url = new URL(resource)
        final Map<String, List<String>> suggestions = this.restTemplate.getForObject(url.toURI(), Map.class)
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

    protected int findNextTopValue(long skip, int ratio) {
        ///cap this at 1000
        if (skip > 1000) {
            return 1000;
        }
        return ratio;
    }

    String buildExperimentQuery(final Long experimentId, final String etag, final long top, final long skip) {
        final StringBuilder resource = new StringBuilder(getResource(experimentId.toString()));
        resource.append(EntityService.FORWARD_SLASH)

        if (etag) {
            resource.append(EntityService.ETAG).
                    append(EntityService.FORWARD_SLASH).
                    append(etag);
        }
        resource.append(EntityService.EXPTDATA_RESOURCE).
                append(EntityService.QUESTION_MARK).
                append(EntityService.SKIP).
                append(skip).
                append(EntityService.TOP).
                append(top).
                append(EntityService.AMPERSAND).
                append(EntityService.EXPAND_TRUE);
        return resource.toString();
    }

    public List<ETag> getETags(long top, long skip) {
        final String resource = buildQueryForCollectionOfETags(top, skip);
        final URL url = new URL(resource)
        //Using ETag[] to get around issue reported here : https://jira.springsource.org/browse/SPR-7002
        ETagCollection eTagCollection = this.restTemplate.getForObject(url.toURI(), ETagCollection.class)

        return eTagCollection.etags
    }
    /**
     * @param top
     * @param skip
     * @return String
     */
    protected String buildQueryForCollectionOfETags(long top, long skip) {
        return new StringBuilder(getResource()).
                append(EntityService.ETAG).
                append(EntityService.QUESTION_MARK).
                append(EntityService.SKIP).
                append(skip).
                append(EntityService.TOP).
                append(top).
                append(EntityService.AMPERSAND).
                append(EntityService.EXPAND_TRUE).
                toString();
    }
    /**
     * @param etag
     * @return String
     */
    protected final String buildETagQuery(final String etag) {
        return new StringBuilder(EntityService.ETAG).
                append(EntityService.FORWARD_SLASH).
                append(etag).
                append(EntityService.FORWARD_SLASH).
                append(EntityService.FACETS).
                toString();
    }

    public List<Facet> getFacetsByETag(String etag) {
        final String resource = buildETagQuery(etag);
        final String urlString = getResource(resource);
        final URL url = new URL(urlString)
        //Using Facte[] to get around issue reported here : https://jira.springsource.org/browse/SPR-7002
        final List<Facet> facets = (this.restTemplate.getForObject(url.toURI(), Facet[].class)) as List<Facet>

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

        this.extractETagFromResponseHeader(headers, 0, etags)

        // there should only be one ETag returned
        if (!etags.isEmpty()) {
            return etags.keySet().iterator().next().toString()
        }
        return null;
    }

    public void extractETagFromResponseHeader(final HttpHeaders headers, final long skip, final Map<String, Long> etags) {
        if (headers.containsKey(EntityService.E_TAG) && etags != null) {
            final String etag = headers.getFirst(EntityService.E_TAG)
            if (etag) {
                String e = etag.replaceAll("\"", "")
                etags.put(e, skip);
            }
        }
    }


    public void addETagsToHTTPHeader(HttpHeaders requestHeaders, final Map<String, Long> etags) {
        if (etags) {
            String etag = getParentETag(etags);
            requestHeaders.set("If-Match", "\"" + etag + "\"");
        }
    }

    public String buildURLToCreateETag(EntityType entityType = EntityType.COMPOUND) {
        return new StringBuilder(getResource()).append(EntityService.ETAG).toString();
    }

    public String buildURLToPutETag(EntityType entityType = EntityType.COMPOUND) {
        return new StringBuilder(getResource()).append(EntityService.ETAG).append(EntityService.FORWARD_SLASH).append().toString()
    }

    public String buildURLToPostIds(EntityType entityType = EntityType.COMPOUND) {
        StringBuilder url = new StringBuilder(getResource())
        url.append(EntityService.QUESTION_MARK).append(EntityService.AMPERSAND).append(EntityService.EXPAND_TRUE)
        return url.toString()
    }
    /**
     * Build a search url from the params
     * @param searchParams
     * @return a fully encoded search url
     */
    public String buildSearchURL(SearchParams searchParams) {
        final StringBuilder urlBuilder = new StringBuilder()
        urlBuilder.append(getSearchResource()).
                append(EntityService.SOLR_QUERY_PARAM_NAME).
                append(URLEncoder.encode(searchParams.getQuery(), EntityService.UTF_8));
        urlBuilder.append(buildFilters(searchParams));
        if (searchParams.getFilters()) {
            urlBuilder.append(EntityService.COMMA);
        }

        if (searchParams.getSkip() || searchParams.getTop()) {
            return addTopAndSkip(urlBuilder.toString(), true, searchParams.getTop(), searchParams.getSkip())
        }
        return urlBuilder.toString();

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
                append(!resource.contains(EntityService.QUESTION_MARK) ? EntityService.QUESTION_MARK : EntityService.AMPERSAND).
                append(EntityService.SKIP).
                append(skip).
                append(EntityService.TOP).
                append(top).
                append(expand ? (EntityService.AMPERSAND + EntityService.EXPAND_TRUE) : "").toString();
    }

    /**
     * @param params
     * @return String
     */
    protected String buildFilters(SearchParams params) {
        final StringBuilder f = new StringBuilder("");
        if (params.getFilters() != null
                && !params.getFilters().isEmpty()) {
            f.append(EntityService.FILTER);
            String sep = "";
            for (String[] entry : params.getFilters()) {
                f.append(sep).
                        append(EntityService.FQ).append(EntityService.LEFT_PAREN).
                        append(URLEncoder.encode(entry[0], EntityService.UTF_8)).
                        append(EntityService.COLON)
                        .append(URLEncoder.encode(entry[1], EntityService.UTF_8))
                        .append(EntityService.RIGHT_PAREN);
                sep = EntityService.COMMA;
            }
        }
        return f.toString();
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
    /**
     *  Get the URL to get a Compound. This is  url template so replace {id} with the
     *  real ID
     * @return the url
     */
    public String buildEntityURL() {
        return new StringBuilder(getResource()).
                append("{id}").
                append(EntityService.FORWARD_SLASH).
                toString();
    }

    public int putETag(final String etag, List<Long> ids) {
        if (StringUtils.isBlank(etag) || !ids) {
            final String message = "etag value and id list is expected";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("ids", ids.join(","));

        final HttpEntity<Integer> entity = new HttpEntity(map, new HttpHeaders());
        final String url = this.buildURLToPutETag() + etag
        final HttpEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        Integer count = Integer.parseInt(exchange.getBody())
        return count;
    }

    protected String getResource(final String resource) {
        return new StringBuilder(getResource()).append(resource).toString();
    }
    /**
     * Get a count of entities making up a resource
     * @return the number of  entities
     */
    public long getResourceCount() {
        final String resource = getResource(EntityService._COUNT);
        final URL url = new URL(resource)
        //Using Facte[] to get around issue reported here : https://jira.springsource.org/browse/SPR-7002
        final String countString = this.restTemplate.getForObject(url.toURI(), String.class)
        Long count = Long.parseLong(countString)
        return count;
    }

    public abstract String getResource();

    public abstract String getSearchResource();

    public abstract String getResourceContext();
}
