/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package webtests.pages

import grails.plugin.remotecontrol.RemoteControl
import bard.core.rest.spring.util.StructureSearchParams

class StructureSearchPage extends ScaffoldPage {

    static url = "/bardWebInterface/jsDrawEditor"

    static at = {
        assert title ==~ /BARD/

        // Make sure all of the structure search types are there
        RemoteControl remote = new RemoteControl()
        List<String> structureSearchTypes = remote { StructureSearchParams.Type.findAll() }
        structureSearchTypes.each { structureSearchType ->
            println structureSearchType
            assert $("input", name:"structureSearchType", value: "$structureSearchType")
        }

        assert $("#searchButton")

        return true
    }

    static content = {
        structureModalDialog(required: true) { $("#jsDrawEditorDiv") }
        structureRadioButton(required: true) { $("input[type='radio']").structureSearchType }
        structureSearchButton(required: true, to: ResultsPage) { $("#searchButton") }
        //do confirmation here
    }
}
