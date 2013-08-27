package querycart

import org.apache.commons.lang.StringUtils

class CartCompound extends QueryItem {

    static final int MAXIMUM_SMILES_FIELD_LENGTH = 255

    String smiles
    int numAssayActive
    int numAssayTested

    CartCompound() {
        this.queryItemType = QueryItemType.Compound
    }
    //TODO: Should only be called for testing, because validation is by passed
    CartCompound(String smiles, String name, Long compoundId, int numAssayActive, int numAssayTested) {
        this.smiles = smiles
        this.name = name
        this.externalId = compoundId
        this.numAssayActive = numAssayActive
        this.numAssayTested = numAssayTested
        this.queryItemType = QueryItemType.Compound
    }

    /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        super.beforeValidate()
        this.smiles = StringUtils.abbreviate(smiles?.trim(), MAXIMUM_SMILES_FIELD_LENGTH)
    }


    void setSmiles(String smilesString) {
        this.smiles = StringUtils.abbreviate(smilesString?.trim(), MAXIMUM_SMILES_FIELD_LENGTH)
    }



    static constraints = {
        smiles(nullable: false, maxSize: MAXIMUM_SMILES_FIELD_LENGTH)
    }

    static transients = ['queryService']

    @Override
    String toString() {
        if (StringUtils.isBlank(name) || name == 'null') {
            return "PubChem CID=${externalId}"
        }
        return name
    }
}
