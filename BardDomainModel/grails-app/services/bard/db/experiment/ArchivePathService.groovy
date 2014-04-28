/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
