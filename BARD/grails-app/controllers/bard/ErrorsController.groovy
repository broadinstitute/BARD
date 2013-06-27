package bard

import grails.plugins.springsecurity.Secured

@Secured(['permitAll'])
class ErrorsController {

    def error403 = {}

    def error500 = {
        render view: '/error'
    }
}