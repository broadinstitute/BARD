package bardqueryapi

/**
 * Place holder until this appears in the JDO
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/27/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ActivityOutcome {

    /**
     *
     */
    INACTIVE("inactive",1),
    /**
     *
     */
    ACTIVE("active",2),

    /**
     *
     */
    INCONCLUSIVE("inconclusive",3),
    /**
     *
     */
    UNSPECIFIED("unspecified",4),
    /**
     *
     */
    PROBE("probe",5);

    static final private Map<Integer,ActivityOutcome> activityOutcomes;
    static{
        activityOutcomes = new HashMap<Integer, ActivityOutcome>();
        for (ActivityOutcome activityOutcome : ActivityOutcome.values()) {
            activityOutcomes.put(new Integer(activityOutcome.getPubChemValue()),activityOutcome);
        }
    }
    private final String label;
    private final int pubChemValue;
    private ActivityOutcome(String label, int pubChemValue){
        this.label = label;
        this.pubChemValue = pubChemValue;
    }
    /**
     * @return the name
     */
    public String getLabel() {
        return this.label;
    }
    /**
     * @return the pub chem value in the PC schema
     */
    public int getPubChemValue() {
        return this.pubChemValue;
    }
    /**
     * @param outComeValue
     * @return {@link ActivityOutcome}
     */
    public static ActivityOutcome  findActivityOutcome(int outComeValue){

        return activityOutcomes.get(new Integer(outComeValue));
    }
}