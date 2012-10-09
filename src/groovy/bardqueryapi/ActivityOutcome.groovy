package bardqueryapi

/**
 * Place holder until this appears in the JDO
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/27/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
enum ActivityOutcome {

    /**
     *
     */
    INACTIVE("Inactive",1),
    /**
     *
     */
    ACTIVE("Active",2),

    /**
     *
     */
    INCONCLUSIVE("Inconclusive",3),
    /**
     *
     */
    UNSPECIFIED("Unspecified",4),
    /**
     *
     */
    PROBE("Probe",5);

    static final private Map<Integer,ActivityOutcome> activityOutcomes;
    static{
        activityOutcomes = [:];
        for (ActivityOutcome activityOutcome : ActivityOutcome.values()) {
            activityOutcomes.put(new Integer(activityOutcome.pubChemValue),activityOutcome);
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