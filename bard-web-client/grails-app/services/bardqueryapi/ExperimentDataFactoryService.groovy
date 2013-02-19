package bardqueryapi

import bard.core.SearchParams
import molspreadsheet.MolecularSpreadSheetService

class ExperimentDataFactoryService {
    QueryService queryService
    MolecularSpreadSheetService molecularSpreadSheetService

    WebQueryTableModel createTableModel(
            SpreadSheetInput spreadSheetInput,
            GroupTypes groupTypes,
            List<FilterTypes> filterTypes,
            SearchParams searchParams
    ) {

        if (spreadSheetInput.eids) {
            Long experimentId = spreadSheetInput.eids.get(0)
            return queryService.showExperimentalData(experimentId,groupTypes,filterTypes,searchParams)
         }
        return null
    }

}
