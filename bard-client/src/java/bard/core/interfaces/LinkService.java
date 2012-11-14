package bard.core.interfaces;

import bard.core.Entity;
import bard.core.Link;

public interface LinkService extends EntityService<Link> {
    <E extends Entity> SearchResult<E> neighbors(E entity);
}
