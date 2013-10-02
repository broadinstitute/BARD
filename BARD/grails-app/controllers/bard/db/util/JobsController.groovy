package bard.db.util

import bard.db.experiment.AsyncResultsService

class JobsController {
    AsyncResultsService asyncResultsService

    def index() {
        def jobs = asyncResultsService.updateAndGetJobs()
        return [jobs: jobs]
    }
}
