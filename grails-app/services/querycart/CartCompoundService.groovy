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
            cartCompound = new CartCompound(compoundAdapter.structureSMILES, compoundAdapter.name, compoundAdapter.id)
        }

        return cartCompound
    }
}
