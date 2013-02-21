import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.hibernate.Session

import java.util.zip.GZIPOutputStream

List sids = []

String experimentIdStr = System.getProperty("experimentId")
if (experimentIdStr == null) {
    throw new RuntimeException("Need to run with -DexperimentId=EXPERIMENT")
}

Long experimentId = Long.parseLong(experimentIdStr);

Result.withSession { Session session ->
    sids.addAll(session.createSQLQuery("select distinct r.substance_id from RESULT r where r.experiment_id = ?").setCacheable(false).setParameter(0, experimentId).list());
}

println("${sids.size()} Substance IDs for experiment ${experimentId}")

def contextItemAsJson(ResultContextItem item) {
    JSONObject obj = new JSONObject()
    obj.put("itemId", item.id)
    obj.put("attribute", item.attributeElement.label)
    obj.put("qualifier", item.qualifier)
    obj.put("valueNum", item.valueNum);
    obj.put("valueMin", item.valueMin);
    obj.put("valueMax", item.valueMax);
    obj.put("valueDisplay", item.valueDisplay)
    obj.put("valueElementId", item.valueElementId)
    return obj;
}

def convertToJson(Result result) {
    JSONObject obj = new JSONObject()
    obj.put("resultId", result.id)
    obj.put("resultTypeId", result.resultTypeId)
    obj.put("statsModifierId", result.statsModifierId)
    obj.put("resultType", result.displayLabel)
    obj.put("valueNum", result.valueNum);
    obj.put("valueMin", result.valueMin);
    obj.put("valueMax", result.valueMax);
    obj.put("replicateNumber", result.replicateNumber);
    obj.put("displayVal", result.valueDisplay)

    JSONArray related = new JSONArray();
    result.resultHierarchiesForParentResult.each {
        JSONObject child = convertToJson(it.result);
        child.put("relationship", it.hierarchyType.value);
        related.add(child);
    }

    JSONArray contextItems = new JSONArray();
    result.resultContextItems.each {
        contextItems.add(contextItemAsJson(it));
    }

    obj.put("related", related);
    obj.put("contextItems", contextItems);
}

Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("exp-${experimentId}.json.gz")));

int counter = 0;
for(sid in sids) {
    Result.withSession { Session session ->
        List<Result> results = session.createQuery("select r from ${Result.getName()} r where experiment.id = ? and substance.id = ?").setParameter(0, experimentId).setParameter(1, sid.toLong()).list();

        List<Result> roots = results.findAll { it.resultHierarchiesForResult.size() == 0 }

        List resultsAsJson = roots.collect { convertToJson(it) }

        if((counter%100) == 0) {
            session.clear();
            println("${100*counter/sids.size()} %")
        }

        JSONObject obj = new JSONObject()
        obj.put("sid", sid)
        obj.put("rootElem", new JSONArray(resultsAsJson))

        writer.write(obj.toString())
        writer.write("\n\n");

        counter++
    }
    if (counter > 100)
        break;
}
writer.close()
