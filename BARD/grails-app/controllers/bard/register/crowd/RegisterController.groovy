package bard.register.crowd

import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import register.crowd.CrowdGroupMembership
import register.crowd.CrowdPassword
import register.crowd.CrowdRegisterUserService
import register.crowd.CrowdRegistrationUser

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RegisterController {
    static defaultAction = 'index'
    SpringSecurityService springSecurityService
    // override default value from base class
    static allowedMethods = [register: 'POST']
    CrowdRegisterUserService crowdRegisterUserService

    def index() {
        def copy = [:] + (flash.chainedParams ?: [:])
        copy.remove 'controller'
        copy.remove 'action'
        [registerCommand: new RegisterCommand(copy)]
    }

    def listUsersAndGroups() {
        final CrowdGroupMembership groupMembership = this.crowdRegisterUserService.listUsersAndGroups();
        render(view: 'listUsersAndGroups',
                model: [groupMembership: groupMembership])


    }

    def register(RegisterCommand registerCommand) {
        if (registerCommand.hasErrors()) {
            render view: 'index', model: [registerCommand: registerCommand]
            return
        }
        CrowdRegistrationUser registrationUser =
            new CrowdRegistrationUser(email: registerCommand.email,
                    name: registerCommand.username,
                    password: new CrowdPassword(value: registerCommand.password),
                    first_name: registerCommand.firstName, last_name: registerCommand.lastName,
                    display_name: registerCommand.displayName, active: true)

        this.crowdRegisterUserService.registerUser(registrationUser)
        flash.message = "Add User to the CAP database and assign them a Primary Group"
        redirect(controller: "person", action: "list", params: [userName: registerCommand.username, fullName: registerCommand.displayName, email: registerCommand.email])
    }
}
class RegisterCommand {

    String username
    String email
    String password
    String password2
    String firstName
    String lastName
    String displayName
    CrowdRegisterUserService crowdRegisterUserService

    static constraints = {
        username blank: false, nullable: false, validator: { value, command ->
            if (value) {
                if (command.crowdRegisterUserService.findUserByUserName(value)) {
                    return 'registerCommand.username.unique'
                }
            }
        }
        email blank: false, nullable: false, email: true, validator: { value, command ->
            if (value) {
                if (command.crowdRegisterUserService.findUserByEmail(value)) {
                    return 'registerCommand.email.unique'
                }
            }
        }
        password blank: false, nullable: false, validator: passwordValidator
        password2 validator: password2Validator
    }

    static private final passwordValidator = { String password, command ->
        if (command.username && command.username.equals(password)) {
            return 'command.password.error.username'
        }

        if (!checkPasswordMinLength(password, command) ||
                !checkPasswordMaxLength(password, command) ||
                !checkPasswordRegex(password, command)) {
            return 'command.password.error.strength'
        }
    }

    static private boolean checkPasswordMinLength(String password, command) {

        int minLength = 8

        password && password.length() >= minLength
    }

    static private boolean checkPasswordMaxLength(String password, command) {
        int maxLength = 64

        password && password.length() <= maxLength
    }

    static private boolean checkPasswordRegex(String password, command) {
        String passValidationRegex = '^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'

        password && password.matches(passValidationRegex)
    }

    static final password2Validator = { value, command ->
        if (command.password != command.password2) {
            return 'command.password2.error.mismatch'
        }
    }

}

