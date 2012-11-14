package bard.core.rest;

import bard.core.interfaces.SearchResult;
import bard.core.Value;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 11/4/12
* Time: 9:22 PM
* To change this template use File | Settings | File Templates.
*/
public class ETagSearchResult implements SearchResult<Value> {
    long count = 0;
    Principal principal;
    List<Value> searchResults;
    private RESTAbstractEntityService restAbstractEntityService;

    public List<Value> getSearchResults() {
        return this.searchResults;
    }

    ETagSearchResult(RESTAbstractEntityService restAbstractEntityService, Principal principal) {
        this.restAbstractEntityService = restAbstractEntityService;
        this.principal = principal;
        this.searchResults = new ArrayList<Value>();

    }

    public ETagSearchResult build() {
        this.searchResults.clear();
        int a = 5;
        int top = a * a;
        int ratio = a;
        int skip = 0;
        while (true) {
            if (count < 0) {
                count = 0;
            }
            List<Value> etags = restAbstractEntityService.getETags(top, skip);
            this.searchResults.addAll(etags);
            if (etags.size() < top) {
                break;
            }

            skip += etags.size();

            // geometric progression cap out at DEFAULT_BUFSIZ
            if (skip > 1000) {
                top = 2000;
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

    public List<Value> next(int top, int skip) {
        if (top < 0) {
            throw new IllegalArgumentException("Top must be a number greater than or equals zero");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip must be a number greater than or equals zero");
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
        return null;
    }

    public List<Value> getFacets() {
        return null;
    }


    public long getCount() {
        return this.count;

    }
}
