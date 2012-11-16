package bard.core.rest;

import bard.core.Entity;
import bard.core.interfaces.EntityService;
import bard.core.interfaces.SearchResult;
import bard.core.ServiceParams;
import bard.core.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*
 * A refactor is needed!
 */
public class RESTSearchResult<E extends Entity> extends SearchResultImp<E> {

    private int bufferSize;
    private ServiceParams params;
    private String resource;
    private RESTAbstractEntityService restAbstractEntityService;

    protected RESTSearchResult(){
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
           addETag(etags);
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
                addETag(etags);

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
}
