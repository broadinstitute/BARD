package bard.core.rest;

import bard.core.Value;
import bard.core.interfaces.SearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 11/15/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SearchResultImp<E> implements SearchResult {
    protected List<E> searchResults;
    protected long count = 0;
    protected Object etag;
    protected List<Value> facets = new ArrayList<Value>();


    public List<E> next(int top) {
        return next(top, 0);
    }


    public List<E> getSearchResults() {
        return this.searchResults;
    }


    public List<E> next(int top, int skip) {
        if (top < 0) {
            throw new IllegalArgumentException("Top must be a number greater than or equals zero");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip must be a number greater than or equals zero");
        }
        if (top > this.count) {
            return this.searchResults;
        }
        if (skip > this.count) {
            return new ArrayList<E>();
        }
        if ((skip + top) > this.count) {
            return this.searchResults.subList(skip, this.searchResults.size());
        }
        return this.searchResults.subList(skip, top+skip);
    }


    public Object getETag() {
        return this.etag;
    }


    public Collection<Value> getFacets() {
        return this.facets;
    }


    public long getCount() {
        return this.count;
    }
}
