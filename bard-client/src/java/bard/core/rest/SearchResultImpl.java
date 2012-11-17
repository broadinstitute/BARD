package bard.core.rest;

import bard.core.Value;
import bard.core.interfaces.SearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;

public abstract class SearchResultImpl<E> implements SearchResult {
    Logger log = Logger.getLogger(SearchResultImpl.class);
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
            final String message = "Top must be a number greater than or equals zero";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        if (skip < 0) {
            final String message = "skip must be a number greater than or equals zero";
            log.error(message);
            throw new IllegalArgumentException(message);
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
    protected int findNextTopValue(long skip, int ratio){

        ///cap this at 1000
        if (skip > 1000) {
            return 1000;
        } else {
            return ratio;
        }
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
