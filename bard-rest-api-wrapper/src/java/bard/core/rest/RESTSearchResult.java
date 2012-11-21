package bard.core.rest;

import bard.core.Entity;
import bard.core.ServiceParams;
import bard.core.Value;
import bard.core.interfaces.EntityService;

import java.util.ArrayList;
import java.util.List;


/*
 * A refactor is needed!
 */
public class RESTSearchResult<E extends Entity> extends SearchResultImpl<E> {

    private int bufferSize;
    private ServiceParams params;
    private String resource;
    private RESTAbstractEntityService restAbstractEntityService;

    protected RESTSearchResult() {
        this.searchResults = new ArrayList<E>();
        this.facets = new ArrayList<Value>();
    }

    public RESTSearchResult(RESTAbstractEntityService restAbstractEntityService,
                            String resource, ServiceParams params) {
        this(restAbstractEntityService, RESTAbstractEntityService.DEFAULT_BUFSIZ, resource, params);
    }

    public RESTSearchResult(RESTAbstractEntityService restAbstractEntityService, int bufferSize,
                            String resource, ServiceParams params) {
        this.restAbstractEntityService = restAbstractEntityService;
        this.bufferSize = bufferSize;
        this.params = params;
        this.resource = resource;
        this.searchResults = new ArrayList<E>();
        this.facets = new ArrayList<Value>();
    }

    protected void addETag(List etags) {
        if (this.etag == null && !etags.isEmpty()) {
            // for now just grad the first etag
            this.etag = etags.iterator().next();
        }
    }

    protected final void buildWithParams(String resource, long top, long skip, List<Value> facets) {
        List etags = new ArrayList();
        List<E> results = restAbstractEntityService.get
                (resource, true, top, skip, etags, facets);
        searchResults.addAll(results);
        addETag(etags);
        this.count = getHitCount(this.facets);
    }

    protected final void buildWithNoParams(String resource,  List<Value> facets) {
        final List etags = new ArrayList();
        long top= multiplier * multiplier;
        long skip=0;
        int ratio = multiplier;

        while (true) {
            facets.clear();
            List<E> results = restAbstractEntityService.get
                    (resource, true, top, skip, etags, facets);
            searchResults.addAll(results);
            addETag(etags);

            this.count = getHitCount(this.facets);
            skip += searchResults.size();
            ratio *= multiplier;
            if (results.size() < top || results.size() > EntityService.MAXIMUM_NUMBER_OF_COMPOUNDS) {
                break;
            }
            top = findNextTopValue(skip, ratio);
        }
    }
    @Override
    public RESTSearchResult build() {
        //clear results
        this.searchResults.clear();
        this.facets.clear();

        long top = bufferSize;
        long skip = 0;
        if (params != null) {
            if (params.getSkip() != null) {
                skip = params.getSkip();
            }
           if (params.getTop() != null) {
                top = params.getTop();
            }
            buildWithParams(this.resource, top, skip, this.facets);
        } else {
            // unbounded fetching with geometric progression
            buildWithNoParams(this.resource, this.facets);
        }
        if (this.count == 0) {
            this.count = this.searchResults.size();
        }
        return this;
    }


    int getHitCount(final List<Value> facets) {
        Value hv = null;
        for (Value v : facets) {
            if (v.getId().equals("_HitCount_")) {
                hv = v;
                break;
            }
        }

        if (hv != null) {
            facets.remove(hv);
            return (Integer) hv.getValue();
        }
        return 0;
    }
}
