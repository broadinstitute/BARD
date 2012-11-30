package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.EntityService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import bard.core.rest.spring.util.MetaData
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ExpandedProjectResult

class ProjectRestService extends AbstractRestService {

    public String getResourceContext() {
        return EntityService.PROJECTS_RESOURCE
    }
    /**
     *
     * @param pid
     * @return {@link bard.core.rest.spring.project.Project}
     */
    public Project getProjectById(final Long pid) {
        final String url = this.buildEntityURL() + "?expand={expand}"
        final Map map = [id: pid, expand: "true"]
        try {
            final Project project = this.restTemplate.getForObject(url, Project.class, map)
            return project
        } catch (Exception ee) {
            log.error(ee)
        }
        return null;
    }

    /**
     *
     * @param list of pids
     * @return {@link bard.core.rest.spring.project.ExpandedProjectResult}
     */
    public ExpandedProjectResult searchProjectsByIds(final List<Long> pids) {
        final Map<String, Long> etags = [:]
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("ids", pids.join(","));

        HttpHeaders headers = new HttpHeaders();
        this.addETagsToHTTPHeader(headers, etags)
        HttpEntity<List> entity = new HttpEntity<List>(map, headers);
        final String url = this.buildURLToPostIds()
        final HttpEntity<List> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, List.class);
        final List<Project> projects = exchange.getBody()
        headers = exchange.getHeaders()
        this.extractETagFromResponseHeader(headers, 0, etags)
        int nhits = projects?.size();

        if (nhits == 0) {
            nhits = projects.size();
        }
        final ExpandedProjectResult projectSearchResult = new ExpandedProjectResult()
        projectSearchResult.setProjects(projects)
        projectSearchResult.setEtags(etags)
        final MetaData metaData = new MetaData()
        metaData.nhit = nhits
        projectSearchResult.setMetaData(metaData)
        return projectSearchResult;

    }
    /**
     *
     * @param searchParams
     * @return {@link ExpandedProjectResult}
     */
    public ExpandedProjectResult findProjectsByFreeTextSearch(SearchParams searchParams) {
        final String urlString = this.buildSearchURL(searchParams)
        //We are passing the URI because we have already encoded the string
        //just passing in the string would cause the URI to be encoded twice
        //see http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
        final URL url = new URL(urlString)
        final ExpandedProjectResult projectSearchResult = this.restTemplate.getForObject(url.toURI(), ExpandedProjectResult.class)
        return projectSearchResult
    }
    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        String resourceName = EntityService.PROJECTS_RESOURCE
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
        String resourceName = EntityService.PROJECTS_RESOURCE
        return new StringBuilder(baseUrl).
                append(resourceName).
                append(EntityService.FORWARD_SLASH).
                toString();
    }
}
