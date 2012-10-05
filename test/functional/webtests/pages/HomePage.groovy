package webtests.pages

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        title ==~ /BioAssay Research Database/
    }

    static content = {
        searchBox() {$("#searchString")}
        searchButton(to: HomePage) {$("#searchButton")}
        structureSearchLink {$("a", text:"Draw or paste a structure for a search")}


        //do confirmation here
    }
}