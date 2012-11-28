package webtests.pages

import grails.plugin.remotecontrol.RemoteControl
import bard.core.rest.spring.util.StructureSearchParams

class StructureSearchPage extends ScaffoldPage {

    static url = ""

    static at = {
        assert title ==~ /BioAssay Research Database/

        // Make sure all of the structure search types are there
        RemoteControl remote = new RemoteControl()
        List<String> structureSearchTypes = remote { StructureSearchParams.Type.findAll() }
        structureSearchTypes.each { structureSearchType ->
            println structureSearchType
            assert $("input", name:"structureSearchType", value: "$structureSearchType")
        }

        assert $("#structureSearchButton")

        return true
    }

    static content = {
        structureModalDialog(required: true) { $("#modalDiv") }
        structureRadioButton(required: true) { $("form").structureSearchType }
        closeButton(required: true, to: HomePage) { $("a#closeButton.btn") }
        structureSearchButton(required: true, to: ResultsPage) { $("#structureSearchButton") }
        //do confirmation here
    }
}
