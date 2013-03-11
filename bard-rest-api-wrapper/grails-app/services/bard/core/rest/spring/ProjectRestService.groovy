package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.project.ProjectStep
import bard.core.rest.spring.util.MetaData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class ProjectRestService extends RestService {
    def transactional=false

    public String getResourceContext() {
        return RestApiConstants.PROJECTS_RESOURCE
    }

    public BardAnnotation findAnnotations(final Long pid) {
        final String resource = getResource(pid.toString() + RestApiConstants.ANNOTATIONS)
        final URL url = new URL(resource)

        final BardAnnotation annotations = (BardAnnotation) getForObject(url.toURI(), BardAnnotation.class)
        return annotations;
    }

    /**
     *
     * @param pid
     * @return {@link bard.core.rest.spring.project.ProjectExpanded}
     */
    public ProjectExpanded getProjectById(final Long pid) {
        final String url = this.buildEntityURL() + "?expand={expand}"
        final Map map = [id: pid, expand: "true"]
        final ProjectExpanded project = this.getForObject(url, ProjectExpanded.class, map) as ProjectExpanded
        return project

    }

    /**
     *
     * @param searchParams
     * @param map of etags
     * @return {@link bard.core.rest.spring.project.ProjectResult}
     */
    public List<Project> searchProjectsByCapIds(final SearchParams searchParams, Map<String, Long> etags) {

        if (etags) {
            final String etag = firstETagFromMap(etags)
            final String urlString = buildQueryForETag(searchParams, etag)
            final URL url = new URL(urlString)
            final List<Project> projects = getForObject(url.toURI(), List.class) as List<Project>
            return projects
        }
        return []
    }
    /**
     *
     * @param list of cap project ids
     * @param searchParams
     * @return {@link bard.core.rest.spring.project.ProjectResult}
     */
    public ProjectResult searchProjectsByCapIds(final List<Long> capIds, final SearchParams searchParams) {
        if (capIds) {
            final Map<String, Long> etags = [:]
            final long skip = searchParams.getSkip()
            HttpHeaders requestHeaders = new HttpHeaders();
            HttpEntity<List> entity = new HttpEntity<List>(requestHeaders);


            final String urlString = buildSearchByCapIdURLs(capIds, searchParams, "capProjectId:")
            final URL url = new URL(urlString)
            final HttpEntity<ProjectResult> exchange = getExchange(url.toURI(), entity, ProjectResult.class) as HttpEntity<ProjectResult>
            final ProjectResult projectSearchResult = exchange.getBody()

            final HttpHeaders headers = exchange.getHeaders()
            extractETagsFromResponseHeader(headers, skip, etags)
            projectSearchResult.setEtags(etags)
            return projectSearchResult
        }

        return null

    }
    /**
     *
     * @param list of pids
     * @return {@link bard.core.rest.spring.project.ProjectResult}
     */
    public ProjectResult searchProjectsByIds(final List<Long> pids) {
        if (pids) {
            final Map<String, Long> etags = [:]
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("ids", pids.join(","));

            HttpHeaders headers = new HttpHeaders();
            this.addETagsToHTTPHeader(headers, etags)
            HttpEntity<List> entity = new HttpEntity<List>(map, headers);
            final String url = this.buildURLToPostIds()
            final HttpEntity<List> exchange = postExchange(url, entity, List.class) as HttpEntity<List>
            final List<Project> projects = exchange.getBody()
            headers = exchange.getHeaders()
            this.extractETagsFromResponseHeader(headers, 0, etags)
            int nhits = projects.size();
            final ProjectResult projectSearchResult = new ProjectResult()
            projectSearchResult.setProjects(projects)
            projectSearchResult.setEtags(etags)
            final MetaData metaData = new MetaData()
            metaData.nhit = nhits
            projectSearchResult.setMetaData(metaData)
            return projectSearchResult;
        }
        return null

    }
    /**
     *
     * @param searchParams
     * @return {@link ProjectResult}
     */
    public ProjectResult findProjectsByFreeTextSearch(SearchParams searchParams) {
        final String urlString = this.buildSearchURL(searchParams)
        final URL url = new URL(urlString)
        final ProjectResult projectSearchResult = (ProjectResult) this.getForObject(url.toURI(), ProjectResult.class)
        return projectSearchResult
    }
    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = RestApiConstants.PROJECTS_RESOURCE
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
        String resourceName = RestApiConstants.PROJECTS_RESOURCE
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }

    public List<Assay> findAssaysByProjectId(Long pid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(pid.toString())).
                    append(RestApiConstants.ASSAYS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        List<Assay> assays = getForObject(url.toURI(), Assay[].class) as List<Assay>
        return assays;
    }

    public List<ExperimentSearch> findExperimentsByProjectId(Long pid) {
        final StringBuilder resource =
            new StringBuilder(
                    this.getResource(pid.toString())).
                    append(RestApiConstants.EXPERIMENTS_RESOURCE).
                    append(RestApiConstants.QUESTION_MARK).
                    append(RestApiConstants.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        List<ExperimentSearch> experiments = this.getForObject(url.toURI(), ExperimentSearch[].class) as List<ExperimentSearch>
        return experiments

    }

    public List<ProjectStep> findProjectSteps(final Long pid) {
        final String resource = getResource(pid.toString() + RestApiConstants.STEPS)
        final URL url = new URL(resource)

        final List<ProjectStep> projectSteps = getForObject(url.toURI(), ProjectStep[].class) as List<ProjectStep>
        return projectSteps;
    }
}
