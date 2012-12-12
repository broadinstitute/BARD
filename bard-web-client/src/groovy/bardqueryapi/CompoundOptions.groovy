package bardqueryapi

/**
 * Various Compound Options
 *
 * This should appear in a modal when one right clicks on a compound
 */
enum CompoundOptions {

    COPY_SMILES('Copy Smiles'),
    /**
     *
     */
    ADD_ANALOGS('Search Analog Compounds') ;
    /**
     *
     */
    //SDF('Convert to SDF')


    private final String label;
    private CompoundOptions(String label){
        this.label = label;
    }
    /**
     * @return the name
     */
    String getLabel() {
        return this.label;
    }
}