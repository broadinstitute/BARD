package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.interfaces.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class RESTAssayService extends RESTAbstractEntityService<Assay>
        implements AssayService, AssayValues {


    protected RESTAssayService
            (final RESTEntityServiceManager srvman, final String baseURL) {
        super(srvman, baseURL);
    }

    public Class<Assay> getEntityClass() {
        return Assay.class;
    }

    public String getResourceContext() {
        return ASSAYS_RESOURCE;
    }

    protected Publication createPublication(final ObjectNode on) {
        Publication pub = new Publication();
        final JsonNode title = on.get(TITLE);
        if (isNotNull(title)) {
            pub.setTitle(title.asText());
        }

        final JsonNode doi = on.get(DOI);
        if (isNotNull(doi)) {
            pub.setDoi(doi.asText());
        }
        final JsonNode abs = on.get(ABS);
        if (isNotNull(abs)) {
            pub.setAbs(abs.asText());
        }

        final JsonNode pubmedId = on.get(PUBMED_ID);
        if (isNotNull(pubmedId)) {
            pub.setPubmedId(pubmedId.asLong());
        }
        return pub;
    }

    protected void addPublications(final JsonNode node, final Assay assay) {
        final ArrayNode pubNode = (ArrayNode) node.get(PUBLICATIONS);
        if (isNotNull(pubNode)) {
            for (int i = 0; i < pubNode.size(); i++) {
                ObjectNode on = (ObjectNode) pubNode.get(i);
                Publication pub = createPublication(on);
                assay.addPublication(pub);
            }
        }

    }

    protected void addEntitySummary(final Assay assay, final JsonNode node) {
        final JsonNode capAssayNode = node.get(CAP_ASSAY_ID);
        if (isNotNull(capAssayNode)) {
            assay.setCapAssayId(capAssayNode.longValue());
        }

        final JsonNode bardAssayId = node.get(BARD_ASSAY_ID);
        if (isNotNull(bardAssayId)) {
            assay.setId(bardAssayId.longValue());
        }

        final JsonNode name = node.get(NAME);
        if (isNotNull(name)) {
            assay.setName(name.asText());
        }

        final JsonNode description = node.get(DESCRIPTION);
        if (isNotNull(description)) {
            assay.setDescription(description.asText());
        }

        final JsonNode protocol = node.get(PROTOCOL);
        if (isNotNull(protocol)) {
            assay.setProtocol(protocol.asText());
        }

        final JsonNode comments = node.get(COMMENTS);
        if (isNotNull(comments)) {
            assay.setComments(comments.asText());
        }

        final JsonNode category = node.get(CATEGORY);
        if (isNotNull(category)) {
            assay.setCategory(AssayCategory.valueOf(category.asInt()));
        }

        final JsonNode type = node.get(TYPE);
        if (isNotNull(type)) {
            assay.setType(AssayType.valueOf(type.asInt()));
        }

        final JsonNode classification = node.get(CLASSIFICATION);
        if (isNotNull(classification)) {
            assay.setRole(AssayRole.valueOf(classification.asInt()));
        }

        DataSource ds = getDataSource();
        final JsonNode source = node.get(SOURCE);
        if (isNotNull(source)) {
            assay.addValue(new StringValue
                    (ds, AssaySourceValue, source.asText()));
        }

        final JsonNode grantNo = node.get(GRANT_NO);
        if (isNotNull(grantNo)) {
            assay.addValue(new StringValue
                    (ds, AssayGrantValue, grantNo.asText()));
        }
        final JsonNode aid = node.get(AID);
        if (isNotNull(aid)) {
            assay.addValue(new IntValue
                    (ds, AssayPubChemAIDValue, aid.asInt()));
        }

    }

    protected Assay getEntity(Assay assay, JsonNode node) {
        if (assay == null) {
            assay = new Assay();
        }
        addEntitySummary(assay, node);
        addAnnotations(assay, node);
        addPublications(node, assay);
        addTargets(node, assay);
        return assay;
    }

    protected void addTarget(final JsonNode targetNode, final Assay assay) {
        final DataSource ds = getDataSource();
        final JsonNode accNode = targetNode.get(ACC);
        if (isNotNull(accNode)) {
            final Biology target = new Biology();
            target.setId(accNode.asText());
            target.addValue(new StringValue
                    (ds, Biology.AccessionValue, accNode.asText()));

            final JsonNode descriptionNode = targetNode.get(DESCRIPTION);
            if (isNotNull(descriptionNode)) {
                target.setDescription(descriptionNode.asText());
            }
            final JsonNode nameNode = targetNode.get(NAME);
            if (isNotNull(nameNode)) {
                target.setName(nameNode.asText());
            }
            final JsonNode geneIdNode = targetNode.get(GENE_ID);
            if (isNotNull(geneIdNode)) {
                target.addValue(new IntValue
                        (ds, Biology.GeneIDValue, geneIdNode.asInt()));
            }
            final JsonNode taxonomyNode = targetNode.get(TAX_ID);
            if (isNotNull(taxonomyNode)) {
                target.addValue(new IntValue
                        (ds, Biology.TaxonomyIDValue, taxonomyNode.asInt()));
            }
            assay.addTarget(target);
        }

    }

    protected void addTargets(final JsonNode node, final Assay assay) {
        final JsonNode targetRootNode = node.get(TARGETS);
        if (isNotNull(targetRootNode)) {
            ArrayNode targets = (ArrayNode) targetRootNode;
            for (int i = 0; i < targets.size(); ++i) {
                final JsonNode targetNode = targets.get(i);
                addTarget(targetNode, assay);
            }
        }
    }

    protected void addEntitySearchSummary(final Assay assay, final JsonNode node) {
        final JsonNode assay_id = node.get(ASSAY_ID);
        if (isNotNull(assay_id)) {
            assay.setId(assay_id.asLong());
        }

        final JsonNode name = node.get(NAME);
        if (isNotNull(name)) {
            assay.setName(name.asText());
        }
        final JsonNode description = node.get(DESCRIPTION);
        if (isNotNull(description)) {
            assay.setDescription(description.asText());
        }
        final JsonNode protocol = node.get(PROTOCOL);
        if (isNotNull(protocol)) {
            assay.setProtocol(protocol.asText());
        }
        final JsonNode comment = node.get(COMMENT);
        if (isNotNull(comment)) {
            assay.setComments(comment.asText());
        }

    }

    protected void addHighlight(final Assay assay, final JsonNode node) {
        DataSource ds = getDataSource();
        final JsonNode highlight = node.get(HIGHLIGHT);
        if (isNotNull(highlight)) {
            assay.addValue(new StringValue
                    (ds, Entity.SearchHighlightValue, highlight.asText()));
        }

    }

    protected Assay getEntitySearch(Assay assay, JsonNode node) {
        if (assay == null) {
            assay = new Assay();
        }
        addEntitySearchSummary(assay, node);
        addHighlight(assay, node);
        addAnnotations(assay, node);
        return assay;
    }

    protected void addAnnotations(final Assay assay, final JsonNode node) {
        // pull in assay annotations - process different source with different DataSource's
        final DataSource capds = getDataSource(EntityNamedSources.CAPAnnotationSource);
        if (node.has(AK_DICT_LABEL)) {
            final ArrayNode keys = (ArrayNode) node.get(AK_DICT_LABEL);
            final ArrayNode vals = (ArrayNode) node.get(AV_DICT_LABEL);
            addKeyValuesAsString(assay, keys, vals, capds);
        }
        addAllAnnotations(assay, node);
    }

    protected void addKeyValueAsString(final Assay assay, final String key, final String val, final DataSource ds) {
        assay.addValue(new StringValue(ds, key, val));
    }

    protected void addKeyValuesAsString(final Assay assay, final ArrayNode keys, final ArrayNode vals, final DataSource ds) {
        if (isNotNull(keys) && isNotNull(vals)) {
            for (int index = 0; index < keys.size(); ++index) {
                final JsonNode keyNode = keys.get(index);
                final JsonNode valueNode = vals.get(index);
                if (isNotNull(keyNode) && isNotNull(valueNode)) {
                    final String key = keyNode.asText();
                    final String val = valueNode.asText();
                    addKeyValueAsString(assay, key, val, ds);
                }
            }
        }
    }

    protected void addAnnotations(final Assay assay, final JsonNode node, final DataSource ds, final String keyField, final String valueField) {
        if (node.has(keyField) && !node.get(keyField).isNull()) {
            ArrayNode keys = (ArrayNode) node.get(keyField);
            ArrayNode vals = (ArrayNode) node.get(valueField);
            addKeyValuesAsString(assay, keys, vals, ds);
        }
    }

    protected void addAllAnnotations(final Assay assay, final JsonNode node) {
        addAnnotations(assay, node,
                getDataSource(EntityNamedSources.GOBPAnnotationSource),
                GOBP_ID, GOBP_TERM);
        addAnnotations(assay, node,
                getDataSource(EntityNamedSources.GOMFAnnotationSource),
                GOMF_ID, GOMF_TERM);
        addAnnotations(assay, node,
                getDataSource(EntityNamedSources.GOCCAnnotationSource),
                GOCC_ID, GOCC_TERM);

        addAnnotations(assay, node,
                getDataSource(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource),
                null, KEGG_DISEASE_CAT);
        addAnnotations(assay, node,
                getDataSource(EntityNamedSources.KEGGDiseaseNameAnnotationSource),
                null, KEGG_DISEASE_NAMES);

    }


    @Override
    public <T extends Entity> SearchResult<T> searchResult
            (Assay assay, Class<T> clazz) {
        RESTAbstractEntityService<T> service =
                (RESTAbstractEntityService) getServiceManager().getService(clazz);

        if (clazz.equals(Project.class)) {
            return service.getSearchResult
                    (getResource(assay.getId() + PROJECTS_RESOURCE), null);
        } else if (clazz.equals(Experiment.class)) {
            return service.getSearchResult
                    (getResource(assay.getId() + EXPERIMENTS_RESOURCE), null);
        } else {
            final String message = "No related searchResults available for " + clazz;
            log.error(message);
            throw new IllegalArgumentException
                    (message);
        }
    }
}
