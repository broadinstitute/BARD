package bard.db.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.hibernate.Session

import java.util.zip.GZIPOutputStream

class ResultsExportService {
    ObjectMapper mapper = new ObjectMapper()

    def contextItemAsJson(ResultContextItem item) {
        return new JsonResultContextItem(itemId: item.id,
            attribute: item.attributeElement.label,
            qualifier: item.qualifier?.trim(),
            valueNum: item.valueNum,
            valueMin: item.valueMin,
            valueMax: item.valueMax,
            valueDisplay: item.valueDisplay,
            valueElementId: item.valueElementId)
    }


    def convertToJson(Result result) {
        List related = []
        result.resultHierarchiesForParentResult.each {
            def child = convertToJson(it.result);
            child.relationship = it.hierarchyType.value
            related.add(child);
        }

        List contextItems = []
        result.resultContextItems.each {
            contextItems.add(contextItemAsJson(it));
        }

        return new JsonResult(resultId: result.id,
            resultTypeId: result.resultTypeId,
            statsModifierId: result.statsModifierId,
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

    def writeResultsForSubstance(Writer writer, Long sid, List<Result> results) {
        List<Result> roots = results.findAll { it.resultHierarchiesForResult.size() == 0 }

        List resultsAsJson = roots.collect { convertToJson(it) }

        JsonSubstanceResults substanceResults = new JsonSubstanceResults(sid: sid, rootElem: resultsAsJson)

        writer.write(mapper.writeValueAsString(substanceResults));

        writer.write("\n\n");
    }

    def dumpFromList(String filename, Collection<Result> results) {
        Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename)));
        Map<Long, List<Result>> resultsBySid = results.groupBy { it.substanceId }
        int count = 0;
        for (sid in resultsBySid.keySet()) {
            writeResultsForSubstance(writer, sid, resultsBySid[sid])
            count++;
            if ((count % 100) == 0) {
                println("dumpFromList progress: ${count}/${resultsBySid.size()}")
            }
        }
        writer.close()
    }

    def dumpFromDb(Long experimentId) {
        List sids = []
        Result.withSession { Session session ->
            sids.addAll(session.createSQLQuery("select distinct r.substance_id from RESULT r where r.experiment_id = ?").setCacheable(false).setParameter(0, experimentId).list());
        }

        println("${sids.size()} Substance IDs for experiment ${experimentId}")

        Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("exp-${experimentId}.json.gz")));

        int counter = 0;
        for (sid in sids) {
            Result.withSession { Session session ->
                List<Result> results = session.createQuery("select r from ${Result.getName()} r where experiment.id = ? and substance.id = ?").setParameter(0, experimentId).setParameter(1, sid.toLong()).list();

                writeResultsForSubstance(writer, sid, results)

                if ((counter % 50) == 0) {
                    session.clear();
                    println("${ (int) (100 * counter / sids.size()) } %")
                }

                counter++
            }
        }
        writer.close()

    }
}
