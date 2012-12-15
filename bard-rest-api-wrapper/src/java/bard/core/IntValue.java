package bard.core;




public class IntValue extends Value {  
    private static final long serialVersionUID = 0x8380a4013ac78833l;

    protected Integer value;

    protected IntValue () {}
    public IntValue (Value parent) {
        super (parent);
    }
    public IntValue (Value parent, String id) {
        super (parent, id);
    }
    public IntValue (Value parent, String id, Integer value) {
        super (parent, id);
        this.value = value;
    }
    public IntValue (DataSource source, String id) {
        this (source, id, null);
    }
    public IntValue (DataSource source, String id, Integer value) {
        super (source, id);
        this.value = value;
    }

    public void setValue (Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue () { return value; }
    /**
     * Subclasses should override this
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        IntValue that = (IntValue)o;
        //reverse
        final int compare = this.getValue().compareTo(that.getValue());
        if(compare == 0){
            //then lets compare the names
            return this.id.compareTo(that.id);
        }
        return compare * -1;
    }
}
