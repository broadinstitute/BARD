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

        return null
    }
}
