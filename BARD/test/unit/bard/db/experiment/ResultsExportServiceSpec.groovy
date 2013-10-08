package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bardqueryapi.compoundBioActivitySummary.CompoundBioActivitySummaryBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import java.util.zip.GZIPInputStream

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ResultsExportService)
@Build([Substance, Element])
@Unroll
class ResultsExportServiceSpec extends Specification {

    static File destination = new File("out/resultExportServiceTest.txt.gz")

    String JSON_RESULT_2 = '''
{"sid":152255054,"rootElem":[{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Inactive","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":0.0,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":959,"resultType":"AC50","valueDisplay":"NA","relationship":"supported by","related":[{"resultTypeId":920,"resultType":"Hill s0","valueNum":7.19385,"valueDisplay":"7.19385","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":921,"resultType":"Hill sinf","valueNum":7.19385,"valueDisplay":"7.19385","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":15.308,"valueDisplay":"15.308 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":28.5,"valueDisplay":"28.5 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":5.625,"replicateNumber":1,"valueDisplay":"5.625 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.0,"valueDisplay":"0.0 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":2.753,"replicateNumber":1,"valueDisplay":"2.753 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":1.0E-6,"valueDisplay":"1.0E-6 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":7.151,"replicateNumber":1,"valueDisplay":"7.151 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":2.0E-6,"valueDisplay":"2.0E-6 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":6.458,"replicateNumber":1,"valueDisplay":"6.458 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":6.0E-6,"valueDisplay":"6.0E-6 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":7.78,"replicateNumber":1,"valueDisplay":"7.78 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":1.8E-5,"valueDisplay":"1.8E-5 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":11.789,"replicateNumber":1,"valueDisplay":"11.789 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":5.0E-5,"valueDisplay":"5.0E-5 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":10.807,"replicateNumber":1,"valueDisplay":"10.807 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":1.6E-4,"valueDisplay":"1.6E-4 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":8.738,"replicateNumber":1,"valueDisplay":"8.738 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":4.6E-4,"valueDisplay":"4.6E-4 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":5.568,"replicateNumber":1,"valueDisplay":"5.568 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.00135,"valueDisplay":"0.00135 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":9.359,"replicateNumber":1,"valueDisplay":"9.359 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.0042,"valueDisplay":"0.0042 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":8.114,"replicateNumber":1,"valueDisplay":"8.114 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.012,"valueDisplay":"0.012 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":3.822,"replicateNumber":1,"valueDisplay":"3.822 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.038,"valueDisplay":"0.038 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":11.005,"replicateNumber":1,"valueDisplay":"11.005 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.11,"valueDisplay":"0.11 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":9.329,"replicateNumber":1,"valueDisplay":"9.329 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":0.35,"valueDisplay":"0.35 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-1.897,"replicateNumber":1,"valueDisplay":"-1.897 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":1.0,"valueDisplay":"1.0 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":1.861,"replicateNumber":1,"valueDisplay":"1.861 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":3.0,"valueDisplay":"3.0 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":5.334,"replicateNumber":1,"valueDisplay":"5.334 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":9.0,"valueDisplay":"9.0 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":15.308,"replicateNumber":1,"valueDisplay":"15.308 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":28.5,"valueDisplay":"28.5 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":10.56,"replicateNumber":1,"valueDisplay":"10.56 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"valueNum":80.0,"valueDisplay":"80.0 uM"}]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":40.0,"valueDisplay":"40.0"}]}],"contextItems":[]}]}
'''

    String JSON_RESULT = '''
{"sid":855836,"rootElem":[{"resultTypeId":961,"resultType":"EC50","valueNum":0.104,"replicateNumber":1,"valueDisplay":"<0.104 uM","qualifier":"<","related":[{"resultTypeId":986,"resultType":"percent activity","valueNum":-79.9,"replicateNumber":1,"valueDisplay":"-79.9 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.104,"valueDisplay":"0.104 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-84.04,"replicateNumber":1,"valueDisplay":"-84.04 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.208,"valueDisplay":"0.208 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-87.56,"replicateNumber":1,"valueDisplay":"-87.56 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.417,"valueDisplay":"0.417 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-92.56,"replicateNumber":1,"valueDisplay":"-92.56 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.833,"valueDisplay":"0.833 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-88.46,"replicateNumber":1,"valueDisplay":"-88.46 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":1.667,"valueDisplay":"1.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-89.71,"replicateNumber":1,"valueDisplay":"-89.71 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":3.333,"valueDisplay":"3.333 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-91.98,"replicateNumber":1,"valueDisplay":"-91.98 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":6.667,"valueDisplay":"6.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-88.81,"replicateNumber":1,"valueDisplay":"-88.81 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":13.333,"valueDisplay":"13.333 uM"}]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":-92.56,"replicateNumber":1,"valueDisplay":"-92.56 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":24.0,"valueDisplay":"24.0"}]},{"resultTypeId":961,"resultType":"EC50","valueNum":0.00682,"replicateNumber":2,"valueDisplay":"0.00682 uM","qualifier":"=","related":[{"resultTypeId":921,"resultType":"Hill sinf","valueNum":-89.97,"replicateNumber":2,"valueDisplay":"-89.97","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-7.345,"replicateNumber":2,"valueDisplay":"-7.345 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":4.07E-4,"valueDisplay":"4.07E-4 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-7.692,"replicateNumber":2,"valueDisplay":"-7.692 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":8.14E-4,"valueDisplay":"8.14E-4 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-9.456,"replicateNumber":2,"valueDisplay":"-9.456 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.001628,"valueDisplay":"0.001628 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-16.43,"replicateNumber":2,"valueDisplay":"-16.43 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.003255,"valueDisplay":"0.003255 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-45.8,"replicateNumber":2,"valueDisplay":"-45.8 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.00651,"valueDisplay":"0.00651 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-79.55,"replicateNumber":2,"valueDisplay":"-79.55 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.013201,"valueDisplay":"0.013201 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-86.96,"replicateNumber":2,"valueDisplay":"-86.96 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.026042,"valueDisplay":"0.026042 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-89.19,"replicateNumber":2,"valueDisplay":"-89.19 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.052,"valueDisplay":"0.052 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-89.95,"replicateNumber":2,"valueDisplay":"-89.95 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.104,"valueDisplay":"0.104 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-91.31,"replicateNumber":2,"valueDisplay":"-91.31 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.208,"valueDisplay":"0.208 uM"}]},{"resultTypeId":962,"resultType":"log EC50","valueNum":-8.166,"replicateNumber":2,"valueDisplay":"-8.166 uM","qualifier":"=","relationship":"supported by","related":[{"resultTypeId":1335,"resultType":"standard error","valueNum":0.008083,"replicateNumber":2,"valueDisplay":"0.008083","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":-91.31,"replicateNumber":2,"valueDisplay":"-91.31 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":920,"resultType":"Hill s0","valueNum":-7.648,"replicateNumber":2,"valueDisplay":"-7.648","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":919,"resultType":"Hill coefficient","valueNum":2.879,"replicateNumber":2,"valueDisplay":"2.879","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":20.0,"valueDisplay":"20.0"}]},{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":76.0,"valueDisplay":"76.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":76.0,"valueDisplay":"76.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueNum":2.0,"replicateNumber":1,"valueDisplay":"2.0","qualifier":"=","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":70.0,"replicateNumber":1,"valueDisplay":"70.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueNum":2.0,"replicateNumber":2,"valueDisplay":"2.0","qualifier":"=","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":82.0,"replicateNumber":2,"valueDisplay":"82.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]}],"contextItems":[]}]}
'''

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

    void 'test readResultsForSubstance'() {
        given:
        ObjectMapper mapper = new ObjectMapper()
        ObjectReader reader = mapper.reader(JsonSubstanceResults)
        when:
        JsonSubstanceResults substanceResults = service.readResultsForSubstance(reader, JSON_RESULT)
        then:
        assert substanceResults
        assert substanceResults.sid == 855836
        final List<JsonResult> elem = substanceResults.rootElem
        assert !elem.isEmpty()
        final JsonResult jsonResult = substanceResults.rootElem.get(0)
        CompoundBioActivitySummaryBuilder.jsonResultToConcentrationResponse(elem)
        assert jsonResult

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
