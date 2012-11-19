package bard.core.rest;

import bard.core.Entity;
import bard.core.interfaces.EntityService;
import bard.core.interfaces.EntityServiceManager;

import java.util.ArrayList;
import java.util.List;

public class RESTEntityServiceManager implements EntityServiceManager {
    EntityService[] services;

    // static final Logger log = Logger.getLogger(RESTEntityServiceManager.class);
    public RESTEntityServiceManager(String baseURL) {
        services = new EntityService[]{
                new RESTCompoundService(this, baseURL),
                new RESTProjectService(this, baseURL),
                new RESTAssayService(this, baseURL),
                new RESTExperimentService(this, baseURL),
                new RESTSubstanceService(this, baseURL)
        };
    }

    public <T extends EntityService<? extends Entity>> T getService
            (Class<? extends Entity> clazz) {

        Class c = clazz;
        do {
            for (EntityService<? extends Entity> es : services) {
                if (clazz.equals(es.getEntityClass())) {
                    return (T) es;
                }
            }
            c = c.getSuperclass();
        }
        while (c != null);

        return null;
    }

    public <T extends EntityService<? extends Entity>> List<T> getServices() {
        List<T> srv = new ArrayList<T>();
        for (EntityService<? extends Entity> es : services) {
            srv.add((T) es);
        }
        return srv;
    }

    public void shutdown() {
    }
}
