package bard.core.interfaces;

import bard.core.Compound;
import bard.core.rest.spring.util.StructureSearchParams;
import bard.core.Value;

import java.util.Collection;

public interface CompoundService extends EntityService<Compound> {
    /*
     * structure searching
     */
    SearchResult<Compound> structureSearch(StructureSearchParams params);

    /*
     * return compound synonyms
     */
    Collection<Value> getSynonyms(Compound compound);
}
