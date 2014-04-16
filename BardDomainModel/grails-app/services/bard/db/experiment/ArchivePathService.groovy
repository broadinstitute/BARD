package bard.db.experiment

import org.codehaus.groovy.grails.commons.GrailsApplication

import java.text.SimpleDateFormat

class ArchivePathService {
    GrailsApplication grailsApplication

    String constructUploadResultPath(Experiment experiment) {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
        return "uploaded-results/${(long)(experiment.id/1000)}/${experiment.id}/exp-${experiment.id}-${timestamp}.txt.gz"
    }

    String constructExportResultPath(Experiment experiment) {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
        return "exported-results/${(long)(experiment.id/1000)}/${experiment.id}/exp-${experiment.id}-${timestamp}.json.gz"
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

    /**
     * @return an InputStream for the etl results for the given experiment, or null if this experiment has no results stored.
     */
    InputStream getEtlExport(Experiment experiment) {
        File fullPath = getEtlExportFile(experiment)
        if(fullPath == null)
            return null;
        return new FileInputStream(fullPath)
    }

    File getEtlExportFile(Experiment experiment) {
        List<ExperimentFile> files = new ArrayList(experiment.experimentFiles)
        if (files.size() > 0) {
            files.sort {it.submissionVersion}
            ExperimentFile lastVersion = files.last()
            File fullPath = new File(prefix + File.separator + lastVersion.exportFile)
            return fullPath
        } else {
            return null;
        }
    }
}
