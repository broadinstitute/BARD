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

import bard.db.experiment.ResultsService
import bard.db.experiment.results.ImportSummary
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created by dlahr on 3/15/14.
 */


final File uploadFileDir =
//        new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/output") //for development environment
        new File("/home/unix/dlahr/projects/bard_NCI60/prodLoad/output") //for running on BigBard for loading to prod

List<File> uploadFileList = uploadFileDir.listFiles().collect({File it -> it}) as List<File>
Collections.sort(uploadFileList, new Comparator<File>() {
    @Override
    int compare(File o1, File o2) {
        return o1.name.compareTo(o2.name)
    }
})

SpringSecurityUtils.reauthenticate("dlahr", null)

ResultsService resultsService = ctx.resultsService

for (File uploadFile : uploadFileList) {
    Integer experimentId = readExperimentId(uploadFile)

    println("$experimentId $uploadFile")

    FileInputStream fileInputStream = new FileInputStream(uploadFile)

    ImportSummary importSummary = resultsService.importResults(experimentId, fileInputStream)

    if (importSummary.errors.size() > 0) {
        println("errors with upload:")
        println(importSummary.errors.join("\n"))
    }
}


return


Integer readExperimentId(File uploadFile) {
    BufferedReader reader = new BufferedReader(new FileReader(uploadFile))

    String[] split = reader.readLine().split(",")
    Integer eid = Integer.valueOf(split[2])

    reader.close()

    return eid
}
