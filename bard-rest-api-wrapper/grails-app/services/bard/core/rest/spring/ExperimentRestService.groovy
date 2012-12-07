package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.experiment.*

class ExperimentRestService extends AbstractRestService {

    public String getResourceContext() {
        return RestApiConstants.EXPERIMENTS_RESOURCE;
    }
    /**
     *
     * @param eid
     * @return {@link bard.core.rest.spring.experiment.ExperimentSearch}
     */
    public ExperimentShow getExperimentById(Long eid) {
        final String url = buildEntityURL() + "?expand={expand}"
        final Map map = [id: eid, expand: "true"]
        final ExperimentShow experimentShow = this.restTemplate.getForObject(url, ExperimentShow.class, map)
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

            final HttpEntity<List> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, ExperimentSearch[].class);

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

//    public ExperimentData activities(ExperimentSearch experiment) {
//        return activities(experiment.getId())
//    }
    public ExperimentData activities(Long experimentId) {
        return activities(experimentId, null)
    }
//    public ExperimentData activities(Long experimentId, final Integer top, final Integer skip) {
//        return activities(experimentId, null,top,skip)
//    }

    public ExperimentData activities(final Long experimentId, final String etag) {
        // unbounded fetching
        Integer top = multiplier * multiplier;
        int ratio = multiplier;
        Integer skip = 0;
        final List<Activity> activities = []
        ExperimentData experimentData = new ExperimentData()
        while (true) {
            final String resource = buildExperimentQuery(experimentId, etag, top, skip);
            final URL url = new URL(resource)
            int currentSize
            List<Activity> currentActivities
            if (etag) {
                currentActivities = (this.restTemplate.getForObject(url.toURI(), Activity[].class)) as List<Activity>

            } else {
                final ExperimentData currentExperimentData = this.restTemplate.getForObject(url.toURI(), ExperimentData.class)
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
    public ExperimentData activities(final Long experimentId, final String etag, final Integer top, final Integer skip) {
        final String resource = buildExperimentQuery(experimentId, etag, top, skip)
        final URL url = new URL(resource)
        ExperimentData experimentData

        if (etag) {
          experimentData = new ExperimentData()
          final List<Activity> activities = (this.restTemplate.getForObject(url.toURI(), Activity[].class)) as List<Activity>
          experimentData.setActivities(activities)
        } else {
            experimentData = this.restTemplate.getForObject(url.toURI(), ExperimentData.class)
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
        final String resourceName = getResourceContext()
        return new StringBuilder(baseUrl).
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
        CompoundResult compoundResult = this.restTemplate.getForObject(url.toURI(), CompoundResult.class)
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
        final ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        return projectResult
    }
    //TODO: Right now this returns the default number of compounds, which is 500
    public List<Long> compoundsForExperiment(Long experimentId) {
        String resource = this.getResource(experimentId.toString()) + RestApiConstants.COMPOUNDS_RESOURCE;
        final URL url = new URL(resource)
        Map<String, Object> response = this.restTemplate.getForObject(url.toURI(), Map.class)
        List<String> compoundURLs = (List<String>) response.get("collection")
        if (compoundURLs) {
            return compoundURLs.collect {String s -> new Long(s.substring(s.lastIndexOf("/") + 1).trim())}
        }
        return []
    }

}
