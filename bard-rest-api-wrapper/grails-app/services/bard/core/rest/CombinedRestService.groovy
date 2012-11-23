package bard.core.rest

import bard.core.interfaces.EntityService
import bard.core.interfaces.SearchResult
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import bard.core.*

class CombinedRestService {
    RESTAssayService restAssayService
    RESTProjectService restProjectService
    RESTExperimentService restExperimentService
    RESTCompoundService restCompoundService
    RESTSubstanceService restSubstanceService


    public boolean isNotNull(JsonNode jsonNode) {
        return jsonNode != null && !jsonNode.isNull();
    }
    public Collection<Assay> getTestedAssays(Compound compound,
                                             boolean activeOnly) {
        final String url = restCompoundService.buildQueryForTestedAssays(compound, activeOnly);
        final JsonNode rootNode = restCompoundService.executeGetRequest(url);
        return extractedTestAssays(rootNode);
    }
    protected List<Assay> extractedTestAssays(final JsonNode rootNode) {
        final List<Assay> assays = new ArrayList<Assay>();
        if (isNotNull(rootNode)) {
            final JsonNode node = rootNode.get(EntityService.COLLECTION);
            if (isNotNull(node) && node.isArray()) {
                ArrayNode array = (ArrayNode) node;
                assays.addAll(restCompoundService.jsonArrayNodeToAssays(array, restAssayService));
            }
        }

        return assays;
    }

    public SearchResult<Compound> compounds(Experiment expr) {
        String resource = this.restExperimentService.getResource(expr.getId()) + EntityService.COMPOUNDS_RESOURCE;
        return this.restCompoundService.getSearchResult(resource, null);
    }

    public <T extends Entity> SearchResult<T> searchResultBySubstance(Substance entity, Class<T> clazz) {
        return null;
    }

    public <T extends Entity> SearchResult<T> searchResultByAssay(Assay assay, Class<T> clazz) {
        if (clazz.equals(Project.class)) {
            final String resource = this.restAssayService.getResource(assay.getId() + EntityService.PROJECTS_RESOURCE)
            return (SearchResult<T>) this.restProjectService.getSearchResult(resource, null);
        } else if (clazz.equals(Experiment.class)) {
            final String resource = this.restAssayService.getResource(assay.getId() + EntityService.EXPERIMENTS_RESOURCE)
            return (SearchResult<T>) restExperimentService.getSearchResult(resource, null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public <T extends Entity> SearchResult<T> searchResultByCompound(Compound compound, Class<T> clazz) {

        if (clazz.equals(Project.class)) {
            final String resource = this.restCompoundService.getResource(compound.getId() + EntityService.PROJECTS_RESOURCE)
            return (SearchResult<T>) this.restProjectService.getSearchResult(resource, null);
        } else if (clazz.equals(Assay.class)) {
            final String resource = this.restCompoundService.getResource(compound.getId() + EntityService.ASSAYS_RESOURCE)
            return (SearchResult<T>) this.restAssayService.getSearchResult(resource, null);
        } else if (clazz.equals(Experiment.class)) {
            final String resource = this.restCompoundService.getResource(compound.getId() + EntityService.EXPERIMENTS_RESOURCE)
            return (SearchResult<T>) this.restExperimentService.getSearchResult(resource, null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException
            (message);
        }
    }

    public <T extends Entity> SearchResult<T> searchResultByExperiment(Experiment expr, Class<T> clazz) {

        if (clazz.equals(Project.class)) {
            final String resource = this.restExperimentService.getResource(expr.getId() + EntityService.PROJECTS_RESOURCE)
            return (SearchResult<T>) restProjectService.getSearchResult(resource, null);
        } else if (clazz.equals(Compound.class)) {
            final String resource = this.restExperimentService.getResource(expr.getId() + EntityService.COMPOUNDS_RESOURCE)
            return (SearchResult<T>) this.restCompoundService.getSearchResult(resource, null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException
            (message);
        }
    }

    public <T extends Entity> SearchResult<T> searchResultByProject(Project project, Class<T> clazz) {
        if (clazz.equals(Assay.class)) {
            final String resource = restProjectService.getResource(project.getId() + EntityService.ASSAYS_RESOURCE)
            return (SearchResult<T>) this.restAssayService.getSearchResult(resource, null);
        } else if (clazz.equals(Experiment.class)) {
            final String resource = restProjectService.getResource(project.getId() + EntityService.EXPERIMENTS_RESOURCE)
            return (SearchResult<T>) restExperimentService.getSearchResult(resource, null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException
            (message);
        }
    }

}
