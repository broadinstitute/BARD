package bard.admin

import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RequestmapController extends grails.plugins.springsecurity.ui.RequestmapController {
}
