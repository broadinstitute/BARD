package bard.db.experiment

import bard.db.dictionary.Element

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import grails.buildtestdata.mixin.Build
import spock.lang.Specification
import spock.lang.Unroll

import java.util.zip.GZIPInputStream

import grails.test.mixin.*
import grails.test.mixin.support.*
import bard.db.enums.HierarchyType

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ResultsExportService)
@Build([Substance,Element])
@Unroll
class ResultsExportServiceSpec extends Specification {

    static File destination = new File("out/resultExportServiceTest.txt.gz")

    void 'test contextItemAsJson'() {
        given:
        Substance substance = Substance.build(smiles: 'CC')
        Element attributeElement = Element.build(externalURL: "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", label: "someLabel")

        Result child = new Result(substanceId: substance.id, valueDisplay: "http://unprot.com")
        ResultContextItem contextItem = new ResultContextItem(extValueId: "222", valueDisplay: "http://unprot.com?term=222", attributeElement: attributeElement, result: child)
        when:
        JsonResultContextItem jsonResultContextItem = service.contextItemAsJson(contextItem)
        then:
        assert jsonResultContextItem
        assert contextItem.valueDisplay == jsonResultContextItem.valueDisplay
        assert !jsonResultContextItem.valueElementId
        assert attributeElement.id == jsonResultContextItem.attributeId
        assert contextItem.id == jsonResultContextItem.itemId
        assert attributeElement.label == jsonResultContextItem.attribute
        assert !jsonResultContextItem.qualifier
        assert !jsonResultContextItem.valueNum
        assert !jsonResultContextItem.valueMin
        assert !jsonResultContextItem.valueMax
    }

    void 'test transform to json'() {
        setup:

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

        ResultHierarchy relationship = new ResultHierarchy(parentResult: parent, result: child, hierarchyType: HierarchyType.SUPPORTED_BY)
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
        tChild.relationship == HierarchyType.SUPPORTED_BY.getId()
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
        new File("out").mkdirs();
        ArchivePathService archivePathService = Mock(ArchivePathService)
        this.service.archivePathService = archivePathService
        archivePathService.prepareForWriting("path") >> destination

        Substance substance1 = new Substance()
        substance1.id = 1

        Substance substance2 = new Substance()
        substance2.id = 2

        List results = [new Result(resultType: new Element(label: "ac50"), substanceId: substance1.id),
                new Result(resultType: new Element(label: "bingo"), substanceId: substance2.id)]

        when:
        this.service.dumpFromList("path", results)

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
