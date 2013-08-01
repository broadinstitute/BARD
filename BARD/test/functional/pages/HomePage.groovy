package pages

class HomePage extends ScaffoldPage {

    static url = ""

    static at = {
        waitFor(5, 0.5){ title }
        assert title.contains("BARD: Catalog of Assay Protocols")
        return true
    }

    static content = {

    }
}