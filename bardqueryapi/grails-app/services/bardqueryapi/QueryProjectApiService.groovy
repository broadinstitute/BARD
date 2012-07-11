package bardqueryapi

class QueryProjectApiService {

    def grailsApplication
    QueryExecutorInternalService queryExecutorInternalService

    //We consider a project to be represented by a PubChem summary assay.
    // As the BAO develops and includes the specifications for project management,
    // the representation of a project will be updated.
    ///v1/projects - return a list of paths to all projects available.
    ///v1/projects/{id} - JSON representation of a project, identified by its AID
    ///v1/projects/{id}/assays - list of paths to assays associated with this project
    //v1/projects/{id}/probes - by default, a list of paths to compounds that have been designated as probes for this project
    //v1/projects{id}/targets - a list of paths to protein targets, only considering the targets
    // annotated directly to the project (ie summary assay) and the not those associated with the component assays
    def findProjects() {
        final String url = grailsApplication.config.ncgc.server.projects.root.url
        return queryExecutorInternalService.executeGetRequestJSON(url, null)
    }
    /**
     *
     * Find a single project
     *
     * @param projectId
     */
    def findProject(String projectUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + projectUrl
        return queryExecutorInternalService.executeGetRequestJSON(url, null)
    }
    /**
     * Find the protein targets by project
     * @param projectId
     */
    def findProteinTargetsByProject(String projectUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + projectUrl + "/targets"
        println url
        return queryExecutorInternalService.executeGetRequestJSON(url, null)
    }
    /**
     * Find probes by project
     *
     * @param projectUrl
     */
    def findProbesByProject(String projectUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + projectUrl + "/probes"
        println url
        return queryExecutorInternalService.executeGetRequestJSON(url, null)
    }
    /**
     *
     * @param projectId
     */
    def findAssaysByProject(String projectUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + projectUrl + "/assays"
        println url
        return queryExecutorInternalService.executeGetRequestJSON(url, null)
    }
}
