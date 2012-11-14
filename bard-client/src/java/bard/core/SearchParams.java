package bard.core;

import java.util.List;

/**
 * Basic text search parameters
 */
public class SearchParams extends ServiceParams {
    private static final long serialVersionUID = 0xedd95256480bd363l;

    protected String query;
    protected List<String[]> filters = null;

    public SearchParams () {
    }

    public SearchParams (String query) {
        this.query = query;
    }
    
    public SearchParams(String query, List<String[]> filters) {
        this.query = query;
        this.filters = filters;
    }

    public List<String[]> getFilters() {
        return filters;
    }

    public void setFilters(List<String[]> filters) {
        this.filters = filters;
    }

    public String getQuery () { return query; }
    public SearchParams setQuery (String query) {
        this.query = query;
        return this;
    }
}
