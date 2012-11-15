package bard.core.rest;

import bard.core.Experiment;
import bard.core.interfaces.SearchResult;
import bard.core.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ActivitySearchResult extends SearchResultImp<Value> {
    volatile Experiment expr;
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
}
