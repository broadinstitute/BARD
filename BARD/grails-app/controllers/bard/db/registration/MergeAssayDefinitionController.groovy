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
        try {
            mergeAssayDefinitionService.validateConfirmMergeInputs(confirmMergeAssayCommand.targetAssayId, confirmMergeAssayCommand.sourceAssayIds, confirmMergeAssayCommand.assayIdType)

            final List<Long> assayIdsToMerge = mergeAssayDefinitionService.convertStringToIdList(confirmMergeAssayCommand.sourceAssayIds)

            final Assay assayToMergeInto = mergeAssayDefinitionService.convertIdToAssayDefinition(confirmMergeAssayCommand.assayIdType, confirmMergeAssayCommand.targetAssayId)

            if (!assayToMergeInto) {
                throw new RuntimeException("Could not find assay with ${confirmMergeAssayCommand.assayIdType} ${confirmMergeAssayCommand.targetAssayId}")
            }
            final List<Long> assaysToMerge = mergeAssayDefinitionService.convertAssaysToMerge(
                    assayIdsToMerge, confirmMergeAssayCommand.assayIdType, assayToMergeInto)


            MergeAssayCommand mergeAssayCommand = new MergeAssayCommand(targetAssayId: assayToMergeInto.id, sourceAssayIds: assaysToMerge)
            final List<String> errorMessages =
                mergeAssayDefinitionService.validateAllContextItems(mergeAssayCommand.targetAssay, mergeAssayCommand.sourceAssays)
            mergeAssayCommand.errorMessages = errorMessages
            //validate context items
            render(status: HttpServletResponse.SC_OK, template: "confirmMerge", action: "show", model: [mergeAssayCommand: mergeAssayCommand])


        } catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "mergeError", model: [message: ee.message])
        }
    }

    def mergeAssays(MergeAssayCommand mergeAssayCommand) {

        try {
            Assay mergedAssay =
                mergeAssayDefinitionService.mergeAllAssays(mergeAssayCommand.targetAssay,
                        mergeAssayCommand.sourceAssays)
            render(status: HttpServletResponse.SC_OK, template: "mergeAssaySuccess", model: [mergedAssay: mergedAssay, oldAssays: mergeAssayCommand.sourceAssays])
        }
        catch (ValidationException validationError) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "mergeValidationError", model: [validationError: validationError])

        }
        catch (Exception ee) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, template: "mergeValidationError", model: [errorMessage: ee.message])
        }
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
    AssayIdType assayIdType

    ConfirmMergeAssayCommand() {}



    static constraints = {
        targetAssayId(nullable: false)
        sourceAssayIds(nullable: false, blank: false)
        assayIdType(nullable: false)
    }
}
