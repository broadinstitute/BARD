package bard.db.people

import bard.db.PersonService
import bard.db.project.InlineEditableCommand
import bard.db.registration.EditingHelper
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException
import groovy.util.Eval
import bard.db.enums.TeamRole

@Mixin(EditingHelper)
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RoleController {

    static allowedMethods = [save: "POST", update: "POST", modifyTeamRoles: "POST"]
    SpringSecurityService springSecurityService
    PersonService personService

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        [roleInstanceList: Role.list()]
    }


    def create() {
        [roleInstance: new Role(params)]
    }

    def show(Long id) {
        def roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.authority.label', default: 'Team'), id])
            redirect(action: "list")
            return
        }
//        def persons = PersonRole.findAllByRole(roleInstance, [sort: "person.fullName", order: "asc"]).collect { it.person } as Set
        def persons = PersonRole.findAllByRole(roleInstance, [sort: "person.fullName", order: "asc"])
        boolean isTeamManage = personService.isTeamManager(roleInstance.id)
        [roleInstance: roleInstance, editable: 'canedit', teamMembers: persons, isTeamManager: isTeamManage]
    }

    def addUserToTeam(String email, Long roleId){
        def person = Person.findByEmailAddress(email)
        if(person){
            Role role = Role.get(roleId)
            PersonRole personRole = new PersonRole(person: person, role: role)
            if(!personRole.save(flush: true)){
                flash.error = "ERROR: Unable to  save user ${person.fullName} (${person.emailAddress})"
            }
            flash.success = "User ${person.fullName} (${person.emailAddress}) was successfully added to team ${role.displayName}"
            redirect(action: "show", id: roleId)
        }
        else{
            flash.error = "${email} is not a registered BARD user"
            redirect(action: "show", id: roleId)
        }
    }

    def myTeams(){
        def username = springSecurityService.principal?.username
        Person user = Person.findByUserName(username)
//        def roles = PersonRole.findAllByTeamRoleAndPerson('Manager', user).collect{it.role} as Set
        def roles = PersonRole.findAllByTeamRoleAndPerson('Manager', user)
        [teams: roles]
    }

    def modifyTeamRoles(){
        def successfulSaves = []
        def errorSaves = []
        def personRoleArray = params.list("checkboxes")
        for (int i = 0; i < personRoleArray.size(); i++){
            PersonRole personRole = PersonRole.get(personRoleArray.get(i))
            if(personRole){
                def personName = personRole.person.fullName
                personRole.teamRole = params.teamRole
                if(!personRole.save(flush: true)){
                    errorSaves.add(personName)
                }
                else{
                    successfulSaves.add(personName)
                }
            }
            else{
                errorSaves.add("No record found")
            }
        }
        if(successfulSaves.size() > 0){
            flash.success = "Team role '${params.teamRole}' was successfully applied to user(s) ${successfulSaves.join(", ")}."
        }
        if(errorSaves.size() > 0){
            flash.error = "ERROR: Unable to set team role ${params.teamRole} for user(s) ${errorSaves.join(", ")}."
        }
        redirect(action: "show", id: params.roleId)
    }

    def save() {
        String authority = params.authority

        //remove all spaces from string
        authority = authority?.toUpperCase()?.replaceAll("\\s+", "")
        String displayName = params.displayName
        if (authority && !authority.startsWith("ROLE_TEAM_")) {
            authority = "ROLE_TEAM_" + authority
        }

        Role roleInstance = new Role(authority: authority, displayName: displayName, modifiedBy: springSecurityService.principal?.username)
        if (!roleInstance.save(flush: true)) {
            render(view: "create", model: [roleInstance: roleInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'role.authority.label', default: 'Team'), roleInstance.id])
        redirect(action: "show", id: roleInstance.id)
    }



    def editDisplayName(InlineEditableCommand inlineEditableCommand) {
        try {
            Role role = Role.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(role.version, Role.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Role.DISPLAY_NAME_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            role.displayName = inputValue.trim()
            role.modifiedBy = springSecurityService.principal?.username

            role.save(flush: true)
            if (role?.hasErrors()) {
                throw new Exception("Error while editing field display Name")
            }

            generateAndRenderJSONResponse(role.version, role.modifiedBy, role.lastUpdated, role.displayName)
        }
        catch (AccessDeniedException ade) {
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee, ee)
            editErrorMessage()
        }
    }

    def editAuthority(InlineEditableCommand inlineEditableCommand) {
        try {
            Role role = Role.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(role.version, Role.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()


            String maxSizeMessage = validateInputSize(Role.AUTHORITY_NAME_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            String authority = inputValue.trim()?.toUpperCase()?.replaceAll("\\s+", "")

            if (authority && !authority.startsWith("ROLE_TEAM_")) {
                authority = "ROLE_TEAM_" + authority
            }
            role.authority = authority
            role.modifiedBy = springSecurityService.principal?.username

            role.save(flush: true)
            if (role?.hasErrors()) {
                throw new Exception("Error while editing field Name")
            }

            generateAndRenderJSONResponse(role.version, role.modifiedBy, role.lastUpdated, role.authority)
        }
        catch (AccessDeniedException ade) {
            log.error(ade, ade)
            render accessDeniedErrorMessage()
        } catch (Exception ee) {
            log.error(ee, ee)
            editErrorMessage()
        }
    }
}
