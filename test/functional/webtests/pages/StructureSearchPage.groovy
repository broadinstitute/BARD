package webtests.pages

import geb.Module

class StructureSearchPage extends ScaffoldPage {

   static url = ""

    static at = {
       title ==~ /BioAssay Research Database/
    }

    static content = {
        structureModalDialog { $("#modalDiv") }
        structureRadioButton {$("form").structureSearchType = "Substructure"}
        structureSearchButton(to: StructureSearchPage) { $("#structureSearchButton") }
        //do confirmation here
    }
}

class StructureSearchDialog extends Module {
    static content = {
        structureModalDialog { $("#modalDiv") }
        structureRadioButton {$("form").structureSearchType = "Substructure"}
        structureSearchButton(to: HomePage) { $("#structureSearchButton") }

    }
}
