package webtests

import geb.Module

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        title ==~ /BioAssay Research Database/
    }

    static content = {
        searchBox() {$("#searchString")}
        searchButton(to: HomePage) {$("#searchButton")}
        structureSearchLink {$("a", text:"Create a structure for a search")}


        //do confirmation here

//		newPersonButton(to: CreatePage) { $("a", text: "New Person") }
//		peopleTable { $("div.content table", 0) }
//		personRow { module PersonRow, personRows[it] }
//		personRows(required: false) { peopleTable.find("tbody").find("tr") }
    }
}

//class StructureSearchDialog extends Module {
//    static content = {
//        structureModalDialog { $("#modalDiv") }
//        structureRadioButton {$("form").structureSearchType = "Substructure"}
//        structureSearchButton(to: HomePage) { $("#structureSearchButton") }
//
//    }
//}

//class PersonRow extends Module {
//	static content = {
//		cell { $("td", it) }
//		cellText { cell(it).text() }
//        cellHrefText{ cell(it).find('a').text() }
//		enabled { Boolean.valueOf(cellHrefText(0)) }
//		firstName { cellText(1) }
//		lastName { cellText(2) }
//		showLink(to: ShowPage) { cell(0).find("a") }
//	}
//}