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

package bard.dm.minimumassayannotation

import org.apache.commons.lang3.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 1/6/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayLoadResultsWriter {
    private BufferedWriter writer

    public AssayLoadResultsWriter(String resultFilePath) {
        writer = new BufferedWriter(new FileWriter(resultFilePath))
        writer.writeLine("filename, row_num, aid, status, num_context_saved, message")
    }

    public void write(AssayDto assayDto, LoadResultType resultType, String message) {
        int numContextsSaved = 0;
        if (resultType != LoadResultType.fail) {
            for (List<ContextDTO> contextDTOList : [assayDto.assayContextDTOList, assayDto.measureContextDTOList]) {
                for (ContextDTO contextDTO : contextDTOList) {
                    if (contextDTO.wasSaved) {
                        numContextsSaved += 1
                    }
                }
            }
        }

        final def aidOutput = assayDto.aid ? assayDto.aid : assayDto.aidFromCell

        def lineData = [assayDto.sourceFile.name, assayDto.rowNum, aidOutput, resultType, numContextsSaved, message]

        StringBuilder line = new StringBuilder()
        for (def lineDatum : lineData) {
            line.append(StringUtils.defaultString(lineDatum.toString())).append(", ")
        }

        writer.writeLine(line.toString())
    }


    public void close() {
        writer.close()
    }

    public enum LoadResultType {
        success, assayContextSuccessOnly, nothingToLoad, fail
    }
}
