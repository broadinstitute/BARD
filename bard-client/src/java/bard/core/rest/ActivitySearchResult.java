package bard.core.rest;

import bard.core.Experiment;
import bard.core.interfaces.SearchResult;
import bard.core.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ActivitySearchResult implements SearchResult<Value> {
    volatile Experiment expr;


    List<Value> searchResults;
    long count = 0;
    List<Value> facets = new ArrayList<Value>();
    Object etag;
    private RESTExperimentService restExperimentService;

    ActivitySearchResult(RESTExperimentService restExperimentService, Experiment expr) {
        this(restExperimentService, expr, null);
    }

    ActivitySearchResult(RESTExperimentService restExperimentService, Experiment expr, Object etag) {
        this.restExperimentService = restExperimentService;
        this.expr = expr;
        this.etag = etag;
        this.searchResults = new ArrayList<Value>();
    }

    public ActivitySearchResult build() {
        this.facets.clear();
        this.searchResults.clear();
        // unbounded fetching
        int a = 5;
        int top = a * a;
        int ratio = a;
        long skip = 0;

        while (true) {
            List<Value> values = this.restExperimentService.getValues(expr, etag, top, skip);
            this.searchResults.addAll(values);
            if (values.size() < top) {
                break; // we're done
            }
            skip += values.size();
            // cap this
            if (skip > 1000) {
                top = 1000;
            } else {
                ratio *= a;
                top = ratio;
            }
        }
        if (this.count == 0) {
            this.count = this.searchResults.size();
        }
        return this;
    }

    public List<Value> next(int top) {
        return next(top, 0);
    }

    public List<Value> getSearchResults() {
        return searchResults;
    }

    public List<Value> next(int top, int skip) {
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
            return new ArrayList<Value>();
        }
        if ((skip + top) > this.count) {
            return this.searchResults.subList(skip, this.searchResults.size());
        }
        return this.searchResults.subList(skip, top+skip);
    }

    public Object getETag() {
        return etag;
    }

    public Collection<Value> getFacets() {
        return facets;
    }

    public long getCount() {
        return this.count;
    }
}
