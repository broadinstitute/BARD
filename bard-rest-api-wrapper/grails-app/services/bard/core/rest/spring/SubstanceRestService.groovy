package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.substances.Substance
import bard.core.rest.spring.substances.SubstanceResult
import bard.core.rest.spring.util.SubstanceSearchType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class SubstanceRestService extends AbstractRestService {

    public String getResourceContext() {
        return RestApiConstants.SUBSTANCES_RESOURCE
    }
    /**
     * See: https://github.com/ncatsdpiprobedev/bard/wiki/REST-Query-API#wiki-experiments
     * @param sids
     * @param bardExperimentIds
     * @return List
     */
    public List<Activity> findExperimentData(final List<Long> sids, final List<Long> bardExperimentIds) {
        final List<Activity> activities = []

        if (sids && bardExperimentIds) {
            final String url = buildExperimentQuery()
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("ids", sids.join(","));
            map.add("eids", bardExperimentIds.join(","));
            final List<Activity> activitiesFound = this.restTemplate.postForObject(url, map, Activity[].class) as List<Activity>;
            activities.addAll(activitiesFound)
        }
        return activities
    }

    String buildExperimentQuery() {
        final StringBuilder resource =
            new StringBuilder(this.baseUrl).append(RestApiConstants.EXPTDATA_RESOURCE)
        return resource.toString();
    }
    /**
     *
     * @param pid
     * @return {@link Substance}
     */
    public Substance getSubstanceById(final Long sid) {
        final String url = this.buildEntityURL() + "?expand={expand}"
        final Map map = [id: sid, expand: "true"]
        try {
            final Substance substance = this.restTemplate.getForObject(url, Substance.class, map)
            return substance
        } catch (Exception ee) {
            log.error(ee)
        }
        return null;
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = getResource()
        return new StringBuilder().
                append(resourceName).
                append(RestApiConstants.QUESTION_MARK).
                toString();
    }

    @Override
    public String getResource() {
        String resourceName = RestApiConstants.SUBSTANCES_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public List<Substance> findSubstancesByCid(final Long cid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(RestApiConstants.CID)).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(cid.toString()).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final List<Substance> substances = (List<Substance>) this.restTemplate.getForObject(url.toURI(), Substance[].class)
        return substances;
    }

    public ExperimentData findExperimentDataBySid(final Long sid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(sid.toString())).
                    append(RestApiConstants.EXPTDATA_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final ExperimentData experimentData = this.restTemplate.getForObject(url.toURI(), ExperimentData.class)
        return experimentData;
    }

    public ExperimentSearchResult findExperimentsBySid(final Long sid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(sid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ExperimentSearchResult experiments = this.restTemplate.getForObject(url.toURI(), ExperimentSearchResult.class)
        return experiments

    }
    /**
     * example urls:
     *
     //http://bard.nih.gov/api/latest/substances?skip=416100&top=50&expand=true&filter=MLSMR
     //http://bard.nih.gov/api/latest/substances?skip=416100&top=50&expand=true&filter=MLSMR[dep_regid]
     //http://bard.nih.gov/api/latest/substances?skip=416100&top=50&expand=true&filter=MLSMR[source_name]
     * Build a search url from the params
     * @param searchParams
     * @return a fully encoded search url
     */
    public String buildURLForSearch(final SubstanceSearchType substanceSearchType, final SearchParams searchParams) {
        final StringBuilder urlBuilder = new StringBuilder()
        urlBuilder.append(getSearchResource())
        if (searchParams) {
            if (searchParams.getSkip() || searchParams.getTop()) {
                urlBuilder.append(RestApiConstants.SKIP).
                        append(searchParams.getSkip()).
                        append(RestApiConstants.TOP).
                        append(searchParams.getTop())
                urlBuilder.append(RestApiConstants.AMPERSAND)
            }
        }
        urlBuilder.append(RestApiConstants.EXPAND_TRUE)
        switch (substanceSearchType) {
            case SubstanceSearchType.MLSMR:
            case SubstanceSearchType.MLSMR_DEP_REGID:
            case SubstanceSearchType.MLSMR_SOURCE_NAME:
                urlBuilder.append(RestApiConstants.AMPERSAND)
                urlBuilder.append(substanceSearchType.getFilter())
                break;
        }
        return urlBuilder.toString();

    }
    /**
     * @param substanceSearchType
     * @param searchParams
     * @return {@link SubstanceResult}
     */
    public SubstanceResult findSubstances(final SubstanceSearchType substanceSearchType, final SearchParams searchParams) {
        final String urlString = buildURLForSearch(substanceSearchType, searchParams)
        final URL url = new URL(urlString)
        final SubstanceResult substanceResult = this.restTemplate.getForObject(url.toURI(), SubstanceResult.class)
        return substanceResult
    }

}
