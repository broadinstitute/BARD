package bard.core;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class ServiceParams implements Serializable {
    private static final long serialVersionUID = 0x5a0c250a6d096052l;

    protected Long top;
    protected Long skip;
    protected String ordering;
    protected Boolean partial;
    protected Integer depth = 3;

    public ServiceParams () {
    }

    public ServiceParams (Integer depth) {
        this.depth = depth;
    }

    public ServiceParams (Long top, Long skip) {
        this.top = top;
        this.skip = skip;
    }

    public Long getTop () { return top; }
    public ServiceParams setTop (Long top) { 
        this.top = top;
        return this;
    }

    public Long getSkip () { return skip; }
    public ServiceParams setSkip (Long skip) {
        this.skip = skip;
        return this;
    }

    public String getOrdering () { return ordering; }
    public ServiceParams setOrdering (String ordering) { 
        this.ordering = ordering; 
        return this;
    }

    public Boolean getPartial () { return partial; }
    public ServiceParams setPartial (Boolean partial) {
        this.partial = partial;
        return this;
    }

    public Integer getMaxDepth () { return depth; }
    public ServiceParams setMaxDepth (Integer depth) {
        this.depth = depth;
        return this;
    }
}
