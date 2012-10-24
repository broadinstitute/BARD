package querycart

import org.apache.commons.lang.StringUtils

class CartCompound extends QueryItem {

    static final int MAXIMUM_SMILES_FIELD_LENGTH = 1024

    String smiles

    CartCompound() {
        this.queryItemType = QueryItemType.Compound
    }

    CartCompound(String smiles, String name, String compoundId) {
        this(smiles, name, Long.parseLong(compoundId))
    }

    CartCompound(String smiles, String name, Long compoundId) {
        this.smiles = smiles
        this.name = name
        this.externalId = compoundId
        this.queryItemType = QueryItemType.Compound
    }

    /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        super.beforeValidate()
        this.smiles = StringUtils.abbreviate(smiles?.trim(), MAXIMUM_SMILES_FIELD_LENGTH)
    }

    static constraints = {
        smiles(nullable: false, maxSize: MAXIMUM_SMILES_FIELD_LENGTH)
    }

    @Override
    String toString() {
        if (StringUtils.isBlank(name) || name == 'null') {
            return "PubChem CID=${externalId}"
        }
        return name
    }
}
