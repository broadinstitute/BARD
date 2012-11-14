package bard.core;




public class LongValue extends Value {  
    private static final long serialVersionUID = 0x3ada977e36e9e57cl;

    protected Long value;

    protected LongValue () {}
    public LongValue (Value parent) {
        super (parent);
    }
    public LongValue (Value parent, String id) {
        super (parent, id);
    }
    public LongValue (Value parent, String id, Long value) {
        super (parent, id);
        this.value = value;
    }
    public LongValue (DataSource source, String id) {
        this (source, id, null);
    }
    public LongValue (DataSource source, String id, Long value) {
        super (source, id);
        this.value = value;
    }

    public void setValue (Long value) {
        this.value = value;
    }

    @Override
    public Object getValue () { return value; }
}
