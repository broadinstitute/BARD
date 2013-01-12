package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.exceptions.RestApiException
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.MetaData
import bard.core.rest.spring.util.StructureSearchParams
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

import bard.core.rest.spring.compounds.*

class CompoundRestService extends AbstractRestService {
    ExecutorService executorService

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

    protected String buildQueryForCompoundSummary(final Long cid) {
        StringBuilder url = new StringBuilder();
        url.append(getResource(cid.toString())).
                append(RestApiConstants.SUMMARY).
                append(RestApiConstants.QUESTION_MARK).
                append(RestApiConstants.EXPAND_TRUE)

        return url.toString();
    }
    /**
     * Create a url to do a structure search
     * @param structureSearchParam
     * @return url for structure searches
     */
    public String buildStructureSearchURL(StructureSearchParams structureSearchParam, boolean withCount) {
        final StructureSearchParams.Type structureType = structureSearchParam.type
        final Double threshold = structureSearchParam.threshold
        final String query = structureSearchParam.query
        String resource = withCount ? getResource(RestApiConstants._COUNT) : getResource()
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

    public CompoundSummary getSummaryForCompound(final Long cid) {
        final String resource = buildQueryForCompoundSummary(cid)
        final URL url = new URL(resource)
        final CompoundSummary compoundSummary = (CompoundSummary) getForObject(url.toURI(), CompoundSummary.class)
        return compoundSummary
    }

    public List<Assay> getTestedAssays(Long cid, boolean activeOnly) {
        final String resource = buildQueryForTestedAssays(cid, activeOnly);
        final URL url = new URL(resource)
        final AssayResult assayResult = (AssayResult) getForObject(url.toURI(), AssayResult.class)
        return assayResult.assays
    }

    /**
     *
     * @param compound
     * @return {@link bard.core.rest.spring.compounds.Compound}
     */
    public Compound getCompoundById(Long cid) {
        //lets also get the annotations and the sids
        //prepare task to run concurrently add time out of 50 seconds
        def tasks = []
        tasks << (findAnnotations.curry(cid) as Callable)
        tasks << (findCompoundById.curry(cid) as Callable)
        //we set this to time out in 50 seconds
        final List<FutureTask<Object>> results = executorService.invokeAll(tasks, 50, TimeUnit.SECONDS)
        return handleCompoundByIdFutures(results)
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
            final HttpEntity<List> exchange = postExchange(url, entity, List.class) as HttpEntity<List>
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
        final CompoundResult compoundSearchResult = (CompoundResult) getForObject(url.toURI(), CompoundResult.class)
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
        final PromiscuityScore promiscuityScore = (PromiscuityScore) getForObject(url, PromiscuityScore.class, map)
        return promiscuityScore;

    }
    /**
     *
     * @param compound
     * @return {@link String}'s to be used as Synonyms
     */
    public List<String> getSynonymsForCompound(Long cid) {
        final String url = this.buildEntityURL() + RestApiConstants.SYNONYMS + "?expand={expand}"
        Map map = [expand: "true", id: cid]
        final List<String> synonyms = getForObject(url, List.class, map) as List<String>
        return synonyms;
    }

    /*
    * @param params
    * @return list of {@link Compound}'s
    */

    public CompoundResult structureSearch(StructureSearchParams params, Map<String, Long> etags = [:], Integer nhits = -1) {
        //first get the number of hits, we  should do this concurrently
        //TODO: If we already have the number of hits we do not need to get it again

        //prepare task to run concurrently add time out of 50 seconds
        def tasks = []
        if (nhits <= 0) { // we only do this if we do not already know the number of hits
            tasks << (getStructureCount.curry(params) as Callable)
        }
        tasks << (doStructureSearch.curry(params, etags) as Callable)
        //we set this to time out in 50 seconds
        final List<FutureTask<Object>> results = executorService.invokeAll(tasks, 50, TimeUnit.SECONDS)
        return handleStructureSearchFutures(results, nhits)
    }

    public ExperimentSearchResult findExperimentsByCID(final Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(cid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final ExperimentSearchResult experimentResult = (ExperimentSearchResult) getForObject(url.toURI(), ExperimentSearchResult.class)
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
        final AssayResult assayResult = (AssayResult) getForObject(url.toURI(), AssayResult.class)
        return assayResult

    }

    public ProjectResult findProjectsByCID(Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(cid.toString())).
                    append(RestApiConstants.PROJECTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ProjectResult projectResult = (ProjectResult) getForObject(url.toURI(), ProjectResult.class)
        return projectResult

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
        final List<Compound> compounds = this.getForObject(url.toURI(), Compound[].class) as List<Compound>
        CompoundResult compoundResult = new CompoundResult(compounds: compounds)
        return compoundResult
    }

    //===================== Concurrency code ==================
    protected Compound handleCompoundByIdFutures(final List<FutureTask<Object>> results) {
        try {
            //assert that there are 2 objects in the list
            assert results.size() == 2

            //the first task in the queue is the annotations
            final FutureTask<CompoundAnnotations> compoundAnnotationsTask = (FutureTask<CompoundAnnotations>) results.get(0)
            final CompoundAnnotations compoundAnnotations = compoundAnnotationsTask.get()
            //second task is the compound search
            final FutureTask<Compound> compoundTask = (FutureTask<Compound>) results.get(1)
            Compound compound = compoundTask.get()
            if (compound) {
                if (compoundAnnotations) {
                    compound.setCompoundAnnotations(compoundAnnotations)
                }
            }
            return compound
        }
        catch (Exception e) {
            log.error("Futures threw an Exception", e)
            throw new RestApiException(e)
        }

    }
    /**
     * Handle the tasks after execution
     * @param results
     * @return
     */
    protected CompoundResult handleStructureSearchFutures(final List<FutureTask<Object>> results, final Integer nhits = -1) {
        try {
            //assert that there are 2 objects in the list
            // assert results.size() == 2
            int numHits = nhits
            FutureTask<CompoundResult> structureResultsTask
            if (nhits <= 0) {
                //the first task in the queue is the number of hits
                final FutureTask<Long> numberHitsFutureTask = (FutureTask<Long>) results.get(0)
                //we could check if its done, but we can assume it is, otherwise invokeAll would not complete
                numHits = numberHitsFutureTask.get().intValue()
                structureResultsTask = (FutureTask<CompoundResult>) results.get(1)
            } else {
                //second task is the structure search
                structureResultsTask = (FutureTask<CompoundResult>) results.get(0)
            }

            final CompoundResult compoundResult = structureResultsTask.get()
            if (numHits <= 0) {
                numHits = compoundResult.compounds?.size() ?: 0
            }

            final MetaData metaData = new MetaData()
            metaData.nhit = numHits
            compoundResult.setMetaData(metaData)
            return compoundResult;
        }
        catch (Exception e) {
            log.error("Futures threw an Exception", e)
            throw new RestApiException(e)
        }
    }

    def findAnnotations = {Long cid ->
        final String resource = getResource(cid.toString() + RestApiConstants.ANNOTATIONS)
        final URL url = new URL(resource)
        final CompoundAnnotations annotations = (CompoundAnnotations) getForObject(url.toURI(), CompoundAnnotations.class)
        return annotations;
    }
    /**
     *
     * @param compound
     * @return {@link bard.core.rest.spring.compounds.Compound}
     */
    def findCompoundById = { Long cid ->
        //lets also get the annotations and the sids
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: cid, expand: "true"]

        final List<Compound> compounds = getForObject(url, List.class, map) as List<Compound>
        if (compounds) {
            return compounds.get(0)
        }
        return null;
    }
    /**
     * Return the number of records that match this query
     * @param params
     * @param withCount
     * @return
     */

    def getStructureCount = {StructureSearchParams params ->
        String resource = buildStructureSearchURL(params, true)
        return this.getResourceCount(resource)
    }
    def doStructureSearch = {StructureSearchParams structureSearchParams, Map<String, Long> etags ->
        final StructureSearchParams clonedParams = new StructureSearchParams(structureSearchParams)

        if (!clonedParams.getSkip()) {
            clonedParams.setSkip(0)
        }
        if (!clonedParams.getTop()) {
            clonedParams.setTop(10)
        }
        final long skip = clonedParams.getSkip()
        HttpHeaders requestHeaders = new HttpHeaders();
        addETagsToHTTPHeader(requestHeaders, etags)
        HttpEntity<List> entity = new HttpEntity<List>(requestHeaders);


        final String structureSearchURL = buildStructureSearchURL(clonedParams, false)
        final URL url = new URL(structureSearchURL)
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html

        final HttpEntity<List> exchange = getExchange(url.toURI(), entity, List.class) as HttpEntity<List>
        List<Compound> compounds = exchange.getBody()
        final HttpHeaders headers = exchange.getHeaders()
        extractETagsFromResponseHeader(headers, skip, etags)
        CompoundResult compoundSearchResult = new CompoundResult()
        compoundSearchResult.setCompounds(compounds)
        compoundSearchResult.setEtags(etags)
        return compoundSearchResult
    }


}
