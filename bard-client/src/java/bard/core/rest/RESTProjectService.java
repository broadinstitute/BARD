package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.interfaces.AssayValues;
import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.ProjectService;
import bard.core.interfaces.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.StringUtils;


public class RESTProjectService extends RESTAbstractEntityService<Project>
        implements ProjectService, AssayValues {
    final DataSource CAP_ANNOTATIONS = getDataSource(EntityNamedSources.CAPAnnotationSource);

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
        addProjectIdNode(project, node);
        addProjectNameNode(project, node);
        addProjectDescriptionNode(project, node);
        addExperimentCountNode(project, node);
        addAllAnnotations(project, node);
        return project;
    }

    protected void addProjectDescriptionNode(final Project project, final JsonNode node) {
        final JsonNode descriptionNode = node.get(DESCRIPTION);
        if (isNotNull(descriptionNode)) {
            project.setDescription(descriptionNode.asText());
        }
    }


    protected void addProjectIdNode(final Project project, final JsonNode node) {
        final JsonNode idNode = node.get(PROJECT_ID);
        if (isNotNull(idNode)) {
            project.setId(idNode.asLong());
        } else {
            throw new IllegalArgumentException("Project JSON does not contain " + PROJECT_ID + " node");
        }
    }

    protected void addExperimentCountNode(final Project project, final JsonNode node) {
        DataSource ds = getDataSource();
        final JsonNode experimentCountNode = node.get(EXPERIMENT_COUNT);
        if (isNotNull(experimentCountNode)) {
            project.addValue(new IntValue
                    (ds, Project.NumberOfExperimentsValue, experimentCountNode.asInt()));
        }

    }

    protected void addProjIdNode(final Project project, final JsonNode node) {
        final JsonNode idNode = node.get(PROJ_ID);
        if (isNotNull(idNode)) {
            project.setId(idNode.asLong());
        } else {
            throw new IllegalArgumentException("Project JSON does not contain " + PROJ_ID + " node");
        }
    }

    protected void addProjectNameNode(final Project project, final JsonNode node) {
        final JsonNode nameNode = node.get(NAME);
        if (isNotNull(nameNode)) {
            project.setName(nameNode.asText());
        }
    }

    protected void addProjectHighlightNode(final Project project, final JsonNode node) {
        final DataSource ds = getDataSource();
        final JsonNode highlightNode = node.get(HIGHLIGHT);
        if (isNotNull(highlightNode)) {
            project.addValue(new StringValue
                    (ds, Entity.SearchHighlightValue, highlightNode.asText()));
        }
    }

    protected void addProjectNumExperimentsNode(final Project project, final JsonNode node) {
        final DataSource ds = getDataSource();
        final JsonNode numExperimentsNode = node.get(NUM_EXPT);
        if (isNotNull(numExperimentsNode)) {
            project.addValue(new IntValue
                    (ds, Project.NumberOfExperimentsValue, numExperimentsNode.asInt()));
        }
    }

    protected Project getEntitySearch(Project project, JsonNode node) {
        if (project == null) {
            project = new Project();
        }
        addProjIdNode(project, node);
        addProjectNameNode(project, node);
        addProjectDescriptionNode(project, node);
        addProjectHighlightNode(project, node);
        addProjectNumExperimentsNode(project, node);
        return project;
    }

    Probe createProbe(JsonNode probeNode) {
        String cid = probeNode.get(CID).asText();
        String probeId = probeNode.get(PROBE_ID).asText();
        String url = probeNode.get(URL_STRING).asText();
        String smiles = probeNode.get(SMILES).asText();
        return new Probe(cid, probeId, url, smiles);
    }

    void addProbes(Project project, JsonNode node) {
        final ArrayNode probeNodes = (ArrayNode) node.get(PROBES);
        if (isNotNull(probeNodes)) {
            for (int i = 0; i < probeNodes.size(); ++i) {
                final JsonNode probeNode = probeNodes.get(i);
                final Probe probe = createProbe(probeNode);
                project.addProbe(probe);
            }
        }
    }

    protected void addNonCAPannotations(Project project, JsonNode node) {
        addAnnotations(project, node,
                getDataSource(EntityNamedSources.GOBPAnnotationSource),
                GOBP_ID, GOBP_TERM);
        addAnnotations(project, node,
                getDataSource(EntityNamedSources.GOMFAnnotationSource),
                GOMF_ID, GOMF_TERM);
        addAnnotations(project, node,
                getDataSource(EntityNamedSources.GOCCAnnotationSource),
                GOCC_ID, GOCC_TERM);

        addAnnotations(project, node,
                getDataSource(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource),
                null, KEGG_DISEASE_CAT);
        addAnnotations(project, node,
                getDataSource(EntityNamedSources.KEGGDiseaseNameAnnotationSource),
                null, KEGG_DISEASE_NAMES);
    }

    protected void addCAPannotations(final Project project, final JsonNode node) {
        // pull in assay annotations - process different source with different DataSource's
        if (node.has(AK_DICT_LABEL)) {
            addAnnotations(project, node, CAP_ANNOTATIONS, AK_DICT_LABEL, AV_DICT_LABEL);
        }
    }

    protected void addAllAnnotations(final Project project, final JsonNode node) {
        addCAPannotations(project, node);
        addNonCAPannotations(project, node);
    }

    protected void addAnnotations(final Project project, final JsonNode node, final DataSource ds, final String keyField, final String valueField) {
        if (node.has(keyField) && !node.get(keyField).isNull()) {
            ArrayNode keys = (ArrayNode) node.get(keyField);
            ArrayNode vals = (ArrayNode) node.get(valueField);
            addSingleArrayNodeAnnotation(project, ds, keys, vals);
        }
    }

    protected void addSingleArrayNodeAnnotation(final Project project, final DataSource ds, final ArrayNode keys, final ArrayNode vals) {
        if (isNotNull(keys) && isNotNull(vals)) {
            for (int i = 0; i < keys.size(); ++i) {
                final String key = keys.get(i).asText();
                final String val = vals.get(i).asText();
                addSingleAnnotation(project, ds, key, val);
            }
        }
    }

    protected void addSingleAnnotation(final Project project, final DataSource ds, final String key, final String val) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(val)) {
            project.addValue(new StringValue(ds, key, val));
        }
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
