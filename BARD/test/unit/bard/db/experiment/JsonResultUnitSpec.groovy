package bard.db.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/26/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
class JsonResultUnitSpec extends Specification {
    def 'test serialization'() {
        when:
        def contextItem = new JsonResultContextItem(qualifier: "<", valueMax: 200, valueMin: -200, valueNum: 102, valueDisplay: "value", itemId: 100, attribute: "attribute", valueElementId: 100);
        def child = new JsonResult(qualifier: "=", replicateNumber: 1, resultId: 100, resultTypeId: 101, resultType: "AC50", valueDisplay: "valueDisplay", valueMax: 1000, valueMin: 0, valueNum: 10, relationship: "Derives", statsModifierId: 15)
        def parent = new JsonResult(qualifier: "=", replicateNumber: 1, resultId: 100, resultTypeId: 101, resultType: "AC50", valueDisplay: "valueDisplay", valueMax: 1000, valueMin: 0, valueNum: 10, related: [child], contextItems: [contextItem], statsModifierId: 15)
        def substanceResults = new JsonSubstanceResults(sid: 100, rootElem: [parent])

        ObjectMapper mapper = new ObjectMapper()
        def serialized = mapper.writeValueAsString(substanceResults)

        def w = mapper.writer().withDefaultPrettyPrinter();
        println(w.writeValueAsString(substanceResults))
//        println(w.writeValueAsString(mapper.generateJsonSchema(JsonResult.class).schemaNode));
//        println(w.writeValueAsString(mapper.generateJsonSchema(JsonResultContextItem.class).schemaNode));

        println()

        then:
        serialized == "{\"resultId\":100,\"resultTypeId\":101,\"resultType\":\"AC50\",\"valueNum\":10.0,\"valueMin\":0.0,\"valueMax\":1000.0,\"replicateNumber\":1,\"valueDisplay\":\"valueDisplay\",\"qualifier\":\"=\"}"
    }
}
