package dataexport.experiment

import bard.db.experiment.Project
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import dataexport.registration.BardHttpResponse
import javax.servlet.http.HttpServletResponse
import dataexport.registration.UpdateType
import dataexport.util.UtilityService

class ProjectExportService {
    LinkGenerator grailsLinkGenerator
    final String projectMediaType
    final String projectsMediaType
    UtilityService utilityService

    //This is instantiated from resources.groovy
    ProjectExportService(final MediaTypesDTO mediaTypesDTO) {
        this.projectMediaType = mediaTypesDTO.projectMediaType
        this.projectsMediaType = mediaTypesDTO.projectsMediaType
    }


    /**
     * Set the ReadyForExtraction value on the element to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(final Long id, final Long clientVersion, final String latestStatus) {
        final Project project = Project.findById(id)
        return utilityService.update(project,id,clientVersion,latestStatus,"Project")
    }
    /**
     * Generate a Project
     * @param markupBuilder
     * @param project
     */
    protected void generateProject(final MarkupBuilder markupBuilder, final Project project) {
        def attributes = [:]
        attributes.put('projectId', project.id)
        if (project.readyForExtraction) {
            attributes.put('readyForExtraction', project.readyForExtraction)
        }
        if (project.groupType) {
            attributes.put('groupType', project.groupType)
        }
        markupBuilder.project(
                attributes
        ) {
            if (project.projectName) {
                projectName(project.projectName)
            }
            if (project.description) {
                description(project.description)
            }
            final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: project.id]).toString()
            link(rel: 'edit', href: "${projectHref}", type: "${this.projectMediaType}")
            final String projectsHref = grailsLinkGenerator.link(mapping: 'projects', absolute: true).toString()
            link(rel: 'up', href: "${projectsHref}", type: "${this.projectsMediaType}")
        }
    }
    /**
     *  Generate a project from a given projectId
     * @param markupBuilder
     * @param projectId
     */
    public Long generateProject(final MarkupBuilder markupBuilder, final Long projectId) {
        final Project project = Project.get(projectId)
        if (!project) {
            throw new NotFoundException("Could not find Project with Id ${projectId}")
        }
        this.generateProject(markupBuilder, project)
        return project.version
    }
    /**
     * @param xml
     */
    public void generateProjects(def markupBuilder) {
        final List<Project> projects = Project.findAllByReadyForExtraction('Ready')
        final int numberOfProjects = projects.size()
        markupBuilder.projects(count: numberOfProjects) {
            for (Project project : projects) {
                generateProject(markupBuilder, project)
            }
        }
    }
}
