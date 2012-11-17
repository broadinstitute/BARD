package bard.core.interfaces;

import bard.core.Value;

import java.util.Collection;
import java.util.List;

public interface SearchResult<E> {
    final int multiplier = 5;

    public List<E> next(int top);

    public List<E> next(int top, int skip);

    long getCount(); // total count (if known)

    Object getETag();

    Collection<Value> getFacets();

    List<E> getSearchResults();
    public E build();

}
