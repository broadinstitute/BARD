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

class SubstanceRestService extends RestService {
    def transactional=false

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
            final String urlString = buildExperimentQuery()
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("sids", sids.join(","));
            map.add("eids", bardExperimentIds.join(","));
            final URL url = new URL(urlString)
            final List<Activity> activitiesFound = this.postForObject(url.toURI(), Activity[].class, map) as List<Activity>;
            activities.addAll(activitiesFound)
        }
        return activities
    }

    String buildExperimentQuery() {
        final StringBuilder resource =
            new StringBuilder(this.externalUrlDTO.baseUrl).append(RestApiConstants.EXPTDATA_RESOURCE)
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
        final Substance substance = (Substance) this.getForObject(url, Substance.class, map)
        return substance

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
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public List<Substance> findSubstancesByCidExpandedSearch(Long cid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(RestApiConstants.CID)).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(cid.toString())
        resource.append(RestApiConstants.QUESTION_MARK).
                append(RestApiConstants.EXPAND_TRUE)

        final URL url = new URL(resource.toString())
        final List<Substance> substances = getForObject(url.toURI(), Substance[].class) as List<Substance>
        return substances;
    }
    /**
     * Returns a lis of sids
     * @param cid
     * @return
     */
    public List<Long> findSubstancesByCid(Long cid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(RestApiConstants.CID)).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(cid.toString())


        final URL url = new URL(resource.toString())
        List<String> sidUrls = getForObject(url.toURI(), String[].class) as List<String>
        if (sidUrls) {
            return sidUrls.collect {String s -> new Long(s.substring(s.lastIndexOf("/") + 1).trim())}
        }
        return []
    }

    public ExperimentData findExperimentDataBySid(final Long sid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(sid.toString())).
                    append(RestApiConstants.EXPTDATA_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final ExperimentData experimentData = (ExperimentData) getForObject(url.toURI(), ExperimentData.class)
        return experimentData;
    }

    public ExperimentSearchResult findExperimentsBySid(final Long sid) {
        final StringBuilder resource =
            new StringBuilder(this.getResource(sid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ExperimentSearchResult experiments = (ExperimentSearchResult) getForObject(url.toURI(), ExperimentSearchResult.class)
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
        final SubstanceResult substanceResult = (SubstanceResult) getForObject(urlString, SubstanceResult.class,[:])
        return substanceResult
    }

}
