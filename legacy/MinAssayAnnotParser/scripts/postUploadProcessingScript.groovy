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

import bard.dm.Log
import org.apache.log4j.Level
import postUploadProcessing.ContextGroupService
import bard.dm.postUploadProcessing.ContextChange
import bard.db.registration.AssayContext
import org.springframework.transaction.TransactionStatus

Log.initializeLogger("test/exampleData/logsAndOutput/dnaRepairLoad.log")
final Date startDate = new Date()
Log.logger.info("Start post-processing the spreadsheet uplaods ${startDate}")

final String modifiedBy = "jbittker"
Log.logger.setLevel(Level.INFO)
final Integer START_ROW = 3 //1-based

ContextGroupService contextGroupService = ctx.getBean("contextGroupService")

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".xlsx")
    }
}
List<String> inputDirPathArray = ["test/exampleData/postUploadProcessing/"]
for (String inputDirPath : inputDirPathArray) {
    File inputDirFile = new File(inputDirPath)
    inputFileList.addAll(inputDirFile.listFiles(xlsxExtensionFilenameFilter))
}
Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")

List<ContextChange> contextChangeDTOs = []
for (File inputFile : inputFileList) {
    Log.logger.info("${new Date()} processing file ${inputFile.absolutePath}")
    contextChangeDTOs.addAll(contextGroupService.parseFile(inputFile, START_ROW))
}

List<ContextChange> contextChangeList = contextGroupService.buildContextChangeListFromDTOs(contextChangeDTOs)

AssayContext.withTransaction { TransactionStatus status ->
    for (ContextChange contextChange : contextChangeList) {
        contextChange.doChange()
    }
    //comment out to commit the transaction
    status.setRollbackOnly()
}

final Date endDate = new Date()
final double durationMin = (endDate.time - startDate.time) / 60000.0
Log.logger.info("finished at ${endDate}   duration[min]: ${durationMin}")
Log.close()

return false
