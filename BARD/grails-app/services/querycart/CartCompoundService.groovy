package querycart

import bardqueryapi.IQueryService
import bard.core.adapter.CompoundAdapter

class CartCompoundService {

    IQueryService queryService

    CartCompound createCartCompoundFromCID(Long cid) {
        CartCompound cartCompound;

        List<CompoundAdapter> compoundAdapters = queryService.findCompoundsByCIDs([cid]).compoundAdapters
        assert compoundAdapters.size() <= 1, "CompoundAdapter must be unique given a CID"
        CompoundAdapter compoundAdapter = compoundAdapters ? compoundAdapters.first() : null
        if (compoundAdapter) {
            cartCompound = new CartCompound()
            cartCompound.smiles = compoundAdapter.structureSMILES
            cartCompound.name = compoundAdapter.name
            cartCompound.externalId = compoundAdapter.id
            cartCompound.numAssayActive = compoundAdapter.numberOfActiveAssays
            cartCompound.numAssayTested = compoundAdapter.numberOfAssays
        }

        return cartCompound
    }
}
