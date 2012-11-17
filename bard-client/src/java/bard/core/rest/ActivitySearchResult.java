package bard.core.rest;

import bard.core.Experiment;
import bard.core.Value;

import java.util.ArrayList;
import java.util.List;


public class ActivitySearchResult extends SearchResultImpl<Value> {
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
    @Override
    public ActivitySearchResult build() {
        this.facets.clear();
        this.searchResults.clear();
        // unbounded fetching
        int top = multiplier * multiplier;
        int ratio = multiplier;
        long skip = 0;

        while (true) {
            List<Value> values = this.restExperimentService.getValues(expr, etag, top, skip);
            this.searchResults.addAll(values);
            skip += values.size();
            ratio *= multiplier;
            if (values.size() < top) {
                break; // we're done
            }
            top = findNextTopValue(skip, ratio);

        }
        this.count = this.searchResults.size();
        return this;
    }
}
