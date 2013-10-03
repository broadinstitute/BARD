package bard.db.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

import java.util.zip.GZIPOutputStream

class ResultsExportService {

    ArchivePathService archivePathService

    ObjectMapper mapper = new ObjectMapper()
    ObjectMapper prettyPrintMapper

    public ResultsExportService() {
        prettyPrintMapper = new ObjectMapper()
        prettyPrintMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    JsonResultContextItem contextItemAsJson(ResultContextItem item) {
        return new JsonResultContextItem(itemId: item.id,
            attribute: item.attributeElement.label,
            attributeId: item.attributeElement.id,
            qualifier: item.qualifier?.trim(),
            valueNum: item.valueNum,
            valueMin: item.valueMin,
            valueMax: item.valueMax,
            valueDisplay: item.valueDisplay.trim(),
            valueElementId: item.valueElement?.id,
            extValueId: item?.extValueId?.trim())
    }

    JsonResult convertToJson(Result result) {
        List related = []
        result.resultHierarchiesForParentResult.each {
            def child = convertToJson(it.result);
            child.relationship = it.hierarchyType.id
            related.add(child);
        }

        List contextItems = []
        result.resultContextItems.each {
            contextItems.add(contextItemAsJson(it));
        }

        return new JsonResult(resultId: result.id,
            resultTypeId: result.resultType?.id,
            statsModifierId: result.statsModifier?.id,
            resultType: result.displayLabel,
            valueNum: result.valueNum,
            valueMin: result.valueMin,
            valueMax: result.valueMax,
            replicateNumber: result.replicateNumber,
            valueDisplay: result.valueDisplay,
            qualifier: result.qualifier?.trim(),
            related: related,
            contextItems: contextItems)
    }

    JsonSubstanceResults transformToJson(Long sid, List<Result> results) {
        List<Result> roots = results.findAll { it.resultHierarchiesForResult.size() == 0 }

        List resultsAsJson = roots.collect { convertToJson(it) }

        return new JsonSubstanceResults(sid: sid, rootElem: resultsAsJson)
    }

    void writeResultsForSubstance(Writer writer, Long sid, List<Result> results) {
        JsonSubstanceResults substanceResults = transformToJson(sid, results)

        writer.write(mapper.writeValueAsString(substanceResults));

        writer.write("\n\n");
    }

    String resultsToPrettyPrintString(Long sid, List<Result> results) {
        JsonSubstanceResults substanceResults = transformToJson(sid, results)
        return prettyPrintMapper.writeValueAsString(substanceResults)
    }


    void dumpFromList(String filename, Collection<Result> results) {
        File file = archivePathService.prepareForWriting(filename)
        dumpFromListToAbsPath(file, results);
    }

    void dumpFromListToAbsPath(File file, Collection<Result> results) {
        Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)));
        Map<Long, List<Result>> resultsBySid = results.groupBy { it.substanceId }
        for (sid in resultsBySid.keySet()) {
            writeResultsForSubstance(writer, sid, resultsBySid[sid])
        }
        writer.close()
    }

    Writer createWriter(String filename) {
        File absPath = archivePathService.prepareForWriting(filename)
        Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(absPath)));
        return writer
    }

//    void dumpFromDb(Long experimentId) {
//        Experiment experiment = Experiment.get(experimentId)
//        assert experiment != null
//        List<Result> results = bulkResultService.findResults(experiment)
//
//        println("writing ${results.size()} results")
//
//        dumpFromListToAbsPath(new File("exp-${experimentId}.json.gz"), results)
//    }
}
