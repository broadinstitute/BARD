package bard.core;


import java.math.BigDecimal;

public class NumericValue extends Value {
    private static final long serialVersionUID = 0x795fc131bdd311dbl;

    protected BigDecimal value;

    protected NumericValue () {}
    public NumericValue (Value parent) {
        super (parent);
    }
    public NumericValue (Value parent, String id) {
        super (parent, id);
    }
    public NumericValue (Value parent, String id, BigDecimal value) {
        super (parent, id);
        this.value = value;
    }
    public NumericValue (DataSource source, String id) {
        this (source, id, null);
    }
    public NumericValue (DataSource source, String id, BigDecimal value) {
        super (source, id);
        this.value = value;
    }

    public void setValue (BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal getValue () { return value; }
    /**
     * Subclasses should override this
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        NumericValue that = (NumericValue)o;
        //reverse
        final int compare = this.getValue().compareTo(that.getValue());
        if(compare == 0){
            //then lets compare the names
            return this.id.compareTo(that.id);
        }
        return compare * -1;
    }
}
