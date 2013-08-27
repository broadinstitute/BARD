package bard.core;



public class IntValue extends Value {
    private static final long serialVersionUID = 0x8380a4013ac78833l;

    protected Integer value;

    protected IntValue() {
    }

    public IntValue(Value parent) {
        super(parent);
    }

    public IntValue(Value parent, String id) {
        super(parent, id);
    }

    public IntValue(Value parent, String id, Integer value) {
        super(parent, id);
        this.value = value;
    }

    public IntValue(DataSource source, String id) {
        this(source, id, null);
    }

    public IntValue(DataSource source, String id, Integer value) {
        super(source, id);
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * Subclasses should override this
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        //if the key is one of [] then we need to do something different

        IntValue that = (IntValue) o;
        final String thatKey = that.id;
        final String thisKey = this.id;

        //if the facet of the form [* to 100], then [100 to 200] should be sorted differently
        //see https://www.pivotaltracker.com/story/show/41274725
        if ((thisKey.startsWith("[") && thisKey.endsWith("]")) ||
                (thatKey.startsWith("[") && thatKey.endsWith("]"))) {
            return handleFiltersOnProperties(this, that);
        }
        //reverse
        final int compare = this.getValue().compareTo(that.getValue());
        if (compare == 0) {
            //then lets compare the names
            return this.id.compareTo(that.id);
        }
        return compare * -1;
    }

    static String removeSquareBrackets(String input) {
        String tempValue = input.replaceAll("\\[", "").trim();
        return tempValue.replaceAll("]", "").trim();
    }

    /**
     * Handles quantities
     *
     * @param thisIntValue
     * @param thatIntValue
     * @return int
     */
    public static int handleFiltersOnProperties(IntValue thisIntValue, IntValue thatIntValue) {

        //get the ids of the two values
        String thatKey = thatIntValue.id;
        String thisKey = thisIntValue.id;


        //remove all '[' and ']'. We could have used regex here, but i thought it would be confusing
        //Values look like [1 To 10] OR [* TO 100] etc
        thisKey = removeSquareBrackets(thisKey);
        thatKey = removeSquareBrackets(thatKey) ;


        //Handle case where both contain *  e.g [* To 100] or [100 TO *]
        //if this key starts with * or thatKey ends with *, return -1
        if (thisKey.startsWith("*") || thatKey.endsWith("*")) {
            return -1;
        }
        //if this key ends with * or thatKey starts with *, return 1
        if (thatKey.startsWith("*") || thisKey.endsWith("*")) {
            return 1;
        }

        //if we get here then neither contains *
        //Now split around the string 'TO'
        final String[] splitThisKeys = thisKey.toLowerCase().split("to");
        final String[] splitThatKeys = thatKey.toLowerCase().split("to");

        //then get the values on both sides of the TO '2 TO 4"
        //thisKeyLHS == 2 and thisKeyRHS == 4
        final Integer thisKeyLHS = new Integer(splitThisKeys[0].trim());
        final Integer thisKeyRHS = new Integer(splitThisKeys[1].trim());

        //do same for thatKey
        final Integer thatKeyLHS = new Integer(splitThatKeys[0].trim());
        final Integer thatKeyRHS = new Integer(splitThatKeys[1].trim());

        final int compareValue = thisKeyLHS.compareTo(thatKeyLHS);
        //if they are the same then compare the RHS
        if (compareValue == 0) {
            return thisKeyRHS.compareTo(thatKeyRHS);
        }
        return compareValue;
    }
}
