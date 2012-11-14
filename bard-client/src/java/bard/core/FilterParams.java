package bard.core;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class FilterParams extends ServiceParams {
    private static final long serialVersionUID = 0x69d0aa5d8122152dl;

    protected String filter;
    protected Object[] arguments;
    protected String variables; // pass through

    public FilterParams (String filter, Object... args) {
        this.filter = filter;
        this.arguments = args;
    }

    public String getFilter () { return filter; }
    public Object[] getArguments () { return arguments; }
    public FilterParams setFilter (String filter, Object... args) {
        this.filter = filter;
        this.arguments = args;
        return this;
    }

    public String getVariables () { return variables; }
    public FilterParams setVariables (String variables) {
        this.variables = variables;
        return this;
    }
}
