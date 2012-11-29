package bard.core.rest.spring

import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.SearchParams
import bard.core.rest.spring.util.StructureSearchParams
import bard.core.interfaces.EntityService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundAnnotations
import bard.core.rest.spring.compounds.ExpandedCompoundResult
import bard.core.rest.spring.util.MetaData

class CompoundRestService extends AbstractRestService {
    protected String buildQueryForTestedAssays(final Long cid,
                                               final boolean activeOnly) {
        StringBuilder url = new StringBuilder();
        url.append(getResource(cid.toString())).
                append(EntityService.ASSAYS_RESOURCE).
                append(EntityService.QUESTION_MARK).
                append(EntityService.EXPAND_TRUE);
        if (activeOnly) {
            url.append(EntityService.FILTER_ACTIVE);
        }
        return url.toString();

    }

    public List<Assay> getTestedAssays(Long cid,
                                             boolean activeOnly) {
        final String resource = buildQueryForTestedAssays(cid, activeOnly);
        final URL url = new URL(resource)
        final AssayResult assayResult = this.restTemplate.getForObject(url.toURI(), AssayResult.class)
        return assayResult.assays
    }

    public CompoundAnnotations findAnnotations(Long cid) {
        final String resource = getResource(cid.toString() + EntityService.ANNOTATIONS)
        final URL url = new URL(resource)
        final CompoundAnnotations annotations = this.restTemplate.getForObject(url.toURI(), CompoundAnnotations.class)
        return annotations;
    }
    /**
     * something like 'plugins/badapple/prom/cid/'
     * The url to get a promiscuity score from the badapple plugin
     * Url is a URLTemplate
     * @return the relative url to the promiscuity plugin
     */
    public String buildPromiscuityScoreURL() {
        return new StringBuilder(this.promiscuityUrl).append("{cid}").append("?expand={expand}&repr={mediaType}").toString();
    }

    public String getResourceContext() {
        return EntityService.COMPOUNDS_RESOURCE;
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = EntityService.COMPOUNDS_RESOURCE
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
        String resourceName = EntityService.COMPOUNDS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(EntityService.FORWARD_SLASH).
                toString();
    }

    /**
     * Create a url to do a structure search
     * @param structureSearchParam
     * @return url for structure searches
     */
    public String buildStructureSearchURL(final StructureSearchParams structureSearchParam) {
        final StructureSearchParams.Type structureType = structureSearchParam.type
        final Double threshold = structureSearchParam.threshold
        final String query = structureSearchParam.query
        final String resource = getResource()
        final StringBuilder url = new StringBuilder();
        try {
            url.append(resource).append(EntityService.FILTER_QUESTION).append(
                    URLEncoder.encode(query, EntityService.UTF_8)).append(
                    EntityService.STRUCTURE);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex);
            throw new IllegalArgumentException
            ("Bogus query: " + query);
        }

        switch (structureType) {
            case StructureSearchParams.Type.Substructure:
                url.append(EntityService.TYPE_SUB);
                break;
            case StructureSearchParams.Type.Superstructure:
                url.append(EntityService.TYPE_SUP);
                break;
            case StructureSearchParams.Type.Exact:
                url.append(EntityService.TYPE_EXACT);
                break;
            case StructureSearchParams.Type.Similarity:
                url.append(EntityService.TYPE_SIM);
                if (threshold != null) {
                    url.append(EntityService.CUTOFF).append(String.format('%1$.3f', threshold));
                } else {
                    final String message = "No threshold specified for similarity search!";
                    log.error(message);
                    throw new IllegalArgumentException
                    (message);
                }
                break;
        }
        url.append(EntityService.AMPERSAND);
        url.append(EntityService.EXPAND_TRUE);
        long skip = structureSearchParam.getSkip() ?: 0
        long top = structureSearchParam.getTop() ?: 100

        url.append(EntityService.TOP).
                append(top).
                append(EntityService.AMPERSAND).
                append(EntityService.SKIP).
                append(skip);

        return url.toString();
    }

    /**
     *
     * @param compound
     * @return {@link bard.core.rest.spring.compounds.Compound}
     */
    public Compound getCompoundById(Long cid) {
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: cid, expand: "true"]

        final List<Compound> compoundTemplates = this.restTemplate.getForObject(url, List.class, map)
        if (compoundTemplates) {
            return compoundTemplates.get(0)
        }
        return null;
    }
    /**
     *
     * @param compound
     * @return {@link String}'s to be used as Synonyms
     */
    public List<String> getSynonymsForCompound(Long cid) {
        final String url = this.buildEntityURL() + EntityService.SYNONYMS + "?expand={expand}"
        final List<String> synonyms = this.restTemplate.getForObject(url, List.class, cid, "true")
        return synonyms;
    }

    /**
     *
     * @param params
     * @return list of {@link Compound}'s
     */
    public ExpandedCompoundResult structureSearch(StructureSearchParams params, final Map<String, Long> etags = [:]) {


        final List<Compound> compoundTemplates = []
        if (!params.getSkip()) {
            params.setSkip(0)
        }
        if (!params.getTop()) {
            params.setTop(100)
        }
        long top = params.getTop()
        long skip = params.getSkip()

        int nhits = 0
        while (true) {
            HttpHeaders requestHeaders = new HttpHeaders();
            addETagsToHTTPHeader(requestHeaders, etags)
            HttpEntity<List> entity = new HttpEntity<List>(requestHeaders);


            final String structureSearchURL = buildStructureSearchURL(params)
            final URL url = new URL(structureSearchURL)
            //We are passing the URI because we have already encoded the string
            //just passing in the string would cause the URI to be encoded twice
            //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
            final HttpEntity<List> exchange = restTemplate.exchange(url.toURI(), HttpMethod.GET, entity, List.class);
            List<Compound> results = exchange.getBody()
            compoundTemplates.addAll(results)
            final HttpHeaders headers = exchange.getHeaders()
            extractETagFromResponseHeader(headers, skip, etags)
            nhits += results.size();
            if (params.getTop() != null || results.size() < top) {
                break;
            }
            skip += results.size();
        }
        if (nhits == 0) {
            nhits = compoundTemplates.size();
        }
        ExpandedCompoundResult compoundSearchResult = new ExpandedCompoundResult()
        compoundSearchResult.setCompounds(compoundTemplates)
        compoundSearchResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        compoundSearchResult.setMetaData(metaData)
        return compoundSearchResult;
    }
    /**
     *
     * @param list of cids
     * @return {@link ExpandedCompoundResult}
     */
    public ExpandedCompoundResult searchCompoundsByIds(final List<Long> cids) {
        final Map<String, Long> etags = [:]
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("ids", cids.join(","));

        final HttpHeaders headers = new HttpHeaders();
        addETagsToHTTPHeader(headers, etags)
        final HttpEntity entity = new HttpEntity(map, headers);
        final String url = buildURLToPostIds()
        final HttpEntity<List> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
        final List<Compound> compounds = exchange.getBody()
        headers = exchange.getHeaders()
        extractETagFromResponseHeader(headers, 0, etags)
        int nhits = compounds?.size();

        if (nhits == 0) {
            nhits = compounds.size();
        }
        final ExpandedCompoundResult compoundSearchResult = new ExpandedCompoundResult()
        compoundSearchResult.setCompounds(compounds)
        compoundSearchResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        compoundSearchResult.setMetaData(metaData)
        return compoundSearchResult;

    }
    /**
     *
     * @param searchParams
     * @return {@link ExpandedCompoundResult}
     */
    public ExpandedCompoundResult findCompoundsByFreeTextSearch(SearchParams searchParams) {
        final String urlString = buildSearchURL(searchParams)
        println urlString
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlString)
        final ExpandedCompoundResult compoundSearchResult = this.restTemplate.getForObject(url.toURI(), ExpandedCompoundResult.class)
        return compoundSearchResult;
    }

    /**
     * this Should probably take a Compound as an arg, instead of a Long
     * @param cid -
     * @return {@link PromiscuityScore}
     */
    public PromiscuityScore findPromiscuityScoreForCompound(final Long cid) {
        final String url = buildPromiscuityScoreURL()
        final Map map = [cid: cid, expand: "true", mediaType: "XML"]
        final PromiscuityScore promiscuityScore = this.restTemplate.getForObject(url, PromiscuityScore.class, map)
        return promiscuityScore;
    }
}
