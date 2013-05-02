package barddataqa

class ProjectAssaysCentersController {

    ProjectsAssaysCentersService projectsAssaysCentersService

    def index() {
        [rowList: projectsAssaysCentersService.findProjectsAssaysAndCenters(), headerList: ProjectsAssaysCentersService.headerArray]
    }
}
