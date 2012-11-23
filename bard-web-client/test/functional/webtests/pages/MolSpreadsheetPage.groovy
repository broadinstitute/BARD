package webtests.pages

import geb.Module

/**
 * Created with IntelliJ IDEA.
 * User: jlev
 * Date: 10/19/12
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadsheetPage extends HomePage {
    static url = "molSpreadSheet/molecularSpreadSheet"

    static at =  { title == "Molecular spreadsheet" }

    static content = {
        hidePromiscuityScoresButton { $("input#showPromiscuityScores") }
        molSpreadsheetRows(wait: true) { index ->
            moduleList MolSpreadsheetRow, $("table tbody tr", index)
        }
    }
}

class MolSpreadsheetRow extends Module {
    static content = {
//        cell { $("tr td.molSpreadsheet", it) }
        cid { $("td", it)[1].text() }
    }
}
