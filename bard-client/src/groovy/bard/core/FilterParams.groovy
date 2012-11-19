package bard.core

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
class FilterParams extends ServiceParams {

    protected String filter;
    protected Object[] arguments;
    protected String variables; // pass through
    public FilterParams(){
        super()
    }
    public FilterParams(String filter, Object... args) {
        this.filter = filter;
        this.arguments = args;
    }

    public String getFilter() { return filter; }

    public Object[] getArguments() { return arguments; }

    public FilterParams setFilter(String filter, Object... args) {
        this.filter = filter;
        this.arguments = args;
        return this;
    }

    public String getVariables() { return variables; }

    public FilterParams setVariables(String variables) {
        this.variables = variables;
        return this;
    }

}