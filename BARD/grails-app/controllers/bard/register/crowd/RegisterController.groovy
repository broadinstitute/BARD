package bard.register.crowd

import bard.PersonCommand
import bard.db.command.BardCommand
import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import bard.db.registration.RegistrationCode
import grails.plugin.mail.MailService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.commons.GrailsApplication
import register.crowd.CrowdGroupMembership
import register.crowd.CrowdPassword
import register.crowd.CrowdRegisterUserService
import register.crowd.CrowdRegistrationUser

class RegisterController {
    static defaultAction = 'index'
    // override default value from base class
    static allowedMethods = [register: 'POST']
    CrowdRegisterUserService crowdRegisterUserService
    SpringSecurityService springSecurityService
    MailService mailService
    GrailsApplication grailsApplication
    def messageSource

    def index() {
        def copy = [:] + (flash.chainedParams ?: [:])
        copy.remove 'controller'
        copy.remove 'action'
        [command: new RegisterCommand(copy)]
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def listUsersAndGroups() {
        final CrowdGroupMembership groupMembership = this.crowdRegisterUserService.listUsersAndGroups();
        render(view: 'listUsersAndGroups',
                model: [groupMembership: groupMembership])


    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def register(RegisterCommand registerCommand) {

        if (!registerCommand.validate()) {
            render view: 'index', model: [command: registerCommand]
            return
        }
        Person person = registerCommand.createNewPerson()
        if (person) {
            CrowdRegistrationUser registrationUser =
                new CrowdRegistrationUser(email: registerCommand.email,
                        name: registerCommand.username,
                        password: new CrowdPassword(value: registerCommand.password),
                        first_name: registerCommand.firstName, last_name: registerCommand.lastName,
                        display_name: registerCommand.displayName, active: true)

            crowdRegisterUserService.registerUser(registrationUser)
            //add to role table
            //add to db
            flash.message = "Successfully added to database"
        } else {
            flash.message = "Failed to create user"
        }

        redirect(controller: "person", action: "list")
    }

    def signup(SignupCommand signupCommand) {
        if (!request.post) {
            return [command: new SignupCommand()]
        }
        if (!signupCommand.validate()) {
            return [command: signupCommand]
        }

        try {
            CrowdRegistrationUser registrationUser =
                new CrowdRegistrationUser(email: signupCommand.email,
                        name: signupCommand.username,
                        password: new CrowdPassword(value: signupCommand.password),
                        first_name: signupCommand.firstName, last_name: signupCommand.lastName,
                        display_name: signupCommand.displayName, active: false)

            crowdRegisterUserService.registerUser(registrationUser)


            RegistrationCode registrationCode = new RegistrationCode(userName: signupCommand.username)
            if (!registrationCode.save()) {
                crowdRegisterUserService.warnErrors(registrationCode, messageSource)

            }
            if (registrationCode == null || registrationCode.hasErrors()) {
                return [command: signupCommand, errorMessage: message(code: 'register.miscError')]
            }

            String url = generateLink('verifyRegistration', [t: registrationCode.token])

            def body = grailsApplication.config.registeruser.signup.emailBody
            if (body.contains('$')) {
                body = evaluate(body, [username: registrationUser.name, url: url])
            }
            mailService.sendMail {
                to signupCommand.email
                from grailsApplication.config.registeruser.signup.emailFrom
                subject grailsApplication.config.registeruser.signup.emailSubject
                html body.toString()
            }
            render(view: 'registerMessage', model: [successMessage: message(code: 'register.sent')])
        }
        catch (Exception ee) {
            String errorMessage = ee?.message
            if (!errorMessage) {
                errorMessage = "Exception occured during registration. Please contact the BARD team at bard@broadinstitute.org"
            }
            return [command: signupCommand, errorMessage: errorMessage]
        }
    }

    def resetPassword(ResetPasswordCommand resetPasswordCommand) {

        String token = params.t

        def registrationCode = token ? RegistrationCode.findByToken(token) : null
        if (!registrationCode) {
            flash.error = message(code: 'register.resetPassword.badCode')
            return [token: token, resetPasswordCommand: new ResetPasswordCommand(), errorMessage: error]
        }

        if (!request.post) {
            return [token: token, resetPasswordCommand: new ResetPasswordCommand()]
        }

        resetPasswordCommand.username = registrationCode.userName
        resetPasswordCommand.validate()

        if (resetPasswordCommand.hasErrors()) {
            return [token: token, resetPasswordCommand: resetPasswordCommand]
        }


        try {
            RegistrationCode.withTransaction { status ->
                final CrowdRegistrationUser crowdRegistrationUser = crowdRegisterUserService.findUserByUserName(registrationCode.userName)
                if (!crowdRegistrationUser) {
                    throw new RuntimeException("Could not find user ${registrationCode.userName}")
                }
                crowdRegisterUserService.resetPassword(resetPasswordCommand.username, resetPasswordCommand.password)
                registrationCode.delete()
            }

            springSecurityService.reauthenticate registrationCode.userName
            render(view: "registerMessage", model: [successMessage: message(code: 'register.resetPassword.success')])
        } catch (Exception ee) {
            return [token: token, resetPasswordCommand: resetPasswordCommand, errorMessage: ee?.message]
        }
    }

    def forgotPassword() {

        if (!request.post) {
            // show the form
            return
        }

        String username = params.username
        if (!username) {
            return [errorMessage: message(code: 'register.forgotPassword.username.missing')]
        }

        final CrowdRegistrationUser crowdRegistrationUser = crowdRegisterUserService.findUserByUserName(username)
        if (!crowdRegistrationUser) {
            return [errorMessage: message(code: 'register.forgotPassword.user.notFound')]
        }
        try {
            RegistrationCode registrationCode = new RegistrationCode(userName: crowdRegistrationUser.name)
            registrationCode.save(flush: true)

            String url = generateLink('resetPassword', [t: registrationCode.token])


            def body = grailsApplication.config.registeruser.forgotPassword.emailBody
            if (body.contains('$')) {
                body = evaluate(body, [username: crowdRegistrationUser.name, url: url])
            }
            mailService.sendMail {
                to crowdRegistrationUser.email
                from grailsApplication.config.registeruser.forgotPassword.emailFrom
                subject grailsApplication.config.registeruser.forgotPassword.emailSubject
                html body.toString()
            }

            [emailSent: true]
        } catch (Exception ee) {
            return [errorMessage: ee?.message]
        }

    }

    def verifyRegistration() {
        String token = params.t
        try {
            RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null
            if (!registrationCode) {
                render(view: "registerMessage", model: [errorMessage: message(code: 'register.badCode')])
                return
            }

            CrowdRegistrationUser crowdRegistrationUser = null
            RegistrationCode.withTransaction { status ->
                crowdRegistrationUser = crowdRegisterUserService.findUserByUserName(registrationCode.userName)

                if (!crowdRegistrationUser) {
                    return
                }

                crowdRegistrationUser.active = true
                //Save the user
                crowdRegisterUserService.updateRegisteredUser(crowdRegistrationUser)


                PersonCommand personCommand =
                    new PersonCommand(username: crowdRegistrationUser.name,
                            email: crowdRegistrationUser.email,
                            displayName: crowdRegistrationUser.display_name,
                            primaryGroup: null, roles: [], version: 0, validate: false)
                final Person person = personCommand.createNewPerson()
                if (!person) {
                    render(view: "registerMessage", model: [errorMessage: "Failed to create user"])
                    return
                }
                registrationCode.delete()
            }

            if (!crowdRegistrationUser) {
                render(view: "registerMessage", model: [errorMessage: message(code: 'register.badCode')])
                return
            }
            render(view: "registerMessage", model: [successMessage: message(code: 'register.complete')])
        } catch (Exception ee) {
            render(view: "registerMessage", model: [errorMessage: ee?.message])

        }
    }

    protected String generateLink(String action, linkParams) {
        createLink(base: "$request.scheme://$request.serverName:$request.serverPort$request.contextPath",
                controller: 'register', action: action,
                params: linkParams)
    }

    protected String evaluate(s, binding) {
        new SimpleTemplateEngine().createTemplate(s).make(binding)
    }
}
class ResetPasswordCommand {
    String username
    String password
    String password2

    static constraints = {
        username nullable: false
        password blank: false, nullable: false, validator: SignupCommand.passwordValidator
        password2 validator: SignupCommand.password2Validator
    }
}
class RegisterCommand extends SignupCommand {


    Role primaryGroup
    static constraints = {
        importFrom(SignupCommand)

        primaryGroup nullable: false, validator: { value, command ->
            if (!Role.findByAuthority(value?.authority)) {
                return "Role with id ${value.authority} does not exist"
            }
        }
    }

    Person createNewPerson() {
        Person personToReturn = null
        if (validate()) {
            Person person = new Person()
            copyFromCmdToDomain(person)
            if (attemptSave(person)) {
                personToReturn = person
                if (!PersonRole.findByPersonAndRole(person, person.newObjectRole)) {
                    PersonRole.create(person, person.newObjectRole, springSecurityService.principal?.username, true)
                }
            }

        }
        return personToReturn
    }

    void copyFromCmdToDomain(Person person) {
        person.userName = this.username
        person.emailAddress = this.email
        person.fullName = this.displayName
        person.newObjectRole = primaryGroup
    }

}
class SignupCommand extends BardCommand {


    String username
    String email
    String password
    String password2
    String firstName
    String lastName


    public String getDisplayName(){
        return this.firstName + " " + this.lastName
    }
    CrowdRegisterUserService crowdRegisterUserService
    SpringSecurityService springSecurityService

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

    static boolean checkPasswordMinLength(String password) {

        int minLength = 8

        password && password.length() >= minLength
    }

    static boolean checkPasswordMaxLength(String password) {
        int maxLength = 64

        password && password.length() <= maxLength
    }

    static boolean checkPasswordRegex(String password) {
        String passValidationRegex = '^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'

        password && password.matches(passValidationRegex)
    }

    static final password2Validator = { value, command ->
        if (command.password != command.password2) {
            return 'command.password2.error.mismatch'
        }
    }
    static final passwordValidator = { String password, command ->
        if (command.username && password.equals(command.username)) {
            return 'command.password.error.username'
        }

        if (!checkPasswordMinLength(password) ||
                !checkPasswordMaxLength(password) ||
                !checkPasswordRegex(password)) {
            return 'command.password.error.strength'
        }
    }
}

