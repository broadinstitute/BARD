package bardqueryapi

import bard.core.SearchParams
import molspreadsheet.MolecularSpreadSheetService
import bard.core.util.FilterTypes

class ExperimentDataFactoryService {
    QueryService queryService
    MolecularSpreadSheetService molecularSpreadSheetService

    WebQueryTableModel createTableModel(
            SpreadSheetInput spreadSheetInput,
            GroupByTypes groupTypes,
            List<FilterTypes> filterTypes,
            SearchParams searchParams
    ) {

        if (spreadSheetInput.eids) {
            Long experimentId = spreadSheetInput.eids.get(0)
            return queryService.showExperimentalData(experimentId, groupTypes, filterTypes, searchParams)
        }
        else if (spreadSheetInput.cids) {
            Long compoundId = spreadSheetInput.cids.get(0)
            return queryService.showCompoundBioActivitySummaryData(compoundId, groupTypes, filterTypes, searchParams)
        }
        return null
    }

}
