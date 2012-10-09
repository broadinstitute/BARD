package webtests.pages

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        title ==~ /BioAssay Research Database/
    }

    static content = {
        searchBox() {$("#searchString")}
        searchButton(to: HomePage) {$("#searchButton")}
        structureSearchLink(to: StructureSearchPage) {$("a", text: "Draw or paste a structure for a search")}

        viewQueryCartButton { $("a", text: "View/edit") }
        visualizeButton { $("a", text: "Visualize") }
    }
}