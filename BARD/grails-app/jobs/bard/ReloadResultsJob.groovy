package bard

import bard.db.experiment.Experiment
import bard.db.experiment.PubchemImportService
import bard.db.experiment.AsyncResultsService
import bard.db.experiment.results.ImportSummary
import bard.db.registration.ExternalReference
import clover.org.apache.commons.lang.exception.ExceptionUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ReloadResultsJob {
    PubchemImportService pubchemImportService;
    AsyncResultsService asyncResultsService;

    def perform(String username, String jobKey, Long id) {
        try {
            SpringSecurityUtils.doWithAuth(username) {
                ImportSummary results = pubchemImportService.recreateMeasuresAndLoad(true, id,
                        { msg -> asyncResultsService.updateStatus(jobKey, msg)}
                )

                asyncResultsService.updateResult(jobKey, results)
            }
        } catch (Exception ex) {
            log.error("Exception thrown trying to execute recreate measures username: ${username}, jobKey: ${jobKey}, id: ${id}", ex)
            String message = ExceptionUtils.getRootCauseMessage(ex)
            asyncResultsService.updateStatus(jobKey, "An internal error occurred: ${message}")
        }
    }
}
