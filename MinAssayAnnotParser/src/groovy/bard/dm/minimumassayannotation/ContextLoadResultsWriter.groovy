package bard.dm.minimumassayannotation

import org.apache.commons.lang3.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 1/4/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
class ContextLoadResultsWriter {
    private BufferedWriter writer

    public ContextLoadResultsWriter(String resultFilePath) {
        writer = new BufferedWriter(new FileWriter(resultFilePath))
        writer.writeLine("aid, adid, load_result_type, num_potential_contexts_to_load, num_existing_contexts, num_contexts_loaded, message")
    }

    public void write(def aid, Long adid, String contextName, LoadResultType resultType, String message) {
        def lineData = [aid, adid, contextName, resultType, message]
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
        success, alreadyLoaded, deleteOriginal, fail
    }
}
