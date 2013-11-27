package bard.admin

import bard.core.util.ExternalUrlDTO
import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class ConfigController {
    ExternalUrlDTO externalUrlDTO

    def index() {
        [ config: externalUrlDTO ]
    }

    def update() {
        externalUrlDTO.ncgcUrl = params.ncgcUrl
        externalUrlDTO.promiscuityUrl = params.promiscuityUrl

        redirect(controller: "bardWebInterface", action: "navigationPage")
    }
}
