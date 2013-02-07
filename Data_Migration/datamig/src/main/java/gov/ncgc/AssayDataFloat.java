package gov.ncgc;
// $Id: AssayDataFloat.java 2278 2008-05-29 22:27:45Z nguyenda $

public class AssayDataFloat extends AssayData {
    private static final long serialVersionUID = 12242007;

    public AssayDataFloat () {}
    public AssayDataFloat (int tid) { super (tid);}
    public AssayDataFloat (int tid, Float value) { super (tid, value); }
    public Float getFloat () { return (Float)getValue(); }
    public void setFloat (Float value) { setValue (value); }
}
