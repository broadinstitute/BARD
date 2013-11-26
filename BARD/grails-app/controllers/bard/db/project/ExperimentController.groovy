package bard.db.project

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.enums.AssayStatus
import bard.db.enums.ContextType
import bard.db.enums.ExperimentStatus
import bard.db.enums.HierarchyType
import bard.db.experiment.*
import bard.db.experiment.results.JobStatus
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.registration.*
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.Errors

import javax.servlet.http.HttpServletResponse
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
        List<Experiment> experiments = capPermissionService.findAllByOwnerRolesAndClass(Experiment)
        Set<Experiment> uniqueExperiments = new HashSet<Experiment>(experiments)
        [experiments: uniqueExperiments]
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

    def loadExperimentMeasuresAsJSON(Long id) {
        //if user is logged in pass in the boolean
        if (params.id && StringUtils.isNumeric(params.id.toString())) {
            final Object principal = springSecurityService?.principal
            String loggedInUser = null
            Experiment experimentInstance = Experiment.get(id)
            if (experimentInstance) {
                if (principal instanceof String) {
                    loggedInUser = null
                } else {
                    //only people with permission can see this
                    boolean editable = canEdit(permissionEvaluator, springSecurityService, experimentInstance)
                    if (editable) {
                        loggedInUser = principal?.username
                    }
                }

                JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance, loggedInUser))
                render text: measuresAsJsonTree, contentType: 'text/json', template: null
                return
            }
        }
        render text: "", contentType: 'text/json', template: null
    }

    def show() {

        if (params.id && StringUtils.isNumeric(params.id.toString())) {
            final Experiment experiment = Experiment.get(params.id)
            if (!experiment) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
                return
            }

            final Set<Long> contextIds = new HashSet<Long>()
            //load the assay context items for this measure
            for (ExperimentMeasure experimentMeasure : experiment.experimentMeasures) {
                //load the assay context items for this measure
                final Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = experimentMeasure.assayContextExperimentMeasures
                for (AssayContextExperimentMeasure assayContextExperimentMeasure : assayContextExperimentMeasures) {

                    contextIds.add(assayContextExperimentMeasure.assayContext.id);
                }
            }

            boolean editable = canEdit(permissionEvaluator, springSecurityService, experiment)
            boolean isAdmin = SpringSecurityUtils.ifAnyGranted('ROLE_BARD_ADMINISTRATOR')
            String owner = capPermissionService.getOwner(experiment)

            return [
                    instance: experiment,
                    contextIds: contextIds,
                    experimentOwner: owner,
                    editable: editable ? 'canedit' : 'cannotedit',
                    isAdmin: isAdmin
            ]
        } else {

            flash.message = "Experiment ID is required!"
            return

        }
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
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, updatedDateAsString)
        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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

            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, experiment.description)

        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, experiment.owner)

        }
        catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        }
        catch (Exception ee) {
            log.error(ee,ee)
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

            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, experiment.experimentName)

        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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


            final Assay assay = experiment.assay
            if (experimentStatus == ExperimentStatus.APPROVED) {//if experiment status is approved then assay status must be approved
                if (assay.assayStatus != AssayStatus.APPROVED) {
                    String errorMessage = "The assay definition (ADID:${assay.id} for this experiment must be marked Approved before this experiment can be marked Approved."
                    render(status: HttpServletResponse.SC_BAD_REQUEST, text:
                            errorMessage, contentType: 'text/plain', template: null)
                    return
                }
                if (!experiment.measuresHaveAtLeastOnePriorityElement()) {
                    render(status: HttpServletResponse.SC_BAD_REQUEST, text: "You must designate at least one result type as a priority element before this experiment can be marked as approved.",
                            contentType: 'text/plain', template: null)
                    return
                }
            }

            experiment = experimentService.updateExperimentStatus(inlineEditableCommand.pk, experimentStatus)
            generateAndRenderJSONResponse(experiment.version, experiment.modifiedBy, experiment.lastUpdated, experiment.experimentStatus.id)

        } catch (AccessDeniedException ade) {
            log.error(ade,ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee,ee)
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
        if (!experimentCommand.fromCreatePage) { //if we are not coming from the create page then there is an error and we display that to users
            experimentCommand.clearErrors()
        }

        render view: "create", model: [experimentCommand: experimentCommand]
    }

    @Secured(['isAuthenticated()'])
    def editMeasure() {
        ExperimentMeasure experimentMeasure = ExperimentMeasure.get(params.measureId)
        List<Long> contextIds = []
        //load the assay context items for this measure
        final Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = experimentMeasure.assayContextExperimentMeasures
        for (AssayContextExperimentMeasure assayContextExperimentMeasure : assayContextExperimentMeasures) {
            contextIds.add(assayContextExperimentMeasure.assayContext.id);
        }

        ResultTypeCommand resultTypeCommand =
            new ResultTypeCommand(fromCreatePage: true,
                    experimentMeasureId: experimentMeasure.id,
                    priorityElement: experimentMeasure.priorityElement,
                    experimentId: new Long(params.experimentId),
                    statsModifierId: experimentMeasure.statsModifier?.id?.toString(),
                    parentExperimentMeasureId: experimentMeasure.parent?.id?.toString(),
                    parentChildRelationship: experimentMeasure.parentChildRelationship?.id,
                    resultTypeId: experimentMeasure.resultType.id,
                    contextIds: contextIds,

            )
        Map dataMap = resultTypeCommand.createDataResponseMap()
        render view: "createResultTypes", model: dataMap
    }

    @Secured(['isAuthenticated()'])
    def deleteMeasure(Long measureId, Long experimentId) {
        ExperimentMeasure experimentMeasure = ExperimentMeasure.get(measureId)
        experimentService.deleteExperimentMeasure(experimentId, experimentMeasure)
        render text: [link: experimentId] as JSON, contentType: 'text/json', template: null
    }

    @Secured(['isAuthenticated()'])
    def addResultTypes(ResultTypeCommand resultTypeCommand) {

        Map dataMap = resultTypeCommand.createDataResponseMap()
        render view: "createResultTypes", model: dataMap
    }

    @Secured(['isAuthenticated()'])
    def addDoseResultTypes(DoseResultTypeCommand doseResultTypeCommand) {

        Map dataMap = doseResultTypeCommand.createDataResponseMap()
        render view: "createDoseResultTypes", model: dataMap
    }

    @Secured(['isAuthenticated()'])
    def saveDoseResultType(DoseResultTypeCommand doseResultTypeCommand) {

        if (doseResultTypeCommand.hasErrors()) {//If we are not coming from create Page then ignore all errors
            addDoseResultTypes(doseResultTypeCommand)
            return
        }
        final List<ExperimentMeasure> experimentMeasures = doseResultTypeCommand.createExperimentMeasures()
        if (!experimentMeasures) {
            addDoseResultTypes(doseResultTypeCommand)
            return
        }
        redirect(action: "show", id: doseResultTypeCommand.experimentId, fragment: "result-type-header")
    }

    @Secured(['isAuthenticated()'])
    def saveResultType(ResultTypeCommand resultTypeCommand) {


        if (resultTypeCommand.hasErrors()) {//If we are not coming from create Page then ignore all errors
            if (resultTypeCommand.experimentMeasureId) {
                editMeasure()
            } else {
                addResultTypes(resultTypeCommand)
            }
            return
        }
        if (resultTypeCommand.experimentMeasureId) { //we are editing
            ExperimentMeasure experimentMeasure = resultTypeCommand.updateExperimentMeasure()
            if (!experimentMeasure) {
                editMeasure()
                return
            }

        } else {
            ExperimentMeasure experimentMeasure = resultTypeCommand.createNewExperimentMeasure()
            if (!experimentMeasure) {
                addResultTypes(resultTypeCommand)
                return
            }
        }
        redirect(action: "show", id: resultTypeCommand.experimentId, fragment: "result-type-header")
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
class DoseResultTypeCommand extends AbstractResultTypeCommand {

    static final String SCREENING_CONCENTRATION_PATH = "project management> experiment> result detail> screening concentration"
    Long concentrationResultTypeId
    Long responseResultTypeId
    OntologyDataAccessService ontologyDataAccessService
    String parentChildRelationship = HierarchyType.CALCULATED_FROM.id

    //TODO: Read this from the result tree in future when we decide to deal with the other "concentration-response curve" values like cMax, area-under-curve etc
    static List<String> CURVE_FIT_LABELS = ["Hill sinf", "Hill s0", "Hill coefficient"]



    Element getConcentrationResultType() {
        return Element.get(concentrationResultTypeId)
    }

    Element getResponseResultType() {
        return Element.get(responseResultTypeId)
    }



    static constraints = {
        concentrationResultTypeId(nullable: false, validator: { value, command, err ->
            if (!Element.get(value)) {
                err.rejectValue('concentrationResultTypeId', "command.resultTypeId.notexists");
            }
        })
        responseResultTypeId(nullable: false, validator: { value, command, err ->
            if (!Element.get(value)) {
                err.rejectValue('responseResultTypeId', "command.resultTypeId.notexist");
            }
        })
    }

    DoseResultTypeCommand() {
    }


    List<ExperimentMeasure> createExperimentMeasures() {
        final List<ExperimentMeasure> experimentMeasures = []


        final Experiment experiment = getExperiment()
        final Element concentrationResultType = getConcentrationResultType()
        final ExperimentMeasure parentExperimentMeasure = getParentExperimentMeasure()
        final Element responseResultType = getResponseResultType()
        final Element statsModifier = getStatsModifier()

        HierarchyType hierarchyType = parentExperimentMeasure ? HierarchyType.byId(this.parentChildRelationship) : null

        final ExperimentMeasure rootExperimentMeasure =
            createExperimentMeasure(experiment, concentrationResultType, true,
                    parentExperimentMeasure,
                    hierarchyType, null).save(flush: true)
        experimentMeasures.add(rootExperimentMeasure)


        hierarchyType = HierarchyType.CALCULATED_FROM
        final ExperimentMeasure responseExperimentMeasure = createExperimentMeasure(experiment, responseResultType,
                false, rootExperimentMeasure, hierarchyType, statsModifier).save(flush: true)
        experimentMeasures.add(responseExperimentMeasure)

        Element curveFitModifier = null
        CURVE_FIT_LABELS.each { String label ->
            final ExperimentMeasure curveFitMeasure = createCurveFitExperimentMeasure(experiment, rootExperimentMeasure, label, curveFitModifier).save(flush: true)
            experimentMeasures.add(curveFitMeasure)
        }

        associateAssayContext(responseExperimentMeasure)

        return experimentMeasures
    }

    /**
     * Look through all the existing context items, and find one with an attributeElement of 'Screening Concentration"
     * and is the sole context item in that particular context.
     *
     * i.e. The context item should not have any siblings in the context
     *
     * @param assayContextItems
     * @return
     */
    AssayContext findScreeningConcentrationContextItemWithNoSiblings(final List<AssayContextItem> assayContextItems) {

        //Lets find all the descriptors with the given path
        final List<Descriptor> listOfDescriptors = ontologyDataAccessService.getDescriptorsForAttributes(SCREENING_CONCENTRATION_PATH)

        //collect all the screening concentration elements
        List<Element> screeningConcentrationElements = listOfDescriptors.collect { descriptor ->
            descriptor.element
        }
        //Now loop through all the context items looking for one with a screening concentration
        for (AssayContextItem assayContextItem : assayContextItems) {

            //We want to find a screening context item with no siblings
            if (screeningConcentrationElements.contains(assayContextItem.attributeElement)) {//if there is a screening concentration item
                final AssayContext assayContext = assayContextItem.assayContext
                if (assayContext.assayContextItems.size() == 1) {//and it has no siblings
                    return assayContext
                }
            }
        }
        return null
    }
    /*
  Associate the result type to a screening concentration.
 i.e Find an assay context with only one context item, whose attributeElement is one of the screening concentration
 elements.
 If you don't find any then create one.
  */

    void associateAssayContext(ExperimentMeasure experimentMeasure) {


        final List<AssayContextItem> assayContextItems = experiment.assay.assayContextItems
        AssayContext assayContextToAssociateToMeasure = findScreeningConcentrationContextItemWithNoSiblings(assayContextItems)
        //If we don't find a context, we create one
        if (!assayContextToAssociateToMeasure) {
            assayContextToAssociateToMeasure = createAssayContextForResultType(experiment.assay, experimentMeasure?.resultType?.label)
        }

        AssayContextExperimentMeasure assayContextExperimentMeasure = new AssayContextExperimentMeasure()
        assayContextToAssociateToMeasure.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)
        experimentMeasure.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)
        assayContextExperimentMeasure.save(flush: true)

    }

    AssayContext createAssayContextForResultType(Assay assay, String resultTypeLabel) {
        AssayContext assayContext = new AssayContext()
        assayContext.contextName = "annotations for ${resultTypeLabel}";
        assayContext.contextType = ContextType.EXPERIMENT
        AssayContextItem item = new AssayContextItem();
        item.attributeType = AttributeType.Free
        item.attributeElement = Element.get(PubchemReformatService.SCREENING_CONCENTRATION_ID)
        assayContext.addToAssayContextItems(item)
        item.valueDisplay = item.deriveDisplayValue()

        assay.addToAssayContexts(assayContext)
        assayContext.save(flush: true)
        return assayContext.refresh()
    }

    ExperimentMeasure createCurveFitExperimentMeasure(Experiment experiment, ExperimentMeasure parentExperimentMeasure,
                                                      String curveFitElementLabel, Element statsModifier) {
        return createExperimentMeasure(experiment, Element.findByLabel(curveFitElementLabel), false, parentExperimentMeasure, HierarchyType.SUPPORTED_BY, statsModifier)
    }

}
@InheritConstructors
@Validateable
class AbstractResultTypeCommand extends BardCommand {
    ExperimentService experimentService
    boolean fromCreatePage = false
    Long experimentId
    String statsModifierId
    String parentExperimentMeasureId
    String parentChildRelationship
    List<Long> contextIds
    Long experimentMeasureId //This should be present when we are coming from edit page

    boolean priorityElement = false


    ExperimentMeasure createExperimentMeasure(Experiment experiment,
                                              Element resultType,
                                              boolean priorityElement,
                                              ExperimentMeasure parentExperimentMeasure,
                                              HierarchyType hierarchyType,
                                              Element statsModifier) {
        return this.experimentService.createNewExperimentMeasure(experiment.id, parentExperimentMeasure, resultType, statsModifier, this.contextIds,
                hierarchyType, priorityElement)
    }
    /**
     *
     */
    Map createDataResponseMap() {
        if (!this.fromCreatePage) {//If we are not coming from create Page then ignore all errors
            this.clearErrors()
        }
        Experiment experiment = getExperiment()
        List<ExperimentMeasure> currentExperimentMeasures = []
        if (experiment) {
            currentExperimentMeasures = experiment.experimentMeasures as List<ExperimentMeasure>
            currentExperimentMeasures.sort { ExperimentMeasure experimentMeasure1, ExperimentMeasure experimentMeasure2 ->
                experimentMeasure1.resultType.label.compareTo(experimentMeasure2.resultType.label)
            }
        }
        return [resultTypeCommand: this, currentExperimentMeasures: currentExperimentMeasures, canEditMeasures: experiment?.experimentFiles ? false : true]

    }

    Element getStatsModifier() {
        if (statsModifierId) {
            return Element.get(statsModifierId.toLong())
        }
    }

    ExperimentMeasure getParentExperimentMeasure() {
        if (parentExperimentMeasureId) {
            return ExperimentMeasure.get(parentExperimentMeasureId.toLong())
        }
    }

    ExperimentMeasure getExperimentMeasure() {
        if (experimentMeasureId) {
            return ExperimentMeasure.get(experimentMeasureId)
        }
    }

    Experiment getExperiment() {
        final Experiment experiment = Experiment.get(experimentId)
        return experiment
    }

    static constraints = {
        experimentMeasureId(nullable: true, validator: { value, command, err ->
            //should be present when editing
            if (value) {
                if (!ExperimentMeasure.get(value)) {
                    err.rejectValue('experimentMeasureId', "command.experimentMeasure.id.not.found",
                            ["${value}"] as Object[],
                            "Could not find Experiment Measure: ${value}");
                }
            }
        })
        experimentId(nullable: false, validator: { value, command, err ->
            if (!Experiment.get(value.toLong())) {
                err.rejectValue('experimentId', "associateExperimentsCommand.experiment.id.not.found",
                        ["${value}"] as Object[],
                        "Could not find Experiment with EID : ${value}");
            }
        })
        statsModifierId(nullable: true, blank: true, validator: { value, command, err ->
            if (value) {
                if (!Element.get(value.toLong())) {
                    err.rejectValue('statsModifierId', "command.statsModifier.id.not.found",
                            ["${value}"] as Object[],
                            "Could not find Stats Modifier with ID : ${value}");
                }
            }
        })
        contextIds(nullable: true, validator: { value, command, err ->
            if (value) {
                for (Long contextId : value) {
                    if (!AssayContext.get(contextId)) {
                        err.rejectValue('contextIds', "command.context.id.not.found",
                                ["${value}"] as Object[],
                                "Could not find Assay Context with ID : ${contextId}");
                    }
                }
            }
        })
        parentExperimentMeasureId(nullable: true, blank: true, validator: { value, command, err ->
            if (value) {
                if (!ExperimentMeasure.get(value.toLong())) {
                    err.rejectValue('parentExperimentMeasureId', "command.parentMeasure.id.not.found",
                            ["${value}"] as Object[],
                            "Could not find Parent Measure with ID : ${value}");
                }
            }
        })
    }

}
@InheritConstructors
@Validateable
class ResultTypeCommand extends AbstractResultTypeCommand {

    Long resultTypeId

    Element getResultType() {
        return Element.get(resultTypeId)
    }


    static constraints = {

        resultTypeId(nullable: true, validator: { value, command, err ->
            if (command?.experiment?.experimentFiles) { //this result type value can be null
                //user can only update priority elements
                return
            } else if (!value) {
                err.rejectValue('resultTypeId', "command.resultTypeId.nullable");
            } else if (!Element.get(value)) {
                err.rejectValue('resultTypeId', "command.resultTypeId.notexists");
            }
        })

        parentChildRelationship(nullable: true, blank: true, validator: { value, command, err ->
            if (value) { //Should only exist if there is a parent
                if (!command.parentExperimentMeasureId) {
                    err.rejectValue('parentChildRelationship', "command.parentChildRelationship.no.hierarchy");
                }
            }
        })

        parentExperimentMeasureId(nullable: true, blank: true, validator: { value, command, err ->
            if (value) { //Should only exist if there is a parent
                if (!command.parentChildRelationship) {
                    err.rejectValue('parentExperimentMeasureId', "command.parentMeasure.no.hierarchy");
                }
            }
        })
    }

    ResultTypeCommand() {}

    ExperimentMeasure updateExperimentMeasure() {
        if (validate()) {
            ExperimentMeasure experimentMeasureToReturn = getExperimentMeasure()
            experimentMeasureToReturn.priorityElement = this.priorityElement
            if (!getExperiment().experimentFiles) {//if this experiment has no results uploaded then you can edit everything else
                HierarchyType hierarchyType = null
                if (this.parentChildRelationship) {
                    hierarchyType = HierarchyType.byId(this.parentChildRelationship)
                }

                experimentMeasureToReturn.parentChildRelationship = hierarchyType
                experimentMeasureToReturn.statsModifier = getStatsModifier()
                experimentMeasureToReturn.parent = getParentExperimentMeasure()
                experimentMeasureToReturn.resultType = this.getResultType()
            }
            return experimentService.updateExperimentMeasure(this.experimentId, experimentMeasureToReturn, this.contextIds)
        }
        return null
    }

    ExperimentMeasure createNewExperimentMeasure() {
        ExperimentMeasure experimentMeasureToReturn = null
        if (validate()) {

            final ExperimentMeasure parentExperimentMeasure = getParentExperimentMeasure()

            HierarchyType hierarchyType = null
            if (this.parentChildRelationship) {
                hierarchyType = HierarchyType.byId(this.parentChildRelationship)
            }
            experimentMeasureToReturn =
                createExperimentMeasure(experiment, getResultType(),
                        this.priorityElement, parentExperimentMeasure, hierarchyType, getStatsModifier())
        }
        return experimentMeasureToReturn
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
                err.rejectValue('assayId', "command.assay.id.not.found",
                        ["${value}"] as Object[],
                        "Could not find ADID : ${value}");
            }
        })
        ownerRole(nullable: false, blank: false, validator: { value, command, err ->
            Role role = Role.findByAuthority(value)
            if (!BardCommand.isRoleInUsersRoleList(role)) {
                err.rejectValue('ownerRole',
                        "assay.no.create.privileges",
                        ["${role.displayName}"] as Object[],
                        "You do not have the privileges to create Assays for this team : ${role.displayName}");
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

