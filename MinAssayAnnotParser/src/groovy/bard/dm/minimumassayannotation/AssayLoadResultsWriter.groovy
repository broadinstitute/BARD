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
        writer.writeLine("filename, row_num, aid, status, message")
    }

    public void write(AssayDto assayDto, LoadResultType resultType, String message) {
        final def aidOutput = assayDto.aid ? assayDto.aid : assayDto.aidFromCell

        def lineData = [assayDto.sourceFile.name, assayDto.rowNum, aidOutput, resultType, message]

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
        success, assayContextSuccessOnly, fail
    }
}
