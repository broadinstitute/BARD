package bard.db.experiment

import org.codehaus.groovy.grails.commons.GrailsApplication

import java.text.SimpleDateFormat

class ArchivePathService {
    GrailsApplication grailsApplication

    String constructUploadResultPath(Experiment experiment) {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
        return "uploaded-results/${experiment.id%100}/${experiment.id}/${timestamp}.txt.gz"
    }

    String constructExportResultPath(Experiment experiment) {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
        return "exported-results/${experiment.id%100}/${experiment.id}/${timestamp}.json.gz"
    }

    File prepareForWriting(String filename) {
        File file = new File(prefix + File.separator+ filename)
        assert !file.exists()

        if (!file.parentFile.exists()) {
            assert file.parentFile.mkdirs()
        }

        return file
    }

    String getPrefix() {
        return grailsApplication.config.bard.services.resultService.archivePath
    }

    InputStream getEtlExport(Experiment experiment) {
        List<ExperimentFile> files = new ArrayList(experiment.experimentFiles)
        files.sort {it.submissionVersion}
        ExperimentFile lastVersion = files.last()
        new lastVersion.exportFile
    }
}
