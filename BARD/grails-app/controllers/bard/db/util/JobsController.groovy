package bard.db.util

import bard.db.experiment.AsyncResultsService
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class JobsController {
    AsyncResultsService asyncResultsService

    def index() {
        def jobs = asyncResultsService.updateAndGetJobs()
        return [jobs: jobs]
    }
}
