package bard.db.project

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.enums.ContextType
import bard.db.enums.ExperimentStatus
import bard.db.experiment.AsyncResultsService
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import bard.db.experiment.results.JobStatus
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.AssayDefinitionService
import bard.db.registration.EditingHelper
import bard.db.registration.MeasureTreeService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.Errors

import java.text.DateFormat
import java.text.SimpleDateFormat

@Mixin([EditingHelper])

class ExperimentController {
    static final DateFormat inlineDateFormater = new SimpleDateFormat("yyyy-MM-dd")

    ExperimentService experimentService
    AssayDefinitionService assayDefinitionService
    MeasureTreeService measureTreeService
    SpringSecurityService springSecurityService
    def permissionEvaluator
    CapPermissionService capPermissionService
    AsyncResultsService asyncResultsService

    @Secured(['isAuthenticated()'])
    def myExperiments() {
        List<Experiment> experiments = capPermissionService.findAllObjectsForRoles(Experiment)
        Set<Experiment> uniqueExperiments = new HashSet<Experiment>(experiments)
        [experiments: uniqueExperiments]
    }



    @Secured(['isAuthenticated()'])
    def edit() {
        def experiment = Experiment.get(params.id)
        JSON experimentMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experiment, false))
        [experiment: experiment, assay: experiment.assay, experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree]
    }

    /**
     * Draft is excluded as End users cannot set a status back to Draft
     * @return list of strings representing available status options
     */
    def experimentStatus() {
        List<String> sorted = []
        final Collection<ExperimentStatus> experimentStatuses = ExperimentStatus.values()
        for (ExperimentStatus experimentStatus : experimentStatuses) {
            if (experimentStatus != ExperimentStatus.DRAFT) {
                sorted.add(experimentStatus.id)
            }
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            return
        }

        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance, false))

        JSON assayMeasuresAsJsonTree = new JSON([])
        //new JSON(measureTreeService.createMeasureTree(experimentInstance.assay, false))
        boolean editable = canEdit(permissionEvaluator, springSecurityService, experimentInstance)
        boolean isAdmin = SpringSecurityUtils.ifAnyGranted('ROLE_BARD_ADMINISTRATOR')
        String owner = capPermissionService.getOwner(experimentInstance)
        [instance: experimentInstance,
                experimentOwner: owner,
                measuresAsJsonTree: measuresAsJsonTree,
                assayMeasuresAsJsonTree: assayMeasuresAsJsonTree,
                editable: editable ? 'canedit' : 'cannotedit',
                isAdmin: isAdmin]
    }

    @Secured(['isAuthenticated()'])
    def editHoldUntilDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date holdUntilDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateHoldUntilDate(inlineEditableCommand.pk, holdUntilDate)
            final String updatedDateAsString = formatter.format(experiment.holdUntilDate)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editRunFromDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date runFromDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateRunFromDate(inlineEditableCommand.pk, runFromDate)
            final String updatedDateAsString = formatter.format(experiment.runDateFrom)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editRunToDate(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            //format the date
            Date runToDate = inlineDateFormater.parse(inlineEditableCommand.value)


            experiment = experimentService.updateRunToDate(inlineEditableCommand.pk, runToDate)
            final String updatedDateAsString = formatter.format(experiment.runDateTo)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editDescription(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Experiment.DESCRIPTION_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            experiment = experimentService.updateExperimentDescription(inlineEditableCommand.pk, inputValue)

            if (experiment?.hasErrors()) {
                throw new Exception("Error while editing Experiment Description")
            }

            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.description)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editOwnerRole(InlineEditableCommand inlineEditableCommand) {
        try {
            final Role ownerRole = Role.findByDisplayName(inlineEditableCommand.value) ?: Role.findByAuthority(inlineEditableCommand.value)
            if (!ownerRole) {
                editBadUserInputErrorMessage("Could not find a registered team with name ${inlineEditableCommand.value}")
                return
            }
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            if (!BardCommand.isRoleInUsersRoleList(ownerRole)) {
                editBadUserInputErrorMessage("You do not have the permission to select team: ${inlineEditableCommand.value}")
                return
            }
            experiment = experimentService.updateOwnerRole(inlineEditableCommand.pk, ownerRole)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.owner)

        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        }
        catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editExperimentName(InlineEditableCommand inlineEditableCommand) {
        try {
            Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Experiment.DESCRIPTION_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            experiment = experimentService.updateExperimentName(inlineEditableCommand.pk, inputValue)
            if (experiment?.hasErrors()) {
                throw new Exception("Error while editing Experiment Name")
            }

            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.experimentName)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editExperimentStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final ExperimentStatus experimentStatus = ExperimentStatus.byId(inlineEditableCommand.value)
            final Experiment experiment = Experiment.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(experiment.version, Experiment.class)
            if (message) {
                conflictMessage(message)
                return
            }
            experiment = experimentService.updateExperimentStatus(inlineEditableCommand.pk, experimentStatus)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, null, experiment.lastUpdated, experiment.experimentStatus.id)

        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    @Secured(['isAuthenticated()'])
    def editContext(Long id, String groupBySection) {
        Experiment instance = Experiment.get(id)
        if (!instance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), id])
            return

        }
        AbstractContextOwner.ContextGroup contextGroup = instance.groupBySection(ContextType.byId(groupBySection?.decodeURL()))
        render view: '../project/editContext', model: [instance: instance, contexts: [contextGroup]]
    }

    @Secured(['isAuthenticated()'])
    def create(ExperimentCommand experimentCommand) {
        if (experimentCommand.fromCreatePage) { //if we are coming from the create page then there is an error and we display that to users
            return [experimentCommand: experimentCommand]
        } else {
            //if there are errors clear them
            experimentCommand.clearErrors()
            return [experimentCommand: experimentCommand]
        }
        [experimentCommand: new ExperimentCommand(modifiedBy: springSecurityService.principal?.username)]

    }

    @Secured(['isAuthenticated()'])
    def save(ExperimentCommand experimentCommand) {
        if (!experimentCommand.validate()) {
            create(experimentCommand)
            return
        }

        Experiment experiment = experimentCommand.createNewExperiment()
        if (!experiment) {
            create(experimentCommand)
            return
        }
       redirect(action: "show", id: experiment.id)
    }

    @Secured(['isAuthenticated()'])
    def update() {
        def experiment = Experiment.get(params.id)
        try {
            experimentService.updateMeasures(experiment.id, JSON.parse(params.experimentTree))
        } catch (AccessDeniedException ade) {
            log.error("Access denied on update measure", ade)
            render accessDeniedErrorMessage()
            return
        }
        if (!experiment.save(flush: true)) {
            JSON experimentMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experiment, false))
            render view: "edit", model: [experiment: experiment, assay: experiment.assay, experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree]
        } else {
            redirect(action: "show", id: experiment.id)
        }
    }

    @Secured(['isAuthenticated()'])
    def reloadResults(Long id) {
        String jobKey = asyncResultsService.createJobKey()
        String link = createLink(action: 'viewLoadStatus', params: [experimentId: id, jobKey: jobKey])
        asyncResultsService.doReloadResultsAsync(id, jobKey, link)
        redirect(action: "viewLoadStatus", params: [jobKey: jobKey, experimentId: id])
    }

    @Secured(['isAuthenticated()'])
    def viewLoadStatus(String jobKey, String experimentId) {
        JobStatus status = asyncResultsService.getStatus(jobKey)
        [experimentId: experimentId, status: status]
    }
}
@InheritConstructors
@Validateable
class ExperimentCommand extends BardCommand {
    Long assayId
    String experimentName
    String description
    String ownerRole
    Date dateCreated = new Date()
    String runDateFrom
    String runDateTo
    SpringSecurityService springSecurityService
    String modifiedBy
    boolean fromCreatePage = false //If true, we can validate the inputs, otherwise we are coming from some other page and we do not require validations.
    // Note that the assayId must not be blank regardless of which page you are coming from
    public Assay getAssay() {
        return Assay.get(this.assayId)
    }

    public static final List<String> PROPS_FROM_CMD_TO_DOMAIN = ['experimentName', 'description', 'dateCreated'].asImmutable()

    static constraints = {
        importFrom(Experiment, exclude: ['assay', 'holdUntilDate', 'runDateTo', 'runDateFrom', 'readyForExtraction', 'lastUpdated', 'ownerRole', 'experimentStatus'])
        assayId(nullable: false, validator: { value, command, err ->
            if (!Assay.get(value)) {
                err.rejectValue('assayId', "message.code", "Could not find ADID : ${value}");
            }
        })
        ownerRole(nullable: false, blank: false, validator: { value, command, err ->
            Role role = Role.findByAuthority(value)
            if (!BardCommand.isRoleInUsersRoleList(role)) {
                err.rejectValue('ownerRole', "message.code", "You do not have the privileges to create Assays for this team : ${role.displayName}");
            }
        })
        runDateTo(nullable: true, blank: true, validator: { String field, ExperimentCommand instance, Errors errors ->
            if (field?.trim()) {
                try {
                    Date runTo = Experiment.dateFormat.parse(field)
                    if (instance.runDateFrom) {
                        final Date runFrom = Experiment.dateFormat.parse(instance.runDateFrom)
                        //Checks that Run Date To is not before Run From Date
                        if (runFrom.after(runTo)) {
                            errors.rejectValue(
                                    'runDateTo',
                                    'experiment.holdUntilDate.incorrecRunDateFrom',
                                    'Run Date To cannot be before Run From Date')
                        }
                    }
                } catch (Exception ee) {
                    errors.rejectValue(
                            'runDateTo',
                            'experimentCommand.date.invalid')
                }
            }
        })
        runDateFrom(nullable: true, blank: true, validator: { String field, ExperimentCommand instance, Errors errors ->
            if (field?.trim()) {
                try {
                    Date runFrom = Experiment.dateFormat.parse(field)
                    if (instance.runDateFrom) {
                        final Date runTo = Experiment.dateFormat.parse(instance.runDateFrom)
                        //Checks that Run Date To is not before Run From Date
                        if (runFrom.after(runTo)) {
                            errors.rejectValue(
                                    'runDateFrom',
                                   'experiment.holdUntilDate.incorrecRunDateFrom',
                                            'Run Date To cannot be before Run From Date')
                        }
                    }
                } catch (Exception ee) {
                    errors.rejectValue(
                            'runDateFrom',
                            'experimentCommand.date.invalid')
                }
            }
        })
    }

    ExperimentCommand() {}



    Experiment createNewExperiment() {
        Experiment experimentToReturn = null
        if (validate()) {
            Experiment tempExperiment = new Experiment()
            copyFromCmdToDomain(tempExperiment)
            if (attemptSave(tempExperiment)) {
                experimentToReturn = tempExperiment
            }
        }
        return experimentToReturn
    }


    void copyFromCmdToDomain(Experiment experiment) {
        experiment.modifiedBy = springSecurityService.principal?.username
        experiment.runDateFrom = runDateFrom ? Experiment.dateFormat.parse(runDateFrom) : null
        experiment.runDateTo = runDateTo ? Experiment.dateFormat.parse(runDateTo) : null
        experiment.ownerRole = Role.findByAuthority(ownerRole)
        experiment.assay = getAssay()
        for (String field in PROPS_FROM_CMD_TO_DOMAIN) {
            experiment[(field)] = this[(field)]
        }
    }
}

