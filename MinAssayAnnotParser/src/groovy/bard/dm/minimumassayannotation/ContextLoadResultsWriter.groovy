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
        writer.writeLine("aid, adid, context_name, load_result_type, num_potential_contexts_to_load, num_existing_contexts, num_contexts_loaded, message")
    }



    public void write(ContextDTO contextDTO, Long adid, LoadResultType resultType, Integer numExistingContextsInDb,
                      int numContextsLoaded, String message) {
        write_without_context(contextDTO?.aid, adid, contextDTO?.name, resultType, contextDTO?.contextItemDtoList?.size(), numExistingContextsInDb,
                numContextsLoaded, message)
    }

    private void write_without_context(def aid, Long adid, String name, LoadResultType resultType, Integer numPotentialContextsToLoad,
                       Integer numExistingContextsInDb, int numContextsLoaded, String message) {

        def lineData = [aid, adid, name, resultType, numPotentialContextsToLoad, numExistingContextsInDb,
                numContextsLoaded, message]
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
