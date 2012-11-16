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
public class ETagSearchResult extends SearchResultImp<Value> {
    Principal principal;
    private RESTAbstractEntityService restAbstractEntityService;

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
}
