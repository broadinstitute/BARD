package bard.core.rest;

import bard.core.*;
import bard.core.interfaces.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RESTExperimentService
        extends RESTAbstractEntityService<Experiment>
        implements ExperimentService {


    static final Value DONE = new Value(DataSource.getCurrent());

    protected RESTExperimentService
            (RESTEntityServiceManager srvman, String baseURL) {
        super(srvman, baseURL);
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

        if (resource.indexOf(QUESTION_MARK) < 0) {
            resource.append(QUESTION_MARK);
        } else {
            resource.append(AMPERSAND);
        }
        resource.append(SKIP).append(skip).append(TOP).append(top).append(AMPERSAND).append(EXPAND_TRUE);
        return resource.toString();
    }

    protected List<Value> getValues
            (Experiment expr, Object etag, long top, long skip) {

        final List<Value> values = new ArrayList<Value>();

        final String resource = buildExperimentQuery(expr, etag, top, skip);
        final DataSource source = new DataSource
                (getResourceContext(), expr.getId().toString());
        source.setURL(resource);
        final JsonNode root = executeGetRequest(resource);
        final JsonNode node = root.get(COLLECTION);
        ArrayNode array = null;

        if (isNotNull(node) && node.isArray()) {
            array = (ArrayNode) node;
        } else if (root.isArray()) {
            array = (ArrayNode) root;
        }

        if (isNotNull(array)) {
            for (int i = 0; i < array.size(); ++i) {
                JsonNode n = array.get(i);
                values.add(getValue(source, n));
            }
        }
        return values;
    }

    protected long streamValues(BlockingQueue<Value> queue,
                                Experiment expr, Object etag,
                                long top, long skip) {
        long streamed = 0;
        try {
            final String resource = buildExperimentQuery(expr, etag, top, skip);
            DataSource source = new DataSource
                    (getResourceContext(), expr.getId().toString());
            source.setURL(resource);

            final JsonNode root = executeGetRequest(resource);
            JsonNode node = root.get(COLLECTION);
            ArrayNode array = null;

            if (isNotNull(node) && node.isArray()) {
                array = (ArrayNode) node;
            } else if (root.isArray()) {
                array = (ArrayNode) root;
            }

            if (isNotNull(array)) {
                for (int i = 0; i < array.size(); ++i) {
                    JsonNode n = array.get(i);
                    queue.put(getValue(source, n));
                    ++streamed;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            queue.offer(DONE);
        }
        return streamed;
    }

    protected Value parseReadout(Value parent, JsonNode node) {
        HillCurveValue hcv = new HillCurveValue
                (parent, node.get(NAME).asText());

        JsonNode n = node.get(S_0);
        if (isNotNull(n)) {
            hcv.setS0(n.asDouble());
        }

        n = node.get(S_INF);
        if (isNotNull(n)) {
            hcv.setSinf(n.asDouble());
        }

        n = node.get(HILL);
        if (isNotNull(n)) {
            hcv.setCoef(n.asDouble());
        }

        n = node.get(AC_50);
        if (isNotNull(n)) {
            hcv.setSlope(n.asDouble());
        }
        n = node.get(CONC_UNIT);
        if (isNotNull(n)) {
            hcv.setConcentrationUnits(n.asText());
        }
        ArrayNode crc = (ArrayNode) node.get(CR);
        for (int i = 0; i < crc.size(); ++i) {
            ArrayNode xy = (ArrayNode) crc.get(i);
            hcv.add(xy.get(0).asDouble(), xy.get(1).asDouble());
        }

        return hcv;
    }

    protected Value getValue(DataSource source, JsonNode node) {
        String id = node.get(EXPT_DATA_ID).asText();
        Value v = new Value(source, id);
        new IntValue(v, EID, node.get(EID).asInt());
        new LongValue(v, CID, node.get(CID).asLong());
        new LongValue(v, SID, node.get(SID).asLong());

        ArrayNode readouts = (ArrayNode) node.get(READOUTS);
        for (int i = 0; i < readouts.size(); ++i) {
            JsonNode n = readouts.get(i);
            parseReadout(v, n);
        }

        JsonNode n = node.get(POTENCY);
        if (isNotNull(n)) {
            new NumericValue(v, POTENCY, n.asDouble());
        }

        n = node.get(OUTCOME);
        if (isNotNull(n)) {
            new IntValue(v, OUTCOME, n.asInt());
        }

        return v;
    }

    protected Experiment getEntity(Experiment e, JsonNode node) {
        if (e == null) {
            e = new Experiment();
        }
        e.setId(node.get(EXPT_ID).asInt());
        e.setName(node.get(NAME).asText());
        e.setDescription(node.get(DESCRIPTION).asText());

        e.setCategory(ExperimentCategory.valueOf
                (node.get(CATEGORY).asInt()));
        e.setType(ExperimentType.valueOf
                (node.get(TYPE).asInt()));
        e.setRole(ExperimentRole.valueOf
                (node.get(CLASSIFICATION).asInt()));
        e.setPubchemAid(node.get(PUBCHEM_AID).asLong());

        // we'll always have an expanded form, but we only reutrn a
        // a partially filled Assay object
        JsonNode n = node.get(ASSAY_ID);
        if (isNotNull(n) && n.isArray()) {
            RESTAssayService service = (RESTAssayService) getServiceManager()
                    .getService(Assay.class);
            ArrayNode assayNode = (ArrayNode) n;
            for (int i = 0; i < assayNode.size(); i++) {
                n = assayNode.get(i);
                Assay assay = new Assay();
                service.getEntity(assay, n);
                e.setAssay(assay);
            }
        }

        DataSource ds = getDataSource();
        n = node.get(COMPOUNDS);
        if (isNotNull(n)) {
            e.add(new IntValue
                    (ds, Experiment.ExperimentCompoundCountValue, n.asInt()));
        }

        n = node.get(SUBSTANCES);
        if (isNotNull(n)) {
            e.add(new IntValue
                    (ds, Experiment.ExperimentSubstanceCountValue, n.asInt()));
        }

        return e;
    }

    protected Experiment getEntitySearch(Experiment e, JsonNode node) {
        if (e == null) {
            e = new Experiment();
        }

        return e;
    }

    @Override
    public <T extends Entity> SearchResult<T> searchResult
            (Experiment expr, Class<T> clazz) {
        RESTAbstractEntityService<T> service =
                (RESTAbstractEntityService) getServiceManager().getService(clazz);

        if (clazz.equals(Project.class)) {
            return service.getSearchResult
                    (getResource(expr.getId() + PROJECTS_RESOURCE), null);
        } else if (clazz.equals(Compound.class)) {
            return service.getSearchResult
                    (getResource(expr.getId() + COMPOUNDS_RESOURCE), null);
        } else {
            throw new IllegalArgumentException
                    ("No related searchResults available for " + clazz);
        }
    }

    public SearchResult<Compound> compounds(Experiment expr) {
        String resource = getResource(expr.getId()) + COMPOUNDS_RESOURCE;
        RESTCompoundService cs = (RESTCompoundService)
                getServiceManager().getService(Compound.class);
        return cs.getSearchResult(resource, null);
    }

    public SearchResult<Value> activities(Experiment expr) {
        return new ActivitySearchResult(this, expr).build();
    }

    public SearchResult<Value> activities(Experiment expr, Object etag) {
        return new ActivitySearchResult(this, expr, etag).build();
    }
}
