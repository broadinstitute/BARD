package querycart

import bard.core.adapter.ProjectAdapter
import bardqueryapi.IQueryService

class CartProjectService {

    IQueryService queryService

    CartProject createCartProjectFromPID(Long pid) {
        CartProject cartProject;

        List<ProjectAdapter> projectAdapters = queryService.findProjectsByPIDs([pid]).projectAdapters
        assert projectAdapters.size() <= 1, "ProjectAdapter must be unique given a PID"
        ProjectAdapter projectAdapter = projectAdapters ? projectAdapters.first() : null
        if (projectAdapter) {
            cartProject = new CartProject(projectAdapter.name, pid, projectAdapter.id)
        }

        return cartProject
    }
}
