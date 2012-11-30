package bard.core.rest;

import bard.core.Value;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


public class ETagSearchResult extends SearchResultImpl<Value> {
    Principal principal;
    private RESTAbstractEntityService restAbstractEntityService;

    ETagSearchResult(RESTAbstractEntityService restAbstractEntityService, Principal principal) {
        this.restAbstractEntityService = restAbstractEntityService;
        this.principal = principal;
        this.searchResults = new ArrayList<Value>();

    }
    //TODO: Done
    @Override
    public ETagSearchResult build() {
        this.searchResults.clear();
        int top = multiplier * multiplier;
        int ratio = multiplier;
        int skip = 0;
        while (true) {
            final List<Value> etags = restAbstractEntityService.getETags(top, skip);
            this.searchResults.addAll(etags);
            skip += etags.size();
            ratio *= multiplier;
            if (etags.size() < top) {
                break;
            }
            top = findNextTopValue(skip, ratio);
        }
        this.count = this.searchResults.size();
        return this;
    }
}
