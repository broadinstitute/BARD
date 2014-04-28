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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.SerializationFeature

import java.util.zip.GZIPInputStream
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

    JsonSubstanceResults readResultsForSubstance(ObjectReader reader, String resultRow) {
        JsonSubstanceResults substanceResults = reader.readValue(resultRow)
        return substanceResults
    }

    List<JsonSubstanceResults> readResultsForSubstances(Experiment experiment, int numberOfRecords = 10) {
        final FileInputStream export = this.archivePathService.getEtlExport(experiment)
        ObjectReader reader = mapper.reader(JsonSubstanceResults)

        BufferedReader lineReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(export)));

        List<JsonSubstanceResults> jsonSubstanceResultsList = []
        int counter = numberOfRecords
        String line = null
        while ((line = lineReader.readLine()) != null) {
            if(counter == 0){ //terminate either when we finish reading file or the of records to preview is exceeded
                break
            }
            if (line?.trim()) {
                JsonSubstanceResults substanceResults = readResultsForSubstance(reader, line.trim())
                jsonSubstanceResultsList.add(substanceResults)
                counter--
            }

        }
        export.close()
        return jsonSubstanceResultsList
    }

    void writeResultsForSubstance(Writer writer, Long sid, List<Result> results) {
        JsonSubstanceResults substanceResults = transformToJson(sid, results)
        writeJsonResults(writer, substanceResults)
    }

    void writeJsonResults(Writer writer, JsonSubstanceResults substanceResults) {
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
