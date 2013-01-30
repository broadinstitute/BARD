package bard.db.project

import bard.db.experiment.Experiment
import grails.converters.JSON
import bard.db.registration.Assay
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ProjectController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ProjectExperimentRenderService projectExperimentRenderService
    ProjectService projectService

    def show() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        [instance: projectInstance, pexperiment:projectExperimentRenderService.contructGraph(projectInstance)]
    }

    def edit() {
        def projectInstance = Project.get(params.id)

        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            return
        }
        else
            flash.message = null

        [instance: projectInstance, pexperiment:projectExperimentRenderService.contructGraph(projectInstance)]
    }

    def removeExperimentFromProject(Long experimentId, Long projectId) {
        def experiment = Experiment.findById(experimentId)
        def project = Project.findById(projectId)
        try{
            projectService.removeExperimentFromProject(experiment, project)
            project = Project.findById(projectId)
        // TODO: render template not working, as we may use different package to render graph, we defer making template working later
           render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (UserFixableException e) {
            render 'serviceError:'+e.message
        }
    }

    def removeEdgeFromProject(Long fromExperimentId, Long toExperimentId, Long projectId) {
        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)
        def project = Project.findById(projectId)
        try {
            projectService.removeEdgeFromProject(fromExperiment, toExperiment, project)
            project = Project.findById(projectId)
            // TODO: render template not working, as we may use different package to render graph, we defer making template working later
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        } catch (UserFixableException e) {
            render 'serviceError:'+e.message
        }
    }

    def linkExperiment (Long fromExperimentId, Long toExperimentId, Long projectId){
        def project = Project.findById(projectId)
        def fromExperiment = Experiment.findById(fromExperimentId)
        def toExperiment = Experiment.findById(toExperimentId)
        try{
            projectService.linkExperiment(fromExperiment, toExperiment, project)
            project = Project.findById(projectId)
            //render "Link between experiment is added, reload the page to show changes"
            // TODO: render template not working, as we may use different package to render graph, we defer making template working later
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        }catch(UserFixableException e) {
            render 'serviceError:'+e.message
        }
    }

    // Current the client send a list of displaynames of experiments.
    def associateExperimentsToProject() {
        // get all values regardless none, single, or multiple, ajax seemed serialized array and passed [] at the end of the param name.
        def param1 = request.getParameterValues('selectedExperiments[]')
        def projectId = params['projectId']
        def project = Project.findById(projectId)
        // get rid of duplicated selection if there is any
        Set<String> selectedExperiments = new HashSet<String>()

        param1.each{
            selectedExperiments.add(it)
        }

        try {
        selectedExperiments.each{ String experimentDisplayName ->
            def experimentId = experimentDisplayName.split("-")[0]
            def experiment = Experiment.findById(experimentId)
            projectService.addExperimentToProject(experiment, project)
            // TODO: render template not working, as we may use different package to render graph, we defer making template working later
            render(template: "showstep", model: [experiments: project.projectExperiments, pegraph: projectExperimentRenderService.contructGraph(project), instanceId: project.id])
        }
        }catch (UserFixableException e){
            render 'serviceError:'+e.message
        }
     }

    def ajaxFindAvailableExperimentByName(String experimentName, Long projectId){
        List<Experiment> experiments = Experiment.findAllByExperimentNameIlike("%${experimentName}%")
        Project project = Project.findById(projectId)
        Set<Experiment> exps = []
        experiments.each{Experiment experiment ->
            if (!projectService.isExperimentAssociatedWithProject(experiment, project))
                exps.add(experiment)
        }
        render exps.collect {it.displayName} as JSON
    }

    def ajaxFindAvailableExperimentByAssayId(Long assayId, Long projectId){
        Assay assay = Assay.findById(assayId)
        Project project = Project.findById(projectId)
        List<Experiment> experiments = Experiment.findAllByAssay(assay)
        Set<Experiment> exps = []
        experiments.each{Experiment experiment ->
            if (!projectService.isExperimentAssociatedWithProject(experiment, project))
                exps.add(experiment)
        }
        render exps.collect {it.displayName} as JSON
    }

    def ajaxFindAvailableExperimentById(Long experimentId, Long projectId){
        Project project = Project.findById(projectId)
        Experiment experiment = Experiment.findById(experimentId)
        Set<Experiment> exps = []
        if (!projectService.isExperimentAssociatedWithProject(experiment, project))
            exps.add(experiment)
        render exps.collect {it.displayName} as JSON
    }
}
