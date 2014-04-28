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

package bard.db.people

import bard.db.enums.TeamRole
import bard.db.project.InlineEditableCommand
import bard.db.registration.EditingHelper
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.annotation.Transactional

@Mixin(EditingHelper)
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
@Transactional
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

    @Secured("isAuthenticated()")
    def show(Long id) {

        final Role roleInstance = Role.get(id)
        if (!roleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'role.authority.label', default: 'Team'), id])
            redirect(action: "list")
            return
        }
        ifPersonManagerOfTeam(id) {
            final List<PersonRole> personRoles = PersonRole.findAllByRole(roleInstance)
            personRoles.sort(true){it.person.fullName?.toLowerCase()}
            final boolean isTeamManager = personService.isTeamManager(id)
            final Person person = personService.findCurrentPerson()
            final boolean isAdmin = person.roles*.authority.contains('ROLE_BARD_ADMINISTRATOR')
            render(view: 'show', model: [roleInstance: roleInstance, editable: 'canedit', teamMembers: personRoles, isTeamManager: isTeamManager, isAdmin: isAdmin])
        }
    }

    @Secured("isAuthenticated()")
    def addUserToTeam(String email, Long roleId) {
        ifPersonManagerOfTeam(roleId) {
            def person = Person.findByEmailAddress(email)
            if (person) {
                Role role = Role.get(roleId)
                PersonRole personRole = new PersonRole(person: person, role: role)
                if (!personRole.save(flush: true)) {
                    flash.error = "ERROR: Unable to  save user ${person.fullName} (${person.emailAddress})"
                }
                flash.success = "User ${person.fullName} (${person.emailAddress}) was successfully added to team ${role.displayName}"
                redirect(action: "show", id: roleId)
            } else {
                flash.error = "${email} is not a registered BARD user"
                redirect(action: "show", id: roleId)
            }
        }
    }

    @Secured(["isAuthenticated()"])
    def myTeams() {
        final Person person = personService.findCurrentPerson()
        def roles = PersonRole.findAllByTeamRoleAndPerson(TeamRole.MANAGER, person)
        [teams: roles]
    }

    @Secured(["isAuthenticated()"])
    def modifyTeamRoles(Long roleId, String teamRole) {
        ifPersonManagerOfTeam(roleId) {
            def successfulSaves = []
            def errorSaves = []
            def personRoleArray = params.list("checkboxes")
            for (int i = 0; i < personRoleArray.size(); i++) {
                PersonRole personRole = PersonRole.findByIdAndRole(personRoleArray.get(i), Role.get(roleId))
                if (personRole) {
                    def personName = personRole.person.fullName
                    personRole.teamRole = TeamRole.byId(teamRole)
                    if (!personRole.save(flush: true)) {
                        errorSaves.add(personName)
                    } else {
                        successfulSaves.add(personName)
                    }
                } else {
                    errorSaves.add("No record found")
                }
            }
            if (successfulSaves.size() > 0) {
                flash.success = "Team role '${teamRole}' was successfully applied to user(s) ${successfulSaves.join(", ")}."
            }
            if (errorSaves.size() > 0) {
                flash.error = "ERROR: Unable to set team role ${teamRole}. ${errorSaves.join(", ")}."
            }
            redirect(action: "show", id: params.roleId)
        }
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
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

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
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

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
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

    private def ifPersonManagerOfTeam(Long roleId, Closure closure) {
        final Person person = personService.findCurrentPerson()
        final boolean isManager = personService.isTeamManager(roleId)
        final boolean isBardAdmin = person.roles*.authority.contains('ROLE_BARD_ADMINISTRATOR')
        if (isBardAdmin || isManager) {
            closure.call()
        } else {
            throw new AccessDeniedException("sorry")
        }
    }

}
