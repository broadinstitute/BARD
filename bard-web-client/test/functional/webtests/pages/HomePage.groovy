package webtests.pages

import geb.Module

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        title.contains("BioAssay Research Database")
    }

    static content = {
        searchBox() {$("input#searchString[type='text']")}
        searchButton(to: ResultsPage) {$("#searchButton")}
        structureSearchLink(to: StructureSearchPage) {$("a", text: "Draw or paste a structure")}

        queryCart { module QueryCartModule }
    }
}

class QueryCartModule extends Module {
    static base = { $("div#summaryView") }

    static content = {
        viewQueryCartButton { $("a", text: "View/edit") }
        visualizeButton { $("a", text: "Visualize") }
        molSpreadsheetLink(to: MolSpreadsheetPage) { $("a", text: "Molecular Spreadsheet") }
        assayDefContentSummary { $("ul.unstyled.horizontal-list li", 0) }
        compoundContentSummary { $("ul.unstyled.horizontal-list li", 1) }
        projectContentSummary { $("ul.unstyled.horizontal-list li", 2) }
    }
}