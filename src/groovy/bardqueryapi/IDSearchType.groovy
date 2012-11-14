package bardqueryapi

/**
 * Place holder until this appears in the JDO
 *
 * An enum of IDs that we can search for
 */
enum IDSearchType {

    ALL('All'),
    /**
     *
     */
    ADID('Assay Definition IDs'),
    /**
     *
     */
    CID('PubChem CIDs'),

    /**
     *
     */
    PID('Project IDs');

    private final String label;
    private IDSearchType(String label){
        this.label = label;
    }
    /**
     * @return the name
     */
    String getLabel() {
        return this.label;
    }
}