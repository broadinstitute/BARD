package bard.core.rest;

import bard.core.*;
import bard.core.interfaces.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class RESTExperimentService
        extends RESTAbstractEntityService<Experiment>
        implements ExperimentService {

    RESTAssayService restAssayService;

    //static final Value DONE = new Value(DataSource.getCurrent());
    public void setRestAssayService(RESTAssayService restAssayService) {
        this.restAssayService = restAssayService;
    }

    protected RESTExperimentService(String baseURL) {
        super(baseURL);
    }

    public Class<Experiment> getEntityClass() {
        return Experiment.class;
    }

    public String getResourceContext() {
        return EXPERIMENTS_RESOURCE;
    }

    String buildExperimentQuery(final Experiment experiment, final Object etag, final long top, final long skip) {
        StringBuilder resource = new StringBuilder
                (getResource(experiment.getId()));
        if (etag != null) {
            resource.append(FORWARD_SLASH).append(ETAG).append(FORWARD_SLASH).append(getETagId(etag));
        }
        resource.append(EXPTDATA_RESOURCE);
        resource.append(QUESTION_MARK);
        resource.append(SKIP).append(skip).append(TOP).append(top).append(AMPERSAND).append(EXPAND_TRUE);
        return resource.toString();
    }

    protected List<Value> getValues
            (Experiment expr, Object etag, long top, long skip) {

        final String resource = buildExperimentQuery(expr, etag, top, skip);
        final DataSource source = new DataSource
                (getResourceContext(), expr.getId().toString());
        source.setURL(resource);
        final JsonNode root = executeGetRequest(resource);

        return processRootNode(root, source);

    }

    protected List<Value> processRootNode(final JsonNode rootNode, final DataSource dataSource) {
        final JsonNode node = rootNode.get(COLLECTION);
        ArrayNode array = null;

        if (isNotNull(node) && node.isArray()) {
            array = (ArrayNode) node;
        } else if (rootNode.isArray()) {
            array = (ArrayNode) rootNode;
        }
        return extractValuesFromNode(array, dataSource);
    }

    protected List<Value> extractValuesFromNode(final ArrayNode arrayNode, final DataSource source) {
        final List<Value> values = new ArrayList<Value>();

        if (isNotNull(arrayNode)) {
            for (int i = 0; i < arrayNode.size(); ++i) {
                final JsonNode valueNode = arrayNode.get(i);
                values.add(getValue(source, valueNode));
            }
        }
        return values;
    }

    //    protected long streamValues(BlockingQueue<Value> queue,
//                                Experiment expr, Object etag,
//                                long top, long skip) {
//        long streamed = 0;
//        try {
//            final String resource = buildExperimentQuery(expr, etag, top, skip);
//            DataSource source = new DataSource
//                    (getResourceContext(), expr.getId().toString());
//            source.setURL(resource);
//
//            final JsonNode root = executeGetRequest(resource);
//            JsonNode node = root.get(COLLECTION);
//            ArrayNode array = null;
//
//            if (isNotNull(node) && node.isArray()) {
//                array = (ArrayNode) node;
//            } else if (root.isArray()) {
//                array = (ArrayNode) root;
//            }
//
//            if (isNotNull(array)) {
//                for (int i = 0; i < array.size(); ++i) {
//                    JsonNode n = array.get(i);
//                    queue.put(getValue(source, n));
//                    ++streamed;
//                }
//            }
//
//        } catch (Exception ex) {
//            log.error(ex);
//            queue.offer(DONE);
//        }
//        return streamed;
//    }
    protected Value parseReadout(Value parent, JsonNode node) {
        final JsonNode nameNode = node.get(NAME);
        if (isNotNull(nameNode)) {
            final HillCurveValue hcv = new HillCurveValue
                    (parent, nameNode.asText());

            addS0(hcv, node);
            addSinf(hcv, node);
            addCoeff(hcv, node);
            addSlope(hcv, node);
            addConcUnit(hcv, node);
            addCrcs(hcv, node);
            return hcv;
        }
        return null;
    }

    protected void parseReadouts(final Value value, final ArrayNode readouts) {
        for (int i = 0; i < readouts.size(); ++i) {
            final JsonNode readOutNode = readouts.get(i);
            parseReadout(value, readOutNode);
        }
    }


    protected void addReadOuts(final Value value, final JsonNode node) {
        final JsonNode readouts = node.get(READOUTS);
        if (isNotNull(readouts) && readouts.isArray()) {
            parseReadouts(value, (ArrayNode) readouts);
        }
    }

    protected void addS0(final HillCurveValue hcv, final JsonNode node) {
        final JsonNode s0Node = node.get(S_0);
        if (isNotNull(s0Node)) {
            hcv.setS0(s0Node.asDouble());
        }
    }

    protected void addSinf(final HillCurveValue hcv, final JsonNode node) {
        final JsonNode sInfNode = node.get(S_INF);
        if (isNotNull(sInfNode)) {
            hcv.setSinf(sInfNode.asDouble());
        }
    }

    protected void addCoeff(final HillCurveValue hcv, final JsonNode node) {
        final JsonNode hillNode = node.get(HILL);
        if (isNotNull(hillNode)) {
            hcv.setCoef(hillNode.asDouble());
        }
    }

    protected void addSlope(final HillCurveValue hcv, final JsonNode node) {
        final JsonNode ac50Node = node.get(AC_50);
        if (isNotNull(ac50Node)) {
            hcv.setSlope(ac50Node.asDouble());
        }
    }

    protected void addConcUnit(final HillCurveValue hcv, final JsonNode node) {
        final JsonNode concentrationUnitNode = node.get(CONC_UNIT);
        if (isNotNull(concentrationUnitNode)) {
            hcv.setConcentrationUnits(concentrationUnitNode.asText());
        }
    }

    protected void addCrcs(final HillCurveValue hcv, final JsonNode node) {
        final ArrayNode crc = (ArrayNode) node.get(CR);
        for (int i = 0; i < crc.size(); ++i) {
            final ArrayNode xyCoordinate = (ArrayNode) crc.get(i);
            addCrc(hcv, xyCoordinate);
        }
    }

    protected void addCrc(final HillCurveValue hcv, final ArrayNode xyCoordinate) {
        hcv.add(xyCoordinate.get(0).asDouble(), xyCoordinate.get(1).asDouble());
    }


    protected void addSID(final Value value, final JsonNode node) {
        final JsonNode sidNode = node.get(SID);
        if (isNotNull(sidNode)) {
            new LongValue(value, SID, sidNode.asLong());
        }
    }


    protected void addPotency(final Value value, final JsonNode node) {
        final JsonNode potencyNode = node.get(POTENCY);
        if (isNotNull(potencyNode)) {
            new NumericValue(value, POTENCY, potencyNode.asDouble());
        }
    }

    protected void addOutcome(final Value value, final JsonNode node) {
        final JsonNode outcomeNode = node.get(OUTCOME);
        if (isNotNull(outcomeNode)) {
            new IntValue(value, OUTCOME, outcomeNode.asInt());
        }
    }

    protected void addCID(final Value value, final JsonNode node) {
        final JsonNode cidNode = node.get(CID);
        if (isNotNull(cidNode)) {
            new LongValue(value, CID, cidNode.asLong());
        }
    }

    protected void addEID(final Value value, final JsonNode node) {
        final JsonNode eidNode = node.get(EID);
        if (isNotNull(eidNode)) {
            new IntValue(value, EID, eidNode.asInt());
        }
    }

    protected Value createValueFromID(final DataSource source, final JsonNode node) {
        final JsonNode idNode = node.get(EXPT_DATA_ID);
        if (isNotNull(idNode)) {
            final String id = idNode.asText();
            return new Value(source, id);
        }
        return null;

    }

    protected Value createValue(final DataSource source, final JsonNode node) {
        final Value value = createValueFromID(source, node);
        if (value == null) {
            throw new IllegalArgumentException("JSON does not contain " + EXPT_DATA_ID + " node");
        }
        addEID(value, node);
        addCID(value, node);
        addSID(value, node);
        return value;
    }


    protected Value getValue(final DataSource source, final JsonNode node) {
        final Value value = createValue(source, node);
        addReadOuts(value, node);
        addPotency(value, node);
        addOutcome(value, node);
        return value;
    }

    protected void addIdNode(final Experiment experiment, final JsonNode node) {
        final JsonNode idNode = node.get(EXPT_ID);
        if (isNotNull(idNode)) {
            experiment.setId(idNode.asInt());
        } else {
            throw new IllegalArgumentException("Experiment JSON does not contain " + EXPT_ID + " node");
        }

    }

    protected void addNameNode(final Experiment experiment, final JsonNode node) {
        final JsonNode nameNode = node.get(NAME);
        if (isNotNull(nameNode)) {
            experiment.setName(nameNode.asText());
        } else {
            throw new IllegalArgumentException("Experiment JSON does not contain " + NAME + " node");
        }
    }

    protected void addDescriptionNode(final Experiment experiment, final JsonNode node) {
        final JsonNode descriptionNode = node.get(DESCRIPTION);
        if (isNotNull(descriptionNode)) {
            experiment.setDescription(descriptionNode.asText());
        }
    }

    protected void addCategoryNode(final Experiment experiment, final JsonNode node) {
        final JsonNode categoryNode = node.get(CATEGORY);
        if (isNotNull(categoryNode)) {
            experiment.setCategory(ExperimentCategory.valueOf
                    (categoryNode.asInt()));
        }
    }

    protected void addTypeNode(final Experiment experiment, final JsonNode node) {
        final JsonNode typeNode = node.get(TYPE);
        if (isNotNull(typeNode)) {
            experiment.setType(ExperimentType.valueOf
                    (typeNode.asInt()));
        }
    }

    protected void addRoleNode(final Experiment experiment, final JsonNode node) {
        final JsonNode classificationNode = node.get(CLASSIFICATION);
        if (isNotNull(classificationNode)) {
            experiment.setRole(ExperimentRole.valueOf
                    (classificationNode.asInt()));
        }
    }

    protected void addPubChemAIDNode(final Experiment experiment, final JsonNode node) {
        final JsonNode aidNode = node.get(PUBCHEM_AID);
        if (isNotNull(aidNode)) {
            experiment.setPubchemAid(aidNode.asLong());
        }
    }

    protected Experiment getEntity(Experiment experiment, JsonNode node) {
        if (experiment == null) {
            experiment = new Experiment();
        }
        addIdNode(experiment, node);
        addNameNode(experiment, node);
        addDescriptionNode(experiment, node);
        addCategoryNode(experiment, node);
        addTypeNode(experiment, node);
        addRoleNode(experiment, node);
        addPubChemAIDNode(experiment, node);

        // we'll always have an expanded form, but we only return a
        // a partially filled Assay object
        addAssayNode(experiment, node);
        DataSource ds = getDataSource();
        addCompound(experiment, node, ds);
        addSubstance(experiment, node, ds);

        return experiment;
    }

    protected void addAssayNode(final Experiment experiment, final JsonNode node) {
        final JsonNode assayIdNode = node.get(EXPERIMENT_ASSAY_ID);
        if (isNotNull(assayIdNode) && assayIdNode.isArray()) {
            addAssay(experiment, (ArrayNode) assayIdNode);
        }
    }

    protected void addAssay(final Experiment experiment, final ArrayNode assaysNode) {

        for (int i = 0; i < assaysNode.size(); i++) {
            final JsonNode assayNode = assaysNode.get(i);
            addSingleAssayNode(experiment, assayNode, restAssayService);
        }
    }

    protected void addSingleAssayNode(final Experiment experiment, final JsonNode assayNode, final RESTAssayService service) {
        if (isNotNull(assayNode)) {
            final Assay assay = service.getEntity(null, assayNode);
            experiment.setAssay(assay);
        }

    }

    protected void addSubstance(final Experiment experiment, final JsonNode node, final DataSource ds) {
        final JsonNode substanceNode = node.get(SUBSTANCES);
        if (isNotNull(substanceNode)) {
            experiment.addValue(new IntValue
                    (ds, Experiment.ExperimentSubstanceCountValue, substanceNode.asInt()));
        }
    }

    protected void addCompound(final Experiment experiment, final JsonNode node, final DataSource ds) {
        final JsonNode compoundNode = node.get(COMPOUNDS);
        if (isNotNull(compoundNode)) {
            experiment.addValue(new IntValue
                    (ds, Experiment.ExperimentCompoundCountValue, compoundNode.asInt()));
        }
    }

    protected Experiment getEntitySearch(Experiment experiment, JsonNode node) {
        if (experiment == null) {
            experiment = new Experiment();
        }

        return experiment;
    }

    public SearchResult<Value> activities(Experiment expr) {
        return new ActivitySearchResult(this, expr).build();
    }

    public SearchResult<Value> activities(Experiment expr, Object etag) {
        return new ActivitySearchResult(this, expr, etag).build();
    }
}
