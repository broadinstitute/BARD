package bardqueryapi

/**
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
     * @return the label
     */
    String getLabel() {
        return this.label;
    }
}