package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.registration.Assay
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import spock.lang.Specification

import java.util.zip.GZIPInputStream

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ResultsExportServiceSpec extends Specification {

    static File destination = new File("out/resultExportServiceTest.txt.gz")

    void 'test transform to json' () {
        setup:
        ResultsExportService service = new ResultsExportService()

        Substance substance = new Substance()
        substance.id = 100

        Element ac50 = new Element(label: "ac50")
        ac50.id = 21
        Element mean = new Element(label: "mean")
        mean.id = 20
        Element concentration = new Element(label: "concentration")
        concentration.id = 22

        Result parent = new Result(substanceId: substance.id, resultType: ac50, replicateNumber: 1, qualifier: "= ", valueDisplay: "200 uM", valueNum: 200)
        Result child = new Result(substanceId: substance.id, resultType: ac50, statsModifier: mean, qualifier: "< ", valueDisplay: "10-20", valueMin: 10, valueMax: 20)
        ResultContextItem contextItem = new ResultContextItem(valueNum: 1.0, valueDisplay: "1 uM", qualifier: "> ", attributeElement: concentration, result: child)
        child.resultContextItems.add(contextItem)

        ResultHierarchy relationship = new ResultHierarchy(parentResult: parent, result: child, hierarchyType: HierarchyType.Child)
        parent.getResultHierarchiesForParentResult().add(relationship)
        child.getResultHierarchiesForResult().add(relationship)

        List results = [parent, child]

        when:
        JsonSubstanceResults transformed = service.transformToJson(100, results)

        then:
        transformed.sid == 100
        transformed.rootElem.size() == 1

        JsonResult tParent = transformed.rootElem.first()
        tParent.relationship == null
        tParent.qualifier == "="
        tParent.valueDisplay == "200 uM"
        tParent.valueNum == 200.0
        tParent.valueMin == null
        tParent.valueMax == null
        tParent.replicateNumber == 1
        tParent.statsModifierId == null;
        tParent.resultType == "ac50"
        tParent.resultTypeId == 21
        tParent.contextItems.size() == 0
        tParent.related.size() == 1

        JsonResult tChild = tParent.related.first()
        tChild.relationship == "Child"
        tChild.qualifier == "<"
        tChild.valueDisplay == "10-20"
        tChild.valueNum == null
        tChild.valueMin == 10.0
        tChild.valueMax == 20.0
        tChild.statsModifierId == 20;
        tChild.resultType == "ac50 (mean)"
        tChild.resultTypeId == 21
        tChild.related.size() == 0
        tChild.contextItems.size() == 1

        JsonResultContextItem tItem = tChild.contextItems.first()
        tItem.qualifier == ">"
        tItem.attribute == "concentration"
        tItem.attributeId == 22
        tItem.valueDisplay == "1 uM"
        tItem.valueNum == 1.0
    }

    void 'test dumpFromList'() {
        setup:
        ArchivePathService archivePathService = Mock(ArchivePathService)
        archivePathService.prepareForWriting("path") >> destination

        Substance substance1 = new Substance()
        substance1.id = 1

        Substance substance2 = new Substance()
        substance2.id = 2

        ResultsExportService service = new ResultsExportService()
        service.archivePathService = archivePathService
        List results = [new Result(substanceId: substance1.id),
            new Result(substanceId: substance2.id)]

        when:
        service.dumpFromList("path", results)

        then:
        destination.exists()

        when:
        // parse into json objects
        ObjectMapper mapper = new ObjectMapper()
        ObjectReader reader = mapper.reader(JsonSubstanceResults)

        BufferedReader lineReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(destination))));

        JsonSubstanceResults substanceResults1 = reader.readValue(lineReader.readLine())

        then:
        lineReader.readLine() == ""

        when:
        JsonSubstanceResults substanceResults2 = reader.readValue(lineReader.readLine())

        then:
        lineReader.readLine() == ""
        lineReader.readLine() == null
    }
}
