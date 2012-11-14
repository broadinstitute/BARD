package bard.core.interfaces;

import bard.core.Entity;

import java.util.Collection;

public interface EntityServiceManager {
    /*
     * lookup service based on its entity type
     */
    <T extends EntityService<? extends Entity>> T
    getService(Class<? extends Entity> clazz);

    <T extends EntityService<? extends Entity>> Collection<T> getServices();

    /*
     * cleanup
     */
    void shutdown();
}
