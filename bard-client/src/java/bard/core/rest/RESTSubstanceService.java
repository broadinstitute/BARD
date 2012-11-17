package bard.core.rest;

import bard.core.Substance;
import bard.core.interfaces.SubstanceService;
import bard.core.interfaces.SubstanceValues;
import com.fasterxml.jackson.databind.JsonNode;

public class RESTSubstanceService extends RESTAbstractEntityService<Substance>
        implements SubstanceService, SubstanceValues {
    //    static final private Logger logger =
//            Logger.getLogger(RESTSubstanceService.class.getName());

    protected RESTSubstanceService
            (RESTEntityServiceManager srvman, String baseURL) {
        super(srvman, baseURL);
    }

    public Class<Substance> getEntityClass() {
        return Substance.class;
    }

    public String getResourceContext() {
        return SUBSTANCES_RESOURCE;
    }

    protected Substance getEntity(Substance s, JsonNode node) {
        if (s == null) {
            s = new Substance();
        }
        return s;
    }

    protected Substance getEntitySearch(Substance s, JsonNode node) {
        return getEntity(s, node);
    }
}
