package bard.core.rest.spring

import bard.core.interfaces.EntityService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.ProjectResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestTemplate

class RestCombinedService {
    String baseUrl
    RestTemplate restTemplate
    ExperimentRestService experimentRestService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService

    final ObjectMapper mapper = new ObjectMapper()

    //TODO: Right now this returns the default number of compounds, which is 500
    public List<Long> compounds(Long experimentId) {
        String resource = this.experimentRestService.getResource(experimentId.toString()) + EntityService.COMPOUNDS_RESOURCE;
        final URL url = new URL(resource)
        Map<String, Object> response = this.restTemplate.getForObject(url.toURI(), Map.class)
        List<String> compoundURLs = response.get("collection")
        if (compoundURLs) {
            final List<Long> list = compoundURLs.collect {String s -> new Long(s.substring(s.lastIndexOf("/") + 1).trim())}
            return list
        }
        return []
    }

    public ProjectResult findProjectsByAssayId(final Long adid) {
        final StringBuilder resource =
            new StringBuilder(
                    assayRestService.getResource(adid.toString())).
                    append(EntityService.PROJECTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        projectResult

    }

    public List<ExperimentSearch> findExperimentsByAssayId(final Long adid) {
        final StringBuilder resource =
            new StringBuilder(
                    assayRestService.getResource(adid.toString())).
                    append(EntityService.EXPERIMENTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())

        List<ExperimentSearch> experiments = this.restTemplate.getForObject(url.toURI(), ExperimentSearch[].class)
        experiments

    }

    public ProjectResult findProjectsByCID(Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    compoundRestService.getResource(cid.toString())).
                    append(EntityService.PROJECTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        projectResult

    }

    public AssayResult findAssaysByCID(Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    compoundRestService.getResource(cid.toString())).
                    append(EntityService.ASSAYS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        AssayResult assayResult = this.restTemplate.getForObject(url.toURI(), AssayResult.class)
        assayResult

    }

    public ExperimentSearchResult findExperimentsByCID(final Long cid) {
        final StringBuilder resource =
            new StringBuilder(
                    compoundRestService.getResource(cid.toString())).
                    append(EntityService.EXPERIMENTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        ExperimentSearchResult experimentResult = this.restTemplate.getForObject(url.toURI(), ExperimentSearchResult.class)
        return experimentResult

    }

    public ProjectResult findProjectsByExperimentId(final Long eid) {
        final StringBuilder resource =
            new StringBuilder(
                    experimentRestService.getResource(eid.toString())).
                    append(EntityService.PROJECTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        final ProjectResult projectResult = this.restTemplate.getForObject(url.toURI(), ProjectResult.class)
        return projectResult
    }

    public CompoundResult findCompoundsByExperimentId(final Long eid) {
        final StringBuilder resource =
            new StringBuilder(
                    experimentRestService.getResource(eid.toString())).
                    append(EntityService.COMPOUNDS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        CompoundResult compoundResult = this.restTemplate.getForObject(url.toURI(), CompoundResult.class)
        return compoundResult
    }

    public List<Assay> findAssaysByProjectId(Long pid) {
        final StringBuilder resource =
            new StringBuilder(
                    projectRestService.getResource(pid.toString())).
                    append(EntityService.ASSAYS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        List<Assay> assays = this.restTemplate.getForObject(url.toURI(), Assay[].class)
        return assays;
    }

    public List<ExperimentSearch> findExperimentsByProjectId(Long pid) {
        final StringBuilder resource =
            new StringBuilder(
                    projectRestService.getResource(pid.toString())).
                    append(EntityService.EXPERIMENTS_RESOURCE).
                    append(EntityService.QUESTION_MARK).
                    append(EntityService.EXPAND_TRUE)
        final URL url = new URL(resource.toString())
        List<ExperimentSearch> experiments = this.restTemplate.getForObject(url.toURI(), ExperimentSearch[].class)
        return experiments

    }
}