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

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
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
