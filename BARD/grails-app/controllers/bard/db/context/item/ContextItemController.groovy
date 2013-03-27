package bard.db.context.item

import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ContextItemController {

    def index() {}

    def create(Long contextId, String contextClass){
        [instance:new BasicContextItemCommand(contextId:contextId, contextClass: contextClass)]
    }

}
