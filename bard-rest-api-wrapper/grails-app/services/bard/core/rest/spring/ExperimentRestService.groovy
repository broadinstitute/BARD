package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.experiment.*
import bard.core.SearchParams
import bard.core.util.FilterTypes

class ExperimentRestService extends AbstractRestService {
    def transactional=false
    public String getResourceContext() {
        return RestApiConstants.EXPERIMENTS_RESOURCE;
    }

    public ExperimentData activitiesByEIDs(final List<Long> eids, final SearchParams searchParams) {
        if (eids) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("eids", eids.join(","));
            final String urlString = buildURLToExperimentData(searchParams)
            final URL url = new URL(urlString)
            final List<Activity> activities = this.postForObject(url.toURI(), Activity[].class, map) as List<Activity>;
            ExperimentData experimentData = new ExperimentData()
            experimentData.setActivities(activities)
            return experimentData
        }
        return null

    }

    public ExperimentData activitiesByADIDs(final List<Long> adids, final SearchParams searchParams) {
        if (adids) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("aids", adids.join(","));
            final String urlString = buildURLToExperimentData(searchParams)
            final URL url = new URL(urlString)
            final List<Activity> activities = this.postForObject(url.toURI(), Activity[].class, map) as List<Activity>;
            ExperimentData experimentData = new ExperimentData()
            experimentData.setActivities(activities)
            return experimentData
        }
        return null
    }
    public ExperimentData activitiesBySIDs(final List<Long> sids, final SearchParams searchParams) {
        if (sids) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("sids", sids.join(","));
            final String urlString = buildURLToExperimentData(searchParams)
            final URL url = new URL(urlString)
            final List<Activity> activities = this.postForObject(url.toURI(), Activity[].class, map) as List<Activity>;
            ExperimentData experimentData = new ExperimentData()
            experimentData.setActivities(activities)
            return experimentData
        }
        return null
    }

    public ExperimentData activitiesByCIDs(final List<Long> cids, final SearchParams searchParams) {
        if (cids) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("cids", cids.join(","));
            final String urlString = buildURLToExperimentData(searchParams)
            final URL url = new URL(urlString)
            final List<Activity> activities = this.postForObject(url.toURI(), Activity[].class, map) as List<Activity>;
            ExperimentData experimentData = new ExperimentData()
            experimentData.setActivities(activities)
            return experimentData
        }
        return null
    }

    String buildURLToExperimentData(final SearchParams searchParams) {
        final StringBuilder resource =
            new StringBuilder(this.externalUrlDTO.baseUrl).append(RestApiConstants.EXPTDATA_RESOURCE)

        if (searchParams.getTop()) {
            resource.append(RestApiConstants.QUESTION_MARK)
            resource.append(RestApiConstants.SKIP).
                    append(searchParams.getSkip()).
                    append(RestApiConstants.TOP).
                    append(searchParams.getTop())
        }
        return resource.toString()

    }
    /**
     *
     * @param eid
     * @return {@link bard.core.rest.spring.experiment.ExperimentSearch}
     */
    public ExperimentShow getExperimentById(Long eid) {
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: eid, expand: "true"]
        final ExperimentShow experimentShow = (ExperimentShow)this.getForObject(url, ExperimentShow.class, map)
        return experimentShow;
    }
    /**
     *
     * @param list of pids
     * @return {@link ExperimentSearchResult}
     */
    public ExperimentSearchResult searchExperimentsByIds(final List<Long> eids) {
        if (eids) {
            final Map<String, Long> etags = [:]
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("ids", eids.join(","));

            HttpHeaders headers = new HttpHeaders();
            this.addETagsToHTTPHeader(headers, etags)
            HttpEntity<List> entity = new HttpEntity<List>(map, headers);
            final String url = this.buildURLToPostIds()

            final HttpEntity<List> exchange = postExchange(url,entity,ExperimentSearch[].class) as HttpEntity<List>
            final List<ExperimentSearch> experiments = exchange.getBody()
            headers = exchange.getHeaders()
            this.extractETagsFromResponseHeader(headers, 0, etags)
            int nhits = experiments.size();

            final ExperimentSearchResult experimentResult = new ExperimentSearchResult()
            experimentResult.setExperiments(experiments)
            experimentResult.setEtags(etags)
            final MetaData metaData = new MetaData()
            metaData.nhit = nhits
            experimentResult.setMetaData(metaData)
            return experimentResult
        }
        return null

    }


    public ExperimentData activities(Long experimentId) {
        return activities(experimentId, null)
    }

    //TODO: Probably make two calls here, first to get the count and second to use it for parallel processing
    public ExperimentData activities(final Long experimentId, final String etag) {
        // unbounded fetching
        Integer top = multiplier * multiplier;
        int ratio = multiplier;
        Integer skip = 0;
        final List<Activity> activities = []
        ExperimentData experimentData = new ExperimentData()
        while (true) {
            final String resource = buildExperimentQuery(experimentId, etag, top, skip, [FilterTypes.TESTED]);
            final URL url = new URL(resource)
            int currentSize
            List<Activity> currentActivities
            if (etag) {
                currentActivities = (getForObject(url.toURI(), Activity[].class)) as List<Activity>

            } else {
                final ExperimentData currentExperimentData = (ExperimentData)this.getForObject(url.toURI(), ExperimentData.class)
                currentActivities = currentExperimentData.activities
            }
            if (currentActivities) {
                currentSize = currentActivities.size()
                activities.addAll(currentActivities);
            } else {
                currentSize = 0
            }
            if (currentSize < top || activities.size() > RestApiConstants.MAXIMUM_NUMBER_OF_EXPERIMENTS) {
                break; // we're done
            }
            skip += currentSize;
            ratio *= multiplier;
            top = findNextTopValue(skip, ratio);

        }
        experimentData.setActivities(activities)
        return experimentData;
    }
    /**
     *
     * @param experimentId
     * @param etag
     * @param top
     * @param skip
     * @return ExperimentData
     */
    public ExperimentData activities(Long experimentId, String etag, Integer top, Integer skip, List<FilterTypes> filterType) {
        final String resource = buildExperimentQuery(experimentId, etag, top, skip, filterType)
        final URL url = new URL(resource)
        ExperimentData experimentData

        if (etag) {
            experimentData = new ExperimentData()
            final List<Activity> activities = (this.getForObject(url.toURI(), Activity[].class)) as List<Activity>
            experimentData.setActivities(activities)
        } else {
            experimentData = (ExperimentData)this.getForObject(url.toURI(), ExperimentData.class)
        }
        return experimentData
    }
    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        final String resourceName = getResourceContext()
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.SEARCH).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                append(RestApiConstants.QUESTION_MARK).
                toString();
    }

    @Override
    public String getResource() {
        final String resourceName = getResourceContext()
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public CompoundResult findCompoundsByExperimentId(final Long eid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(eid.toString())).
                    append(RestApiConstants.COMPOUNDS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        CompoundResult compoundResult = (CompoundResult)this.getForObject(url.toURI(), CompoundResult.class)
        return compoundResult
    }

    public ProjectResult findProjectsByExperimentId(final Long eid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(eid.toString())).
                    append(RestApiConstants.PROJECTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final ProjectResult projectResult = (ProjectResult)this.getForObject(url.toURI(), ProjectResult.class)
        return projectResult
    }
    //TODO: Right now this returns the default number of compounds, which is 500
    public List<Long> compoundsForExperiment(Long experimentId) {
        String resource = this.getResource(experimentId.toString()) + RestApiConstants.COMPOUNDS_RESOURCE;
        final URL url = new URL(resource)
        Map<String, Object> response = (Map)this.getForObject(url.toURI(), Map.class)
        List<String> compoundURLs = (List<String>) response.get("collection")
        if (compoundURLs) {
            return compoundURLs.collect {String s -> new Long(s.substring(s.lastIndexOf("/") + 1).trim())}
        }
        return []
    }

}
