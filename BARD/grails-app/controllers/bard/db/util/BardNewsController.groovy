package bard.db.util

import bard.util.DownTimeSchedulerService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService

class BardNewsController {
    SpringSecurityService springSecurityService

    def index() {
        redirect(action: "list")
    }

    @Secured(['isAuthenticated()'])
    def list() {
        List<BardNews> bardNewsInstances = BardNews.listOrderByEntryDateUpdated(order: "desc")
        [bardNewsInstanceList: bardNewsInstances, bardNewsInstanceTotal: bardNewsInstances.size()]
    }

    @Secured(['isAuthenticated()'])
    def show(Long id) {
        BardNews bardNews = null
        if (id) {
            bardNews = BardNews.findById(id)
        }
        [bardNewsInstance: bardNews]
    }
}
