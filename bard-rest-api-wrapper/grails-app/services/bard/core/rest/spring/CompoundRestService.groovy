package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundAnnotations
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.MetaData
import bard.core.rest.spring.util.StructureSearchParams
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class CompoundRestService extends AbstractRestService {


    public String getResourceContext() {
        return RestApiConstants.COMPOUNDS_RESOURCE;
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = RestApiConstants.COMPOUNDS_RESOURCE
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
        String resourceName = RestApiConstants.COMPOUNDS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
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

    protected String buildQueryForTestedAssays(final Long cid,
                                               final boolean activeOnly) {
        StringBuilder url = new StringBuilder();
        url.append(getResource(cid.toString())).
                append(RestApiConstants.ASSAYS_RESOURCE).
                append(RestApiConstants.QUESTION_MARK).
                append(RestApiConstants.EXPAND_TRUE);
        if (activeOnly) {
            url.append(RestApiConstants.FILTER_ACTIVE);
        }
        return url.toString();

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
            url.append(resource).append(RestApiConstants.FILTER_QUESTION).append(
                    URLEncoder.encode(query, RestApiConstants.UTF_8)).append(
                    RestApiConstants.STRUCTURE);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex);
            throw new IllegalArgumentException
            ("Bogus query: " + query);
        }

        switch (structureType) {
            case StructureSearchParams.Type.Substructure:
                url.append(RestApiConstants.TYPE_SUB);
                break;
            case StructureSearchParams.Type.Superstructure:
                url.append(RestApiConstants.TYPE_SUP);
                break;
            case StructureSearchParams.Type.Exact:
                url.append(RestApiConstants.TYPE_EXACT);
                break;
            case StructureSearchParams.Type.Similarity:
                url.append(RestApiConstants.TYPE_SIM);
                validateSimilarityThreshold(threshold)
                url.append(RestApiConstants.CUTOFF).append(String.format('%1$.3f', threshold));
                break;
        }
        url.append(RestApiConstants.AMPERSAND);
        url.append(RestApiConstants.EXPAND_TRUE);
        //we cap this at 100 if top is not supplied
        long skip = structureSearchParam.getSkip() ?: 0
        long top = structureSearchParam.getTop() ?: 100

        url.append(RestApiConstants.TOP).
                append(top).
                append(RestApiConstants.AMPERSAND).
                append(RestApiConstants.SKIP).
                append(skip);

        return url.toString();
    }
    //If we are doing a similarity search then the client must specify
    //the threshold
    protected void validateSimilarityThreshold(Double threshold) {
        if (threshold == null) {
            final String message = "No threshold specified for similarity search!";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public List<Assay> getTestedAssays(Long cid, boolean activeOnly) {
        final String resource = buildQueryForTestedAssays(cid, activeOnly);
        final URL url = new URL(resource)
        final AssayResult assayResult = this.restTemplate.getForObject(url.toURI(), AssayResult.class)
        return assayResult.assays
    }

    public CompoundAnnotations findAnnotations(Long cid) {
        final String resource = getResource(cid.toString() + RestApiConstants.ANNOTATIONS)
        final URL url = new URL(resource)
        final CompoundAnnotations annotations = this.restTemplate.getForObject(url.toURI(), CompoundAnnotations.class)
        return annotations;
    }
    /**
     *
     * @param compound
     * @return {@link bard.core.rest.spring.compounds.Compound}
     */
    public Compound getCompoundById(Long cid) {
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: cid, expand: "true"]

        final List<Compound> compounds = this.restTemplate.getForObject(url, List.class, map)
        if (compounds) {
            return compounds.get(0)
        }
        return null;
    }
    /**
     *
     * @param list of cids
     * @return {@link CompoundResult}
     */
    public CompoundResult searchCompoundsByIds(final List<Long> cids) {
        if (cids) {
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
            extractETagsFromResponseHeader(headers, 0, etags)
            int nhits = compounds.size();

            final CompoundResult compoundSearchResult = new CompoundResult()
            compoundSearchResult.setCompounds(compounds)
            compoundSearchResult.setEtags(etags)
            final MetaData metaData = new MetaData()
            metaData.nhit = nhits
            compoundSearchResult.setMetaData(metaData)
            return compoundSearchResult;
        }
        return null
    }

    /**
     *
     * @param searchParams
     * @return {@link CompoundResult}
     */
    public CompoundResult findCompoundsByFreeTextSearch(SearchParams searchParams) {
        final String urlString = buildSearchURL(searchParams)
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlString)
        final CompoundResult compoundSearchResult = this.restTemplate.getForObject(url.toURI(), CompoundResult.class)
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
    /**
     *
     * @param compound
     * @return {@link String}'s to be used as Synonyms
     */
    public List<String> getSynonymsForCompound(Long cid) {
        final String url = this.buildEntityURL() + RestApiConstants.SYNONYMS + "?expand={expand}"
        final List<String> synonyms = this.restTemplate.getForObject(url, List.class, cid, "true")
        return synonyms;
    }

    /**
     *
     * @param params
     * @return list of {@link Compound}'s
     */
    public CompoundResult structureSearch(StructureSearchParams params, final Map<String, Long> etags = [:]) {


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
            extractETagsFromResponseHeader(headers, skip, etags)
            nhits += results.size();
            if (params.getTop() != null || results.size() < top) {
                break;
            }
            skip += results.size();
        }
        if (nhits == 0) {
            nhits = compoundTemplates.size();
        }
        CompoundResult compoundSearchResult = new CompoundResult()
        compoundSearchResult.setCompounds(compoundTemplates)
        compoundSearchResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        compoundSearchResult.setMetaData(metaData)
        return compoundSearchResult;
    }

    public ExperimentSearchResult findExperimentsByCID(final Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(cid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ExperimentSearchResult experimentResult = this.restTemplate.getForObject(url.toURI(), ExperimentSearchResult.class)
        return experimentResult

    }

    public AssayResult findAssaysByCID(Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(cid.toString())).
                    append(RestApiConstants.ASSAYS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        AssayResult assayResult = this.restTemplate.getForObject(url.toURI(), AssayResult.class)
        assayResult

    }

    public ProjectResult findProjectsByCID(Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(cid.toString())).
                    append(RestApiConstants.PROJECTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        projectResult

    }

    /**
     * Returns a list of Compounds (inside a CompoundResult wrapper) given an ETag eTagName
     * @param eTagName
     * @return
     */
    public CompoundResult findCompoundsByETag(String eTagName) {
        List<ETag> etags = findAllETagsForResource()
        ETag matchedETag = etags.find {ETag eTag -> eTag.name == eTagName};
        if (!matchedETag) {
            return new CompoundResult()
        }

        String urlToCompounds = getResource() + RestApiConstants.ETAG + RestApiConstants.FORWARD_SLASH + matchedETag.etag_id

        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlToCompounds)
        final List<Compound> compounds = this.restTemplate.getForObject(url.toURI(), Compound[].class) as List<Compound>
        CompoundResult compoundResult = new CompoundResult(compounds: compounds)
        return compoundResult
    }
}
