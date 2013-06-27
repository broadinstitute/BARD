package bardqueryapi

import bard.core.SearchParams
import molspreadsheet.MolecularSpreadSheetService
import bard.core.util.FilterTypes

class ExperimentDataFactoryService {
    IQueryService queryService
    MolecularSpreadSheetService molecularSpreadSheetService

    TableModel createTableModel(
            SpreadSheetInput spreadSheetInput,
            GroupByTypes groupTypes,
            List<FilterTypes> filterTypes,
            List<SearchFilter> appliedSearchFilters = null,
            SearchParams searchParams
    ) {

        if (spreadSheetInput.eids) {
            Long experimentId = spreadSheetInput.eids.get(0)
            return queryService.showExperimentalData(experimentId, groupTypes, filterTypes, searchParams)
        } else if (spreadSheetInput.cids) {
            Long compoundId = spreadSheetInput.cids.get(0)
            return queryService.createCompoundBioActivitySummaryDataTable(compoundId, groupTypes, filterTypes, appliedSearchFilters, searchParams)
        }
        return null
    }

}
