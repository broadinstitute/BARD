package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.EntityService;
import bard.core.interfaces.SearchResult;
import bard.core.rest.spring.util.FilterParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;


public abstract class RESTAbstractEntityService<E extends Entity>
        implements EntityService<E> {
    private static final long serialVersionUID = 0xe6685070fd7a917el;
    static final Logger log = Logger.getLogger(RESTAbstractEntityService.class);

    protected final String baseURL;
    //this is thread safe so we can re-use it
    protected final ObjectMapper mapper = new ObjectMapper();

    // relative to the base URL
    public abstract String getResourceContext();

    // construct an entity from a json response
    protected abstract E getEntity(E e, JsonNode node);

    // construct an entity from a json search response
    protected abstract E getEntitySearch(E e, JsonNode node);

    // base URL; should have no trailing slash!
    protected RESTAbstractEntityService(String baseURL) {
        this.baseURL = baseURL;
    }

    protected E getEntity(JsonNode node) {
        return getEntity(null, node);
    }

    protected E getEntitySearch(JsonNode node) {
        return getEntitySearch(null, node);
    }

    protected String getResource(Object resource) {
        return new StringBuilder(getResource()).append(resource).toString();
    }

    protected String getResource() {
        return new StringBuilder(baseURL).append(getResourceContext()).append(FORWARD_SLASH).toString();
    }

    /**
     * something like 'plugins/badapple/prom/cid/'
     *
     * @return the relative url to the promiscuity plugin
     */
    protected String getPromiscuityResource(Long cid) {
        return new StringBuilder(baseURL).append("/plugins/badapple/prom/cid/").append(cid).append("?expand=true").toString();
    }
    public boolean isReadOnly() {
        return false;
    }


    protected SearchResult<E>
    getSearchResult(String resource, ServiceParams params) {
        return new RESTSearchResult(this, resource, params).build();
    }

    /*
     * subclass needs to override this to provide specific related
     * searchResults
     */
    //public abstract <T extends Entity> SearchResult<T> searchResult(E entity, Class<T> clazz);

    public SearchResult<E> searchResult() {
        return new RESTSearchResult
                (this, getResource(), null).build();
    }

    public SearchResult<E> searchResult(Object etag) {
        return new RESTSearchResult
                (this, getResource(ETAG + FORWARD_SLASH + getETagId(etag)), null).build();
    }

    public SearchResult<E> searchResult(ServiceParams params) {
        return getSearchResult(getResource(), params);
    }

    public SearchResult<E> filter(FilterParams params) {
        throw new UnsupportedOperationException
                ("Method currently not supported");
    }

    public SearchResult<E> search(SearchParams params) {
        return new RESTSearchResult
                (this, buildSearchResource(params), params).build();
    }

    //=== TODO: WRITE INTEGRATION TESTS FOR THE FOLLOWING

    /**
     * @param values
     * @param facets
     * @param root
     */
    protected void handleArrayNodeForGetEntitySearch(final List<E> values, final List<Value> facets, final JsonNode root) {
        ArrayNode node = (ArrayNode) root.get(COLLECTION);
        if (isNotNull(node)) {
            handleCollectionNode(node, values);
        } else { // now try "docs" for search
            handleDocsNode(values, facets, root);
        }
    }

    /**
     * @param node
     * @param values
     */
    protected void handleCollectionNode(final ArrayNode node, final List<E> values) {
        for (int i = 0; i < node.size(); ++i) {
            JsonNode n = node.get(i);
            values.add(getEntity(n));
        }
    }

    /**
     * @param response
     * @param etags
     */
    protected void extractETagFromHeader(final HttpResponse response, final List etags) {
        for (Header h : response.getHeaders(E_TAG)) {
            final String e = h.getValue();
            final String etag = e.substring(1, e.length() - 1);
            if (etags.indexOf(etag) < 0) {
                etags.add(etag);
            }
        }
    }

    public Map<String, List<String>> suggest(SuggestParams params) {
        final Map<String, List<String>> suggestions = new HashMap<String, List<String>>();
        final String suggestQuery;
        try {
            suggestQuery = buildSuggestQuery(params);
            final JsonNode root = executeGetRequest(suggestQuery);
            extractSuggestsPair(root, suggestions);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        return suggestions;
    }

    protected JsonNode executeGetRequest(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        InputStream is = null;
        try {
            HttpGet get = new HttpGet(url);

            HttpResponse response = httpClient.execute(get);

            HttpEntity entity = response.getEntity();
            if (entity != null
                    && response.getStatusLine().getStatusCode() == 200) {
                is = entity.getContent();
                return this.mapper.readTree(is);

            }
            //TODO: Handle null case
        } catch (IOException e) {
            log.error(e);
        } finally {
            closeInputStream(is);
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }

    /**
     * @param results
     * @param node
     */
    protected void addEntityToResults(List<E> results, JsonNode node) {
        if (isNotNull(node)) {
            if (node.isArray()) {
                ArrayNode array = (ArrayNode) node;
                for (int i = 0; i < array.size(); ++i) {
                    final JsonNode n = array.get(i);
                    addSingleEntity(results, n);
                }
            } else { // single element
                addSingleEntity(results, node);
            }
        }
    }

    public long size() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        long size = 0;
        InputStream is = null;
        try {
            final String resource = getResource(_COUNT);
            HttpGet get = new HttpGet(resource);
            HttpResponse response = httpclient.execute(get);

            HttpEntity entity = response.getEntity();
            if (entity != null
                    && response.getStatusLine().getStatusCode() == 200) {
                is = entity.getContent();
                BufferedReader br = new BufferedReader
                        (new InputStreamReader(is));
                size = Long.parseLong(br.readLine());
            }
        } catch (NumberFormatException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            closeInputStream(is);
            httpclient.getConnectionManager().shutdown();
        }
        return size;
    }

    public Collection<Value> getFacets(Object etag) {
        List<Value> facets = new ArrayList<Value>();
        final String resource = buildETagQuery(etag);
        final String url = getResource(resource);
        final JsonNode root = executeGetRequest(url);
        extractFacets(facets, root);
        return facets;
    }

    public int putETag(Object etag, Collection ids) {
        if (etag == null || ids == null || ids.isEmpty()) {
            final String message = "etag value and id list is expected";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        InputStream is = null;
        final DefaultHttpClient httpclient = new DefaultHttpClient();
        int count = 0;
        try {
            final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair(IDS, toString(ids)));

            final HttpPut put = new HttpPut
                    (getResource(ETAG + FORWARD_SLASH + getETagId(etag)));
            put.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));

            final HttpResponse response = httpclient.execute(put);
            final HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine()
                    .getStatusCode() == HTTP_SUCCESS_CODE) {
                is = entity.getContent();
                BufferedReader br = new BufferedReader
                        (new InputStreamReader(is));
                count = Integer.parseInt(br.readLine());

            }
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            closeInputStream(is);
            httpclient.getConnectionManager().shutdown();
        }
        return count;
    }

    protected List<E> get(final String resource,
                          final boolean expand,
                          final long top,
                          final long skip,
                          final List etags,
                          final List<Value> facets) {

        final DefaultHttpClient httpclient = new DefaultHttpClient();
        final List<E> values = new ArrayList<E>();
        InputStream is = null;
        try {
            final String url = buildGetQuery(resource, expand, top, skip);
            final HttpGet get = new HttpGet(url);
            final HttpResponse response = httpclient.execute(get);
            if (response.containsHeader(E_TAG)) {
                extractETagFromHeader(response, etags);
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine()
                    .getStatusCode() == HTTP_SUCCESS_CODE) {
                is = entity.getContent();
                buildEntityFromResponse(values, facets, is);
            }
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            closeInputStream(is);
            httpclient.getConnectionManager().shutdown();
        }
        return values;
    }

    public List<E> get(final long top, final long skip) {
        return get(getResource(), true, top, skip);
    }

    public Object newETag(final String name) {
        return newETag(name, null);
    }

    public Object newETag(final String name, final Collection ids) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair(NAME, name));
            if (ids != null && !ids.isEmpty()) {
                nvp.add(new BasicNameValuePair(IDS, toString(ids)));
            }

            final HttpPost post = new HttpPost(getResource(ETAG));
            post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));

            HttpResponse response = httpclient.execute(post);
            if (response.containsHeader(E_TAG)) {
                final ArrayList etags = new ArrayList();
                extractETagFromHeader(response, etags);
                // there should only be one ETag returned
                if (!etags.isEmpty()) {
                    return etags.get(0);
                }
            }
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return null;
    }

    protected List<E> get(String resource, boolean expand,
                          long top, long skip) {
        return get(resource, expand, top, skip, new ArrayList(), new ArrayList<Value>());
    }

    /**
     * @param values
     * @param root
     */
    protected void handleArrayNodeForGetEntity(final List<E> values, final JsonNode root) {
        final ArrayNode node = (ArrayNode) root;
        for (int i = 0; i < node.size(); ++i) {
            JsonNode n = node.get(i);
            values.add(getEntity(n));
        }
    }

    public E get(Object id) {
        final String uri = new StringBuilder(getResource(id)).append(QUESTION_MARK).append(EXPAND_TRUE).toString();
        final JsonNode jsonNodes = executeGetRequest(uri);
        if (isNotNull(jsonNodes)) {
            return getEntity(null, jsonNodes);
        }
        return null;
    }

    public Collection<E> get(Collection ids) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        List<E> results = new ArrayList<E>();
        InputStream is = null;
        try {
            final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair(IDS, toString(ids)));

            final String uri = new StringBuilder(getResource()).append(QUESTION_MARK).append(EXPAND_TRUE).toString();
            final HttpPost post = new HttpPost(uri);
            post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));

            final HttpResponse response = httpclient.execute(post);
            final HttpEntity entity = response.getEntity();
            if (entity != null
                    && response.getStatusLine().getStatusCode() == 200) {
                is = entity.getContent();
                JsonNode node = this.mapper.readTree(is);
                addEntityToResults(results, node);
            }
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            closeInputStream(is);
            httpclient.getConnectionManager().shutdown();
        }
        return results;
    }

    protected List<Value> getETags(long top, long skip) {
        final String url = buildQueryForCollectionOfETags(top, skip);
        final JsonNode rootNode = executeGetRequest(url);
        return extractETagsFromRoot(rootNode);
    }

    protected List<Value> extractETagsFromRoot(final JsonNode rootNode) {
        final List<Value> etags = new ArrayList<Value>();
        final ArrayNode node = (ArrayNode) rootNode.get(COLLECTION);
        if (isNotNull(node)) {
            for (int i = 0; i < node.size(); ++i) {
                final Value v = parseETag(node.get(i));
                if (v != null) {
                    etags.add(v);
                }
            }
        }
        return etags;
    }

    //=== Have UNIT TESTS
    protected int parseFacets(List<Value> facets, JsonNode node) {
        int nhits = 0;
        final JsonNode meta = node.get(META_DATA);
        if (isNotNull(meta)) {
            nhits = extractNumberHitsFromJson(meta);
            facets.addAll(extractFacetsFromJson(meta));
        }
        return nhits;
    }

    /**
     * @param metaNode
     * @return int
     */
    protected int extractNumberHitsFromJson(final JsonNode metaNode) {
        final JsonNode hitsNode = metaNode.get(NHIT);

        int nhits = 0;
        if (isNotNull(hitsNode)) {
            nhits = hitsNode.asInt();
        }
        return nhits;
    }

    /**
     * @param metaNode
     * @return list
     */
    protected List<Value> extractFacetsFromJson(final JsonNode metaNode) {
        final List<Value> facets = new ArrayList<Value>();
        final JsonNode facetNode = metaNode.get(FACETS);
        if (isNotNull(facetNode)) {
            final DataSource ds = getDataSource(EntityNamedSources.FacetSource);
            final ArrayNode array = (ArrayNode) facetNode;
            for (int i = 0; i < array.size(); ++i) {
                facets.add(parseFacet(ds, array.get(i)));
            }
        }
        return facets;
    }

    /**
     * @param facet
     * @param key
     * @param count
     */
    protected void extractFacetInfoAndCountFromNode(final Value facet, String key, final int count) {
        if (count > 0) {
            // doesn't make sense to include facets that have zero counts
            new IntValue(facet, key, count);
        }
    }

    /**
     * @param facet
     * @param countsForFacetNode
     */
    protected void extractCountKeyValuePairFromNode(final Value facet, final JsonNode countsForFacetNode) {
        for (Iterator<Map.Entry<String, JsonNode>>
                     iter = countsForFacetNode.fields(); iter.hasNext(); ) {
            final Map.Entry<String, JsonNode> me = iter.next();
            extractFacetInfoAndCountFromNode(facet, me.getKey(), me.getValue().asInt());
        }
    }

    /**
     * @param ds
     * @param n
     * @return Value
     */
    protected Value parseFacet(final DataSource ds, final JsonNode n) {
        final String id = n.get(FACET_NAME).asText();
        final Value facet = new Value(ds, id);

        final JsonNode countsNode = n.get(COUNTS);
        if (isNotNull(countsNode)) {
            extractCountKeyValuePairFromNode(facet, countsNode);
        }
        return facet;
    }

    /**
     * @param params
     * @return String
     */
    protected String buildSearchResource(SearchParams params) {

        try {
            StringBuilder f =
                    new StringBuilder(baseURL).
                            append(FORWARD_SLASH).
                            append(SEARCH).
                            append(getResourceContext()).
                            append(FORWARD_SLASH).
                            append(QUESTION_MARK).
                            append(SOLR_QUERY_PARAM_NAME).
                            append(URLEncoder.encode(params.getQuery(), UTF_8));
            f.append(buildFilters(params));
            f.append(COMMA);
            return f.toString();
        } catch (Exception ex) {
            log.error(ex);
        }
        return null;
    }

    /**
     * @param params
     * @return String
     */
    protected String buildFilters(SearchParams params) {
        final StringBuilder f = new StringBuilder("");
        try {

            if (params.getFilters() != null
                    && !params.getFilters().isEmpty()) {
                f.append(FILTER);
                String sep = "";
                for (String[] entry : params.getFilters()) {
                    f.append(sep).
                            append(FQ).append(LEFT_PAREN).
                            append(URLEncoder.encode(entry[0], UTF_8)).
                            append(COLON)
                            .append(URLEncoder.encode(entry[1], UTF_8))
                            .append(RIGHT_PAREN);
                    sep = COMMA;
                }
            }

        } catch (Exception ex) {
            log.error(ex);
        }
        return f.toString();
    }

    /**
     * @return DataSource
     */
    public DataSource getDataSource() {
        return DataSource.getCurrent();
    }

    public DataSource getDataSource(String name, String version) {
        return new DataSource(name, version);
    }

    /**
     * @param name
     * @return DataSource
     */
    public DataSource getDataSource(String name) {
        return new DataSource(name);
    }

    /**
     * @param results
     * @param node
     */
    protected void addSingleEntity(List<E> results, JsonNode node) {
        if (isNotNull(node)) {
            E e = getEntity(null, node);
            results.add(e);
        }
    }

    /**
     * @param top
     * @param skip
     * @return String
     */
    protected String buildQueryForCollectionOfETags(long top, long skip) {
        return new StringBuilder(getResource()).
                append(ETAG).
                append(QUESTION_MARK).
                append(SKIP).
                append(skip).
                append(TOP).
                append(top).
                append(AMPERSAND).
                append(EXPAND_TRUE).
                toString();
    }

    /**
     * @param node
     * @param etag
     */
    protected void extractNameFromNode(JsonNode node, Value etag) {
        JsonNode nameNode = node.get(NAME);
        if (isNotNull(nameNode)) {
            new StringValue(etag, NAME, nameNode.asText());
        }
    }

    public boolean isNotNull(JsonNode jsonNode) {
        return jsonNode != null && !jsonNode.isNull();
    }

    /**
     * @param node
     * @param etag
     */
    protected void extractCountFromNode(JsonNode node, Value etag) {
        final JsonNode countNode = node.get(COUNT);
        if (isNotNull(countNode)) {
            new IntValue(etag, COUNT, countNode.asInt());
        }
    }

    /**
     * @param node
     * @param etag
     */
    protected void extractUrlFromNode(JsonNode node, Value etag) {
        JsonNode urlNode = node.get(URL_STRING);
        if (isNotNull(urlNode)) {
            new StringValue(etag, URL_STRING, urlNode.asText());
        }
    }

    /**
     * @param node
     * @param etag
     */
    protected void extractDescriptionFromNode(JsonNode node, Value etag) {
        JsonNode descriptionNode = node.get(DESCRIPTION);
        if (isNotNull(descriptionNode)) {
            new StringValue(etag, DESCRIPTION, descriptionNode.asText());
        }
    }

    /**
     * @param resource
     * @param expand
     * @param top
     * @param skip
     * @return
     */
    protected String buildGetQuery(final String resource,
                                   final boolean expand,
                                   final long top,
                                   final long skip) {
        return new StringBuilder(resource).
                append(!resource.contains(QUESTION_MARK) ? QUESTION_MARK : AMPERSAND).
                append(SKIP).
                append(skip).
                append(TOP).
                append(top).
                append(expand ? (AMPERSAND + EXPAND_TRUE) : "").toString();
    }

    /**
     * @param etag
     * @return String
     */
    protected final String buildETagQuery(final Object etag) {
        return new StringBuilder(ETAG).
                append(FORWARD_SLASH).
                append(getETagId(etag)).
                append(FORWARD_SLASH).
                append(FACETS).
                toString();
    }

    /**
     * @param etag
     * @return String
     */
    protected String getETagId(Object etag) {
        String eTagId = null;
        if (etag != null) {
            if (etag instanceof Value) {
                eTagId = ((Value) etag).getId();
            } else {
                eTagId = etag.toString();
            }
        }
        return eTagId;
    }

    /**
     * @param params
     * @return String
     * @throws UnsupportedEncodingException
     */
    protected String buildSuggestQuery(SuggestParams params) throws UnsupportedEncodingException {
        return new StringBuilder(baseURL).
                append(FORWARD_SLASH).
                append(SEARCH).
                append(getResourceContext()).
                append(FORWARD_SLASH).
                append(SUGGEST).append(QUESTION_MARK).
                append(SOLR_QUERY_PARAM_NAME).
                append(URLEncoder.encode(params.getQuery(), UTF_8)).
                append(TOP).append(params.getNumSuggestion()).toString();
    }

    /**
     * @param values
     * @param facets
     * @param root
     */
    protected void handleDocsNode(final List<E> values, final List<Value> facets, final JsonNode root) {
        ArrayNode node = (ArrayNode) root.get(DOCS);
        for (int i = 0; i < node.size(); ++i) {
            JsonNode n = node.get(i);
            values.add(getEntitySearch(n));
        }
        int count = parseFacets(facets, root);
        // internal value representing hit count
        facets.add(new IntValue
                (getDataSource(), HIT_COUNT_,
                        count));

    }

    /**
     * @param root
     * @param fieldName
     * @param suggestions
     */
    protected void addSuggestPair(final JsonNode root,
                                  final String fieldName,
                                  final Map<String, List<String>> suggestions) {
        if (!fieldName.equalsIgnoreCase(QUERY_STRING)) {
            final JsonNode node = root.get(fieldName);
            final List<String> values = new ArrayList<String>();
            for (int i = 0; i < node.size(); i++) {
                values.add(node.get(i).asText());
            }
            suggestions.put(fieldName, values);
        }
    }

    /**
     * @param root
     * @param suggestions
     */
    protected void extractSuggestsPair(final JsonNode root, final Map<String, List<String>> suggestions) {
        final Iterator<String> filter = root.fieldNames();
        while (filter.hasNext()) {
            final String fieldName = filter.next();
            addSuggestPair(root, fieldName, suggestions);
        }
    }

    /**
     * @param node
     * @return
     */
    protected Value extractETagIdFromNode(JsonNode node) {
        JsonNode root = node.get(ETAG_ID);
        Value etag = null;
        DataSource ds = getDataSource();
        if (isNotNull(root)) {
            etag = new Value(ds, root.asText());
        }
        return etag;
    }

    /**
     * @param ids
     * @return String
     */
    protected String toString(Collection ids) {
        final List sb = new ArrayList();
        for (Object id : ids) {
            if (id instanceof Entity) {
                sb.add(((Entity) id).getId());
            } else {
                sb.add(id.toString());
            }
        }
        if (!sb.isEmpty()) {
            return StringUtils.join(sb, COMMA);
        }
        return null;
    }

    protected void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException ee) {
            log.error(ee);
        }
    }

    public void shutdown() {
    }

    /**
     * @param node
     * @return
     */
    protected Value parseETag(JsonNode node) {
        final Value etag = extractETagIdFromNode(node);
        if (etag != null) {
            extractNameFromNode(node, etag);
            extractCountFromNode(node, etag);
            extractUrlFromNode(node, etag);
            extractDescriptionFromNode(node, etag);
        }
        return etag;
    }

    /**
     * @param facets
     * @param root
     */
    protected void extractFacets(final List<Value> facets, final JsonNode root) {
        if (root.isArray()) {
            DataSource ds = getDataSource(Entity.FacetSource);
            ArrayNode array = (ArrayNode) root;
            for (int i = 0; i < array.size(); ++i) {
                facets.add(parseFacet(ds, array.get(i)));
            }
        }
    }

    /**
     * @param values
     * @param facets
     * @param is
     * @throws IOException
     */
    protected void buildEntityFromResponse(final List<E> values, final List<Value> facets, final InputStream is) throws IOException {
        JsonNode root = this.mapper.readTree(is);
        if (root.isArray()) {
            handleArrayNodeForGetEntity(values, root);
        } else {
            handleArrayNodeForGetEntitySearch(values, facets, root);
        }
    }
}
