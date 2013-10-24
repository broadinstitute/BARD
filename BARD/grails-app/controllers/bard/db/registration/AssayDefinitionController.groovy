package bard.db.registration

import acl.CapPermissionService
import bard.db.ContextService
import bard.db.command.BardCommand
import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ContextType
import bard.db.enums.HierarchyType
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.model.AbstractContextOwner
import bard.db.people.Role
import bard.db.project.InlineEditableCommand
import bardqueryapi.IQueryService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.validation.ValidationException
import groovy.transform.InheritConstructors
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.tuple.Pair
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.json.JSONArray
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.acls.domain.BasePermission

import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.SimpleDateFormat

@Mixin(EditingHelper)
class AssayDefinitionController {

    static allowedMethods = [associateContext: "POST", disassociateContext: "POST", deleteMeasure: "POST", addMeasure: "POST"]

    AssayContextService assayContextService
    ContextService contextService
    SpringSecurityService springSecurityService
    def permissionEvaluator
    MeasureTreeService measureTreeService
    AssayDefinitionService assayDefinitionService
    CapPermissionService capPermissionService
    IQueryService queryService

    @Secured(['isAuthenticated()'])
    def groupAssays() {
        List<Assay> assays = capPermissionService.findAllObjectsForRoles(Assay)
        LinkedHashSet<Assay> uniqueAssays = new LinkedHashSet<Assay>(assays)
        render(view: "groupAssays", model: [assays: uniqueAssays])
    }

    def assayComparisonReport() {

    }

    @Secured(['isAuthenticated()'])
    def teams(){
        final List<Role> roles = Role.list()
        [roles:roles]
    }
    def generateAssayComparisonReport(final Long assayOneId, final Long assayTwoId) {
        if (assayOneId == null || assayTwoId == null) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "Please enter valid assay ids in both text boxes")
            return
        }

        Assay assayOne = Assay.findById(assayOneId)
        if (!assayOne) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "ADID: ${assayOneId} does not exist")
            return
        }
        Assay assayTwo = Assay.findById(assayTwoId)
        if (!assayTwo) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "ADID: ${assayTwoId} does not exist")
            return
        }
        final Map reportMap = assayDefinitionService.generateAssayComparisonReport(assayOne, assayTwo)
        render(template: "generateAssayCompareReport", model: reportMap)
    }
    @Secured(['isAuthenticated()'])
    def editAssayType(InlineEditableCommand inlineEditableCommand) {
        try {
            final AssayType assayType = AssayType.byId(inlineEditableCommand.value)
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateAssayType(inlineEditableCommand.pk, assayType)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.lastUpdated, assay.assayType.id)
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
    def editOwnerRole(InlineEditableCommand inlineEditableCommand) {
        try {
            final Role ownerRole = Role.findByDisplayName(inlineEditableCommand.value)?:Role.findByAuthority(inlineEditableCommand.value)
            if (!ownerRole) {
                editBadUserInputErrorMessage("Could not find a registered team with name ${inlineEditableCommand.value}")
                return
            }
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            if (!BardCommand.isRoleInUsersRoleList(ownerRole)) {
                editBadUserInputErrorMessage("You do not have the permission to select team: ${inlineEditableCommand.value}")
                return
            }
            //verify that the role is part of the users role

            assay = assayDefinitionService.updateOwnerRole(inlineEditableCommand.pk, ownerRole)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.lastUpdated, assay.ownerRole.displayName)

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
    def editAssayStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final AssayStatus assayStatus = AssayStatus.byId(inlineEditableCommand.value)
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateAssayStatus(inlineEditableCommand.pk, assayStatus)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.lastUpdated, assay.assayStatus.id)

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
    def editAssayName(InlineEditableCommand inlineEditableCommand) {
        try {
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Assay.ASSAY_NAME_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            assay = assayDefinitionService.updateAssayName(inlineEditableCommand.pk, inputValue)
            if (assay?.hasErrors()) {
                throw new Exception("Error while editing assay Name")
            }
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.lastUpdated, assay.assayName)
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
    def editDesignedBy(InlineEditableCommand inlineEditableCommand) {
        try {
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Assay.DESIGNED_BY_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            assay = assayDefinitionService.updateDesignedBy(inlineEditableCommand.pk, inputValue)
            if (assay?.hasErrors()) {
                throw new Exception("Error while editing Assay designed by")
            }

            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.lastUpdated, assay.designedBy)
        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    /**
     * Draft is excluded as End users cannot set a status back to Draft
     * @return  list of strings representing available status options
     */
    def assayStatus() {
        List<String> sorted = []
        final Collection<AssayStatus> assayStatuses = AssayStatus.values()
        for (AssayStatus assayStatus : assayStatuses) {
            if (assayStatus != AssayStatus.DRAFT) {
                sorted.add(assayStatus.id)
            }
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null

    }

    def roles() {
        List<String> sorted = []

        final Collection<Role> authorities = BardCommand.userRoles()


        for (Role role : authorities) {
            if (role.authority?.startsWith("ROLE_TEAM_")) {
                sorted.add(role.displayName)
            }
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def assayTypes() {
        List<String> sorted = []
        final Collection<AssayType> assayTypes = AssayType.values()
        for (AssayType assayType : assayTypes) {
            sorted.add(assayType.id)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def index() {
        redirect(action: "groupAssays")
    }
    @Secured(['isAuthenticated()'])
    def save(AssayCommand assayCommand) {
        if (!assayCommand.validate()) {
            create(assayCommand)
            return
        }
        final Assay assay = assayCommand.createNewAssay()
        if (assay) {
            redirect(action: "show", id: assay.id)
            return
        }
        render(view: "create", model: [assayCommand: assayCommand])
    }
    @Secured(['isAuthenticated()'])
    def create(AssayCommand assayCommand) {
        if (!assayCommand) {
            projectCommand: new AssayCommand()
        }
        [assayCommand: new AssayCommand()]
    }

    @Secured(['isAuthenticated()'])
    def cloneAssay(Long id) {
        Assay assay = Assay.get(id)
        try {
            if (!BardCommand.userRoles()) {
                throw new RuntimeException("You need to be a member of at least one team to clone any assay")
            }
            assay = assayDefinitionService.cloneAssayForEditing(assay, springSecurityService.principal?.username)
            //Randomly select a Role in the users role list for this assay

            assay = assayDefinitionService.recomputeAssayShortName(assay)
        }
        catch (ValidationException ee) {
            assay = Assay.get(id)
            flash.message = "Cannot clone assay definition with id \"${id}\" probably because of data migration issues. Please email the BARD team at bard-users@broadinstitute.org to fix this assay"

            log.error("Clone assay failed", ee);
        }
        catch (Exception ee) {
            flash.message = ee.message
        }
        redirect(action: "show", id: assay.id)
    }


    def show() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            def messageStr = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return [message: messageStr]
        }
        JSON measureTreeAsJson = null

        // sanity check the context items
        for (context in assayInstance.assayContexts) {
            assert context.id != null
            for (item in context.contextItems) {
                if (item?.id == null) {
                    throw new RuntimeException("Context ${context.id} missing context item.  Display order probably needs to be updated.")
                }
            }
        }

        measureTreeAsJson = []
        boolean editable = canEdit(permissionEvaluator, springSecurityService, assayInstance)
        String owner = capPermissionService.getOwner(assayInstance)

        //TODO: This should get replaced with cache. Get the active vs tested compound counts
        //grab all of the experiment ids
        final List<Long> experimentIds = assayInstance.experiments.collect { it.id }

        final Map<Long, Pair<Long, Long>> experimentsActiveVsTested = queryService.findActiveVsTestedForExperiments(experimentIds)
        return [assayInstance: assayInstance, assayOwner: owner, measureTreeAsJson: measureTreeAsJson, editable: editable ? 'canedit' : 'cannotedit', experimentsActiveVsTested: experimentsActiveVsTested]
    }
    @Secured(['isAuthenticated()'])
    def editContext(Long id, String groupBySection) {
        def assayInstance = Assay.get(id)

        if (!assayInstance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }
        AbstractContextOwner.ContextGroup contextGroup = assayInstance.groupBySection(ContextType.byId(groupBySection?.decodeURL()))

        [assayInstance: assayInstance, contexts: [contextGroup]]
    }
    @Secured(['isAuthenticated()'])
    def editMeasure() {
        JSON measuresTreeAsJson = null;

        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Experiment'), params.id])
            return
        } else {
            measuresTreeAsJson = new JSON(measureTreeService.createMeasureTree(experimentInstance, false));
        }

        [experimentInstance: experimentInstance, measuresTreeAsJson: measuresTreeAsJson]
    }
    @Secured(['isAuthenticated()'])
    def deleteMeasure() {
        ExperimentMeasure experimentMeasure = ExperimentMeasure.get(params.measureId)

        if (experimentMeasure.childMeasures.size() != 0) {
            flash.message = "Cannot delete measure \"${experimentMeasure.displayLabel}\" because it has children"
        }
//        else if (measure.experimentMeasures.size() != 0) {
//            flash.message = "Cannot delete measure \"${measure.displayLabel}\" because it is used in an experiment definition"
//        }
        else {
            if (!canEdit(permissionEvaluator, springSecurityService, experimentMeasure.experiment)) {
                render accessDeniedErrorMessage()
                return
            }
            experimentMeasure.delete()
        }
        redirect(action: "editMeasure", id: params.id)
    }
    @Secured(['isAuthenticated()'])
    def addMeasure() {
        // final Assay assayInstance = Assay.get(params.id)
        final Experiment experiment = Experiment.get(params.id)
        final Element resultType = Element.get(params.resultTypeId)
        final String parentChildRelationship = params.relationship
        HierarchyType hierarchyType = null

        if (!resultType) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Result Type is Required!'
        } else {
            def parentMeasure = null
            if (params.parentMeasureId) {
                parentMeasure = ExperimentMeasure.get(params.parentMeasureId)
            }
            //if there is a parent measure then there must be a selected relationship
            if (parentMeasure && (StringUtils.isBlank(parentChildRelationship) || "null".equals(parentChildRelationship))) {
                render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Relationship to Parent is required!'
                return
            } else {
                if (StringUtils.isNotBlank(parentChildRelationship)) {
                    hierarchyType = HierarchyType.byId(parentChildRelationship.trim())
                }
                def statsModifier = null
                if (params.statisticId) {
                    statsModifier = Element.get(params.statisticId)
                }


                def entryUnit = null
                if (params.entryUnitName) {
                    entryUnit = Element.findByLabel(params.entryUnitName)
                }
                try {
                    ExperimentMeasure newMeasure = assayContextService.addExperimentMeasure(experiment.id, parentMeasure, resultType, statsModifier, entryUnit, hierarchyType)
                    render status: HttpServletResponse.SC_OK, text: "Successfully added measure " + newMeasure.displayLabel
                }
                catch (AccessDeniedException ade) {
                    log.error(ade)
                    render accessDeniedErrorMessage()
                }
                catch (Exception ee) { //TODO add tests
                    render status: HttpServletResponse.SC_BAD_REQUEST, text: "${ee.message}"
                }

            }
        }
    }
    @Secured(['isAuthenticated()'])
    def disassociateContext() {
        final AssayContext assayContext = AssayContext.get(params.assayContextId)
        try {
            final ExperimentMeasure measure = ExperimentMeasure.get(params.measureId)

            if (measure == null) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
            } else if (assayContext == null) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayContext.label', default: 'AssayContext'), params.id])
            } else {
                flash.message = null
                assayContextService.disassociateAssayContext(measure, assayContext, assayContext.assay.id)
            }
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
            return
        }
        redirect(action: "editMeasure", id: assayContext.assay.id)
    }
    @Secured(['isAuthenticated()'])
    def associateContext() {
        try {
            def measure = ExperimentMeasure.get(params.measureId)
            def context = null
            if (params.assayContextId && 'null' != params.assayContextId) {
                context = AssayContext.get(params.assayContextId)
            }

            if (measure == null) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
            } else if (context == null) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayContext.label', default: 'AssayContext'), params.assayContextId])
            } else {

                assayContextService.associateExperimentContext(measure, context, context.assay.id)
                flash.message = "Measure '${measure?.displayLabel}' successfully associated to Context '${context?.contextName}'"
            }
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
            return
        }
        redirect(action: "editMeasure", id: params.id)
    }
    @Secured(['isAuthenticated()'])
    def changeRelationship() {
        try {
            def measure = ExperimentMeasure.get(params.measureId)
            def parentChildRelationship = params.relationship

            HierarchyType hierarchyType = null
            if (StringUtils.isNotBlank(parentChildRelationship)) {
                hierarchyType = HierarchyType.byId(parentChildRelationship.trim())
            }
            if (measure == null) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
            } else {
                flash.message = null
                if (measure.parent) { //if this measure has no parent then do nothing
                    assayContextService.changeParentChildRelationship(measure, hierarchyType, measure.id)
                }
            }
        } catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
            return
        }
        redirect(action: "editMeasure", id: params.id)
    }

    @Secured(['isAuthenticated()'])
    def reloadCardHolder(Long assayId) {
        def assay = Assay.get(assayId)
        if (assay) {
            render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
        } else {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }
    }
    //cannot find anywhere that it is used
    @Deprecated
    def launchEditItemInCard(Long assayContextId, Long assayContextItemId) {
        def assayContextItem = AssayContextItem.get(assayContextItemId)
        render(template: "editItemForm", model: [assayContextItem: assayContextItem, assayContextId: assayContextId])
    }
    //cannot find anywhere that it is used
    @Secured(['isAuthenticated()'])
    def updateCardName(String edit_card_name, Long contextId) {
        try {
            AssayContext assayContext = AssayContext.findById(contextId)
            Assay assay = assayContext.assay
            assayContext = assayContextService.updateCardName(contextId, edit_card_name, assay.id)
            assay = assayContext.assay
            render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
        } catch (AccessDeniedException aee) {
            render accessDeniedErrorMessage()
        }
    }

    /**
     *
     * @param measureId
     * @param parentMeasureId
     * @return
     */
    @Secured(['isAuthenticated()'])
    def moveMeasureNode(Long measureId, Long parentMeasureId) {
        try {
            ExperimentMeasure measure = ExperimentMeasure.get(measureId)
            ExperimentMeasure parentMeasure = null
            if (parentMeasureId) {
                parentMeasure = ExperimentMeasure.get(parentMeasureId)
            }
            assayDefinitionService.moveMeasure(measure.experiment.assay.id, measure, parentMeasure)
            render new JSONArray()
        } catch (AccessDeniedException aee) {
            render accessDeniedErrorMessage()
        }
    }
}
class EditingHelper {
    static final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy")

    String validateInputSize(int maxSize, int currentSize) {
        if (currentSize > maxSize) {
            return "Length of input must not exceed ${maxSize} characters, but you entered ${currentSize} characters"
        }
        return null
    }

    boolean canEdit(PermissionEvaluator permissionEvaluator, SpringSecurityService springSecurityService, domainInstance) {

        final boolean isAdmin = SpringSecurityUtils?.ifAnyGranted('ROLE_BARD_ADMINISTRATOR')
        if (isAdmin) {
            return true
        }

        def auth = springSecurityService?.authentication

        Class<?> clazz = org.springframework.util.ClassUtils.getUserClass(domainInstance.getClass());

        return permissionEvaluator?.hasPermission(auth, domainInstance.id, clazz.name, BasePermission.ADMINISTRATION)
    }

    def generateAndRenderJSONResponse(Long currentVersion, String modifiedBy, Date lastUpdated, final String newValue) {
        Map<String, String> dataMap = [:]
        dataMap.put('version', currentVersion.toString())
        dataMap.put('modifiedBy', modifiedBy)
        dataMap.put('lastUpdated', formatter.format(lastUpdated))
        dataMap.put("data", newValue)

        JSON jsonResponse = dataMap as JSON
        render status: HttpServletResponse.SC_OK, text: jsonResponse, contentType: 'text/json', template: null
    }

    def conflictMessage(String message) {
        render(status: HttpServletResponse.SC_CONFLICT, text: message, contentType: 'text/plain', template: null)
    }

    def editErrorMessage() {
        render(status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: message(code: 'editing.error.message'), contentType: 'text/plain', template: null)
    }

    def editExceedsLimitErrorMessage(String message) {
        render(status: HttpServletResponse.SC_BAD_REQUEST, text: message, contentType: 'text/plain', template: null)

    }
    def editBadUserInputErrorMessage(String message) {
        render(status: HttpServletResponse.SC_BAD_REQUEST, text: message, contentType: 'text/plain', template: null)

    }
    def accessDeniedErrorMessage() {
        return [status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null]
    }


}
@InheritConstructors
@Validateable
class AssayCommand extends BardCommand {

    String assayName
    String assayVersion = "1"
    Date dateCreated = new Date()
    AssayType assayType = AssayType.REGULAR
    Role ownerRole

    SpringSecurityService springSecurityService


    public static final List<String> PROPS_FROM_CMD_TO_DOMAIN = ['ownerRole', 'assayType', 'assayName', 'assayVersion', 'dateCreated'].asImmutable()

    static constraints = {
        importFrom(Assay, exclude: ['ownerRole', 'assayStatus', 'readyForExtraction', 'lastUpdated'])
        ownerRole(nullable: false, validator: { value, command, err ->
            /*We make it required in the command object even though it is optional in the domain.
         We will make it required in the domain as soon as we are done back populating the data*/
            //validate that the selected role is in the roles associated with the user
            if (!BardCommand.isRoleInUsersRoleList(value)) {
                err.rejectValue('ownerRole', "message.code", "You do not have the privileges to create Assays for this team : ${value.displayName}");
            }
        })
    }

    AssayCommand() {}



    Assay createNewAssay() {
        Assay assayToReturn = null
        if (validate()) {
            Assay tempAssay = new Assay()
            copyFromCmdToDomain(tempAssay)
            if (attemptSave(tempAssay)) {
                assayToReturn = tempAssay
            }
        }
        return assayToReturn
    }


    void copyFromCmdToDomain(Assay assay) {
        assay.designedBy = springSecurityService.principal?.username
        assay.modifiedBy = assay.designedBy
        for (String field in PROPS_FROM_CMD_TO_DOMAIN) {
            assay[(field)] = this[(field)]
        }
    }
}
