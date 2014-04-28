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

package bard.db.registration

import bard.db.command.BardCommand
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import grails.validation.ValidationException
import groovy.transform.InheritConstructors

import javax.servlet.http.HttpServletResponse

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class MergeAssayDefinitionController {
    MergeAssayDefinitionService mergeAssayDefinitionService

    def index() {
        redirect(action: "show")
    }

    def show() {

    }

    def confirmMerge(ConfirmMergeAssayCommand confirmMergeAssayCommand) {
       throw new RuntimeException("Deprecated. Use MoveExperimentController instead")
    }

    def mergeAssays(MergeAssayCommand mergeAssayCommand) {

        throw new RuntimeException("Deprecated. Use MoveExperimentController instead")
    }
}


@InheritConstructors
@Validateable
class MergeAssayCommand extends BardCommand {

    Long targetAssayId
    List<Long> sourceAssayIds = []
    List<String> errorMessages = []
    MergeAssayCommand() {}

    Assay getTargetAssay() {
        return Assay.findById(targetAssayId)
    }

    List<Assay> getSourceAssays() {
        List<Assay> sourceAssays = []
        for (Long id : sourceAssayIds) {
            sourceAssays.add(Assay.findById(id))
        }
        return sourceAssays
    }

    static constraints = {
        sourceAssayIds(nullable: false)
        targetAssayId(nullable: false)
    }
}
@InheritConstructors
@Validateable
class ConfirmMergeAssayCommand extends BardCommand {
    Long targetAssayId
    String sourceAssayIds
    IdType idType

    ConfirmMergeAssayCommand() {}



    static constraints = {
        targetAssayId(nullable: false)
        sourceAssayIds(nullable: false, blank: false)
        idType(nullable: false)
    }
}
