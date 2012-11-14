package bard.core.rest;

import bard.core.Entity;
import bard.core.interfaces.EntityService;
import bard.core.interfaces.SearchResult;
import bard.core.ServiceParams;
import bard.core.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 11/4/12
* Time: 9:32 PM
* To change this template use File | Settings | File Templates.
*/ /*
 * A refactor is needed!
 */
class RESTSearchResult<E extends Entity>
        implements SearchResult<E> {

    int bufferSize;

    long count = 0;

    ServiceParams params;
    String resource;
    List<Value> facets;
    List<E> searchResults;
    volatile Object etag;
    private RESTAbstractEntityService restAbstractEntityService;

    RESTSearchResult(RESTAbstractEntityService restAbstractEntityService,
                     String resource, ServiceParams params) {
        this(restAbstractEntityService,RESTAbstractEntityService.DEFAULT_BUFSIZ, resource, params);
    }

    RESTSearchResult(RESTAbstractEntityService restAbstractEntityService, int bufferSize,
                     String resource, ServiceParams params) {
        this.restAbstractEntityService = restAbstractEntityService;
        this.bufferSize = bufferSize;
        this.params = params;
        this.resource = resource;
        this.searchResults = new ArrayList<E>();
        this.facets = new ArrayList<Value>();
    }

    public RESTSearchResult build() {
        //clear results
        this.searchResults.clear();
        this.facets.clear();

        long top = bufferSize;
        long skip = 0;
        List etags = new ArrayList();
        if (params != null) {
            if (params.getSkip() != null) {
                skip = params.getSkip();
            }

            if (params.getTop() != null) {
                top = params.getTop();
            }

            List<E> results = restAbstractEntityService.get
                    (resource, true, top, skip, etags, facets);
            searchResults.addAll(results);
            if (etag == null && !etags.isEmpty()) {
                // for now just assume there is only one etag returned
                this.etag = etags.iterator().next();
            }
            getHitCount();
        } else {
            // unbounded fetching with geometric progression
            int a = 5;
            top = a * a;
            int ratio = a;
            while (true) {
                this.facets.clear();
                List<E> results = restAbstractEntityService.get
                        (resource, true, top, skip, etags, facets);
                searchResults.addAll(results);
                if (etag == null && !etags.isEmpty()) {
                    // for now just assume there is only one
                    //  etag returned
                    etag = etags.iterator().next();
                }

                getHitCount();
                if (results.size() < top || results.size() > EntityService.MAXIMUM_NUMBER_OF_COMPOUNDS) {
                    break;
                }

                skip += searchResults.size();

                // geometric progression cap out at DEFAULT_BUFSIZ
                if (skip > 500) {
                    top = 2000;
                } else {
                    ratio *= a;
                    top = ratio;
                }
            }
        }
        if (this.count == 0) {
            this.count = this.searchResults.size();
        }
        return this;
    }

    public List<E> getSearchResults() {
        return this.searchResults;
    }

    void getHitCount() {
        Value hv = null;
        for (Value v : facets) {
            if (v.getId().equals("_HitCount_")) {
                hv = v;
                break;
            }
        }

        if (hv != null) {
            facets.remove(hv);
            this.count = (Integer) hv.getValue();
        }
    }

    public List<E> next(int top) {
        return next(top, 0);
    }

    public List<E> next(int top, int skip) {
        if (top < 0) {
            throw new IllegalArgumentException("Top must be a number greater than or equals zero");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip must be a number greater than or equals zero");
        }

        if (skip > this.count) {
            return new ArrayList<E>();
        }
        if ((skip + top) > this.count) {
            return this.searchResults.subList(skip, this.searchResults.size());
        }
        return this.searchResults.subList(skip, top+skip);
    }


    public Collection<Value> getFacets() {
        return this.facets;
    }


    public Object getETag() {
        return this.etag;
    }

    public long getCount() {
        return this.count;
    }
}
