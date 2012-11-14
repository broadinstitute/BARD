package bard.core;




public class StringValue extends Value {  
    private static final long serialVersionUID = 0x47ed3eed965a7f86l;

    protected String value;

    protected StringValue () {}
    public StringValue (Value parent) {
        super (parent);
    }
    public StringValue (Value parent, String id) {
        super (parent, id);
    }
    public StringValue (Value parent, String id, String value) {
        super (parent, id);
        this.value = value;
    }
    public StringValue (DataSource source, String id) {
        this (source, id, null);
    }
    public StringValue (DataSource source, String id, String value) {
        super (source, id);
        this.value = value;
    }

    public void setValue (String value) {
        this.value = value;
    }

    @Override
    public String getValue () { return value; }
}
