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
