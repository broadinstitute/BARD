package webtests.pages

import geb.Module

class StructureSearchPage extends ScaffoldPage {

   static url = ""

    static at = {
       assert title ==~ /BioAssay Research Database/
       List<String> structureTypes = ["Substructure", "Superstructure", "Exact", "Similarity"]
       assert $("input", name: "structureSearchType").collect{
           structureTypes.contains(it.value())
       }
       assert $("#structureSearchButton")

       return true
    }

    static content = {
        structureModalDialog(required: true) { $("#modalDiv") }
        structureRadioButton(required: true)  { $("form").structureSearchType }
        closeButton(required: true, to: HomePage) { $("a#closeButton.btn") }
        structureSearchButton(required: true, to: ResultsPage) { $("#structureSearchButton") }
        //do confirmation here
    }
}
