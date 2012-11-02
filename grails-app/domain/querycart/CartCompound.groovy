package querycart

import org.apache.commons.lang.StringUtils
import bard.core.adapter.CompoundAdapter
import bardqueryapi.IQueryService
import javax.persistence.Transient

class CartCompound extends QueryItem {

    IQueryService queryService

    static final int MAXIMUM_SMILES_FIELD_LENGTH = 1024

    String smiles

    CartCompound() {
        this.queryItemType = QueryItemType.Compound
    }

    CartCompound(Long cid) {
        this()
        List<CompoundAdapter> compoundAdapters = queryService.findCompoundsByCIDs([cid]).compoundAdapters
        assert compoundAdapters.size() <= 1, "CompoundAdapter must be unique given a CID"
        CompoundAdapter compoundAdapter = compoundAdapters ? compoundAdapters.first() : null
        if (compoundAdapter) {
            this.smiles = compoundAdapter.structureSMILES
            this.name = name = compoundAdapter.name
            this.externalId = compoundAdapter.id
            this.queryItemType = QueryItemType.Compound
        }
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

    static transients = ['queryService']

    @Override
    String toString() {
        if (StringUtils.isBlank(name) || name == 'null') {
            return "PubChem CID=${externalId}"
        }
        return name
    }
}
