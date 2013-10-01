package bard

import bard.db.experiment.Experiment
import bard.db.experiment.PubchemImportService
import bard.db.experiment.AsyncResultsService
import bard.db.experiment.results.ImportSummary
import bard.db.registration.ExternalReference
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ReloadResultsJob {
    PubchemImportService pubchemImportService;
    AsyncResultsService asyncResultsService;

    def perform(String username, String jobKey, Long id) {
        SpringSecurityUtils.reauthenticate(username, null)

        def experiment = Experiment.get(id)
        ExternalReference xref = experiment.externalReferences.find { it.extAssayRef.startsWith("aid=") }
        def aid = Integer.parseInt(xref.extAssayRef.replace("aid=",""))

        ImportSummary results = pubchemImportService.recreateMeasuresAndLoad(true, aid, {msg -> asyncResultsService.updateStatus(jobKey, msg)})

        asyncResultsService.updateResult(jobKey, results)
    }
}
