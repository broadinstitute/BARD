package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.interfaces.AssayValues;
import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.ProjectService;
import bard.core.interfaces.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class RESTProjectService extends RESTAbstractEntityService<Project>
        implements ProjectService, AssayValues {

    protected RESTProjectService
            (final RESTEntityServiceManager srvman, final String baseURL) {
        super(srvman, baseURL);
    }

    public Class<Project> getEntityClass() {
        return Project.class;
    }

    public String getResourceContext() {
        return PROJECTS_RESOURCE;
    }

    protected Project getEntity(Project project, JsonNode node) {
        if (project == null) {
            project = new Project();
        }
        addProbes(project, node);
        // identical to Assay... sigh
        project.setId(node.get(PROJECT_ID).asLong());
        project.setName(node.get(NAME).asText());
        project.setDescription(node.get(DESCRIPTION).asText());

        DataSource ds = getDataSource();
        /*
         * retrieval of assays and experiments for a project is
         * no longer peformed here. instead, use the iterator
         */

        JsonNode n = node.get(EXPERIMENT_COUNT);
        if (isNotNull(n)) {
            project.addValue(new IntValue
                    (ds, Project.NumberOfExperimentsValue, n.asInt()));
        }

        project = addAnnotations(project, node);

        return project;
    }

    protected Project getEntitySearch(Project project, JsonNode node) {
        if (project == null) {
            project = new Project();
        }

        final JsonNode projectId = node.get(PROJ_ID);
        if (isNotNull(projectId)) {
            project.setId(projectId.asLong());
        }

        final JsonNode name = node.get(NAME);
        if (isNotNull(name)) {
            project.setName(name.asText());
        }
        final JsonNode description = node.get(DESCRIPTION);
        if (isNotNull(description)) {
            project.setDescription(description.asText());
        }

        DataSource ds = getDataSource();
        JsonNode n = node.get(HIGHLIGHT);
        if (isNotNull(n)) {
            project.addValue(new StringValue
                    (ds, Entity.SearchHighlightValue, n.asText()));
        }

        n = node.get(NUM_EXPT);
        if (isNotNull(n)) {
            project.addValue(new IntValue
                    (ds, Project.NumberOfExperimentsValue, n.asInt()));
        }

        return project;
    }

    void addProbes(Project project, JsonNode node) {
        final ArrayNode probeNodes = (ArrayNode) node.get(PROBES);
        if (isNotNull(probeNodes)) {
            for (int i = 0; i < probeNodes.size(); ++i) {
                JsonNode n = probeNodes.get(i);
                String cid = n.get(CID).asText();
                String probeId = n.get(PROBE_ID).asText();
                String url = n.get(URL_STRING).asText();
                String smiles = n.get(SMILES).asText();

                Probe probe = new Probe(cid, probeId, url, smiles);
                project.addProbe(probe);
            }
        }
    }

    Project addAnnotations(Project project, JsonNode node) {
        // pull in assay annotations - process different source with different DataSource's
        DataSource capds = getDataSource(EntityNamedSources.CAPAnnotationSource);
        if (node.has(AK_DICT_LABEL)) {
            ArrayNode keys = (ArrayNode) node.get(AK_DICT_LABEL);
            ArrayNode vals = (ArrayNode) node.get(AV_DICT_LABEL);
            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i).asText();
                String val = vals.get(i).asText();
                project.addValue(new StringValue(capds, key, val));
            }
        }

        project = setAnnotations(project, node,
                getDataSource(EntityNamedSources.GOBPAnnotationSource),
                GOBP_ID, GOBP_TERM);
        project = setAnnotations(project, node,
                getDataSource(EntityNamedSources.GOMFAnnotationSource),
                GOMF_ID, GOMF_TERM);
        project = setAnnotations(project, node,
                getDataSource(EntityNamedSources.GOCCAnnotationSource),
                GOCC_ID, GOCC_TERM);

        project = setAnnotations(project, node,
                getDataSource(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource),
                null, KEGG_DISEASE_CAT);
        project = setAnnotations(project, node,
                getDataSource(EntityNamedSources.KEGGDiseaseNameAnnotationSource),
                null, KEGG_DISEASE_NAMES);

        return project;
    }

    Project setAnnotations(Project a, JsonNode node, DataSource ds, String keyField, String valueField) {
        if (node.has(keyField) && !node.get(keyField).isNull()) {
            ArrayNode keys = (ArrayNode) node.get(keyField);
            ArrayNode vals = (ArrayNode) node.get(valueField);
            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i).asText();
                String val = vals.get(i).asText();
                a.addValue(new StringValue(ds, key, val));
            }
        }
        return a;
    }

    @Override
    public <T extends Entity> SearchResult<T> searchResult
            (Project project, Class<T> clazz) {
        RESTAbstractEntityService<T> service =
                (RESTAbstractEntityService) getServiceManager().getService(clazz);

        if (clazz.equals(Assay.class)) {
            return service.getSearchResult
                    (getResource(project.getId() + ASSAYS_RESOURCE), null);
        } else if (clazz.equals(Experiment.class)) {
            return service.getSearchResult
                    (getResource(project.getId() + EXPERIMENTS_RESOURCE), null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException
                    (message);
        }
    }
}
