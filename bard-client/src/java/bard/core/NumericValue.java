package bard.core;




public class NumericValue extends Value {  
    private static final long serialVersionUID = 0x795fc131bdd311dbl;

    protected Number value;

    protected NumericValue () {}
    public NumericValue (Value parent) {
        super (parent);
    }
    public NumericValue (Value parent, String id) {
        super (parent, id);
    }
    public NumericValue (Value parent, String id, Number value) {
        super (parent, id);
        this.value = value;
    }
    public NumericValue (DataSource source, String id) {
        this (source, id, null);
    }
    public NumericValue (DataSource source, String id, Number value) {
        super (source, id);
        this.value = value;
    }

    public void setValue (Number value) {
        this.value = value;
    }

    @Override
    public Number getValue () { return value; }
}
