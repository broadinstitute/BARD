package bard.db.people

import bard.db.project.InlineEditableCommand
import bard.db.registration.EditingHelper
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException

@Mixin(EditingHelper)
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RoleController {

    static allowedMethods = [save: "POST", update: "POST"]
    SpringSecurityService springSecurityService

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
        def persons = PersonRole.findAllByRole(roleInstance, [sort: "person.fullName", order: "asc"]).collect { it.person } as Set
        [roleInstance: roleInstance, editable: 'canedit', teamMembers: persons]
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
