package webtests.pages

import geb.Module

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        title ==~ /BioAssay Research Database/
    }

    static content = {
        searchBox() {$("#searchString")}
        searchButton(to: ResultsPage) {$("#searchButton")}
        structureSearchLink(to: StructureSearchPage) {$("a", text: "Draw or paste a structure for a search")}

        queryCart { module QueryCartModule }
    }
}

class QueryCartModule extends Module {
    static base = { $("div#summaryView") }

    static content = {
        viewQueryCartButton { $("a", text: "View/edit") }
        visualizeButton { $("a", text: "Visualize") }
        molSpreadsheetLink(to: MolSpreadsheetPage) { $("a", text: "Molecular Spreadsheet") }
        contentSummary { $("ul.unstyled.horizontal-list") }
    }
}