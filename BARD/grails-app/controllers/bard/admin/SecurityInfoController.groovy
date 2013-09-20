package bard.admin

import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class SecurityInfoController extends grails.plugins.springsecurity.ui.SecurityInfoController {
}
