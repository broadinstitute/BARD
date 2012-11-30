package bard.core.rest.spring

import bard.core.interfaces.EntityService
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.experiment.ExperimentShow

class ExperimentRestService extends AbstractRestService {

    public String getResourceContext() {
        return EntityService.EXPERIMENTS_RESOURCE;
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
     * @return {@link bard.core.rest.spring.project.ExpandedProjectResult}
     */
    public ExperimentSearchResult searchExperimentsByIds(final List<Long> eids) {
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
        this.extractETagFromResponseHeader(headers, 0, etags)
        int nhits = experiments?.size();

        final ExperimentSearchResult experimentResult = new ExperimentSearchResult()
        experimentResult.setExperiments(experiments)
        experimentResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        experimentResult.setMetaData(metaData)
        return experimentResult

    }

    public ExperimentData activities(ExperimentSearch experiment) {
        return activities(experiment.getId())
    }

    public ExperimentData activities(Long experimentId) {
        return activities(experimentId, null)
    }

    public ExperimentData activities(final Long experimentId, final String etag) {
        // unbounded fetching
        int top = multiplier * multiplier;
        int ratio = multiplier;
        long skip = 0;
        final List<Activity> activities = []
        ExperimentData experimentData = new ExperimentData()
        while (true) {
            final String resource = buildExperimentQuery(experimentId, etag, top, skip);
            final URL url = new URL(resource)
            int currentSize = 0
            if (etag) {
                List<Activity> currentActivities = (this.restTemplate.getForObject(url.toURI(), Activity[].class)) as List<Activity>
                currentSize = currentActivities.size()
                activities.addAll(currentActivities);

            } else {
                final ExperimentData currentExperimentData = this.restTemplate.getForObject(url.toURI(), ExperimentData.class)
                List<Activity> currentActivities = currentExperimentData.activities
                currentSize = currentActivities.size()
                activities.addAll(currentActivities);

            }
            skip += currentSize;
            ratio *= multiplier;
            if (currentSize < top || activities.size() > EntityService.MAXIMUM_NUMBER_OF_EXPERIMENTS) {
                break; // we're done
            }
            top = findNextTopValue(skip, ratio);

        }
        experimentData.setActivities(activities)
        return experimentData;
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        final String resourceName = EntityService.EXPERIMENTS_RESOURCE
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
        final String resourceName = EntityService.EXPERIMENTS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(EntityService.FORWARD_SLASH).
                toString();
    }


}
