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

package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.enums.ValueType
import bard.db.experiment.*
import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.RawCell
import bard.db.experiment.results.Row
import bard.db.experiment.results.RowParser
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.enums.ExpectedValueType.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(ServiceUnitTestMixin)
@Build([Assay, AssayContext, AssayContextItem, AssayContextExperimentMeasure, Element, Substance, Experiment, ExperimentMeasure])
@Mock([Assay, AssayContext, AssayContextItem, AssayContextExperimentMeasure, Element, Substance, Experiment, ExperimentMeasure])
@TestFor(ResultsService)
@Unroll
class ResultsServiceSpec extends Specification {

    void setup() {
        ItemService itemService = new ItemService()  // Setup logic here
        service.itemService = itemService
    }

    void 'test create parent child measure with relationship expressed by #desc'() {
        when:
        def substance = Substance.build()
        substance.save()

        then:
        substance.id != null

        when:

        def parentResultType = Element.build(label: "parent")
        def parentExperimentMeasure = ExperimentMeasure.build(resultType: parentResultType)
        def parentCell = new RawCell(columnName: "parent", value: "1")

        def childResultType = Element.build(label: "child")
        def childMeasure = ExperimentMeasure.build(resultType: childResultType, parent: parentExperimentMeasure, parentChildRelationship: HierarchyType.CALCULATED_FROM)
        def childCell = new RawCell(columnName: "child", value: "1")

//        //ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build(parent: parentMeasure)
//        ExperimentMeasure childExperimentMeasure = ExperimentMeasure.build(parent: parentExperimentMeasure, childMeasures: [childMeasure] as Set<ExperimentMeasure>, parentChildRelationship: HierarchyType.CALCULATED_FROM)
//        parentExperimentMeasure.childMeasures.add(childExperimentMeasure)

        List<Row> rows;
        if (onSameLine) {
            def row = new Row(rowNumber: 1, sid: substance.id, cells: [parentCell, childCell], replicate: 1)
            rows = [row]
        } else {
            def row0 = new Row(rowNumber: 1, sid: substance.id, cells: [parentCell], replicate: 1)
            def row1 = new Row(rowNumber: 2, parentRowNumber: 1, sid: substance.id, cells: [childCell], replicate: 1)
            rows = [row0, row1]
        }

        def errors = new ImportSummary()

        def results = service.createResults(rows, [parentExperimentMeasure, childMeasure], errors, [:])

        then:
        !errors.hasErrors()
        results.size() == 2

        // we got a parent and child element
        def parentResult = results.find { it.resultType == parentResultType }
        parentResult != null
        def childResult = results.find { it.resultType == childResultType }
        childResult != null

        // we have one relationship which is used by both elements
        parentResult.resultHierarchiesForResult.size() == 0
        parentResult.resultHierarchiesForParentResult.size() == 1
        childResult.resultHierarchiesForResult.size() == 1
        childResult.resultHierarchiesForParentResult.size() == 0

        // contain the same element
        childResult.resultHierarchiesForResult == parentResult.resultHierarchiesForParentResult

        def relationship = childResult.resultHierarchiesForResult.first()
        relationship.hierarchyType == HierarchyType.CALCULATED_FROM

        where:
        desc              | onSameLine
        "on same row"     | true
        "using parent id" | false
    }

    void 'test create template from assay'() {
        when:
        // an assay with 1 measure, and two contexts, one of which is associated with the measure
        // yes, this is a lot scaffolded data, but I think that is more a result of the design of the data model
        // and I prefer using the real domain then mocking much of it out.
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        AssayContext assayContext = AssayContext.build(assay: assay)
        AssayContext measureContext = AssayContext.build(assay: assay)

        AssayContextItem.build(assayContext: assayContext, attributeType: AttributeType.Free, attributeElement: Element.build(label: "cell line", expectedValueType: FREE_TEXT),valueDisplay: null)
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(resultType: Element.build(label: "ec50"))
        AssayContextExperimentMeasure assayContextExperimentMeasure = AssayContextExperimentMeasure.build(assayContext: measureContext, experimentMeasure: experimentMeasure)
        AssayContextItem.build(assayContext: measureContext, attributeType: AttributeType.Free, attributeElement: Element.build(label: "hill slope", expectedValueType: NUMERIC),valueDisplay: null)
        measureContext.assayContextExperimentMeasures = [assayContextExperimentMeasure] as Set

        assay.assayContexts = [assayContext, measureContext]
        //assay.measures = [experimentMeasure] as Set
        experiment.experimentMeasures = [experimentMeasure] as Set


        ResultsService.Template template = service.generateMaxSchema(experiment)

        then:
        template.constantItems.size() == 1
        template.constantItems.get(0) == "cell line"
        template.experiment == experiment
        template.columns.size() == 2
        template.columns.get(0) == "ec50"
        template.columns.get(1) == "hill slope"
    }

    def createSampleFile() {
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure inhibitionMeasure = ExperimentMeasure.build()
        ExperimentMeasure ec50Measure = ExperimentMeasure.build()

//        def columns = [new ResultsService.Column("Inhibition", inhibitionMeasure), new ResultsService.Column("EC50", ec50Measure)]
        def constantItems = []
        ResultsService.Template template = new ResultsService.Template(experiment: experiment, columns: ["Inhibition", "EC50"], constantItems: constantItems)

        String sample = ",Experiment ID,123\n" +
                "\n" +
                "Row #,Substance,Replicate #,Parent Row #,Inhibition,EC50\n" +
                "1,100,,,10,\n" +
                "2,100,,1,,5\n"

        return [sample: sample, template: template]
    }

    void 'test parsing experiment level #desc'() {
        setup:
        ImportSummary errors = new ImportSummary()
        Element attribute = Element.build(label: "column", expectedValueType: NUMERIC)
        def item = service.itemService.getLogicalItems([AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.Free, valueDisplay: null)])[0]

        when:
        String sample = ",Experiment ID,123\n,column," + cellString + "\n"
        BufferedReader reader = new BufferedReader(new StringReader(sample))
        RowParser RowParser = service.parseConstantRegion(new ResultsService.LineReader(reader), errors, [item], false)

        then:
        RowParser.contexts.size() == 1
        ExperimentContext context = RowParser.contexts.first()
        context.contextItems.size() == 1
        ExperimentContextItem contextItem = context.contextItems.first()
        contextItem.attributeElement == attribute
        contextItem.valueMin == minVal
        contextItem.valueMax == maxVal
        contextItem.valueNum == expectedValue
        contextItem.qualifier == expectedQualifier
        contextItem.valueDisplay == expectedValueDisplay

        where:
        desc                  | cellString | expectedValue | expectedQualifier | minVal | maxVal | expectedValueDisplay
        "simple scalar"       | "1"        | 1.0           | "= "              | null   | null   | "1.0"
        "scientific notation" | "1e4"      | 1e4           | "= "              | null   | null   | "10000.0"
        "including qualifier" | "<10"      | 10.0          | "< "              | null   | null   | "<10.0"
        "spaced qualifier"    | ">> 10"    | 10.0          | ">>"              | null   | null   | ">>10.0"
        "range"               | "2-3"      | null          | null              | 2.0    | 3.0    | "2.0-3.0"
    }

    void 'test parse experiment level list item'() {
        setup:

        def tubaElement = Element.build(label: "tuba")
        def attribute = Element.build(label: "column", expectedValueType: ELEMENT)
        def context = AssayContext.build(contextName: "instrument")
        def tubaItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: tubaElement, valueDisplay: tubaElement.label, assayContext: context)
        def trumpetElement = Element.build(label: "trumpet")
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: trumpetElement, valueDisplay: trumpetElement.label, assayContext: context)
        def item = this.service.itemService.getLogicalItems([tubaItem, trumpetItem])[0]

        ImportSummary errors = new ImportSummary()

        when:
        String sample = ",Experiment ID,123\n,column,trumpet\n"
        BufferedReader reader = new BufferedReader(new StringReader(sample))

        RowParser RowParser = service.parseConstantRegion(new ResultsService.LineReader(reader), errors, [item], false)

        then:
        !errors.hasErrors()

        RowParser.contexts.size() == 1
        ExperimentContext expContext = RowParser.contexts.first()
        expContext.contextName == "instrument"
        expContext.contextItems.size() == 1
        ExperimentContextItem expItem = expContext.contextItems.first()
        expItem.attributeElement == attribute
        expItem.valueElement == trumpetElement
    }

    void 'test initial parse'() {
        when:
        def fixture = createSampleFile()

        ImportSummary errors = new ImportSummary()
        RowParser parser = service.initialParse(new StringReader(fixture.sample), errors, fixture.template, false)
        List<Row> rows = parser.readNextSampleRows()

        then:
        parser.experimentId == 123
        rows.size() == 2
        !errors.hasErrors()

        when:
        Row row0 = rows.get(0)
        Row row1 = rows.get(1)

        then:
        row0.parentRowNumber == null
        row0.cells.size() == 1
        row0.cells.get(0).value == "10"
        row0.cells.get(0).columnName == "Inhibition"
        row0.sid == 100
        row0.rowNumber == 1
        row0.replicate == null

        row1.parentRowNumber == 1
        row1.cells.size() == 1
        row1.cells.get(0).value == "5"
        row1.cells.get(0).columnName == "EC50"

        when:
        rows = parser.readNextSampleRows()

        then:
        rows == null
    }

    void 'test parse failures #desc'() {
        when:
        def fixture = createSampleFile()
        // break apart table
        def table = fixture.sample.split("\n").collect { it.split(",") as List }
        // mutate
        def r = table.get(row)
        while (column >= r.size()) {
            r.add("")
        }
        r.set(column, newValue)
        // reassemble
        def sample = (table.collect { it.join(",") }).join("\n")

        ImportSummary errors = new ImportSummary()
        RowParser parser = service.initialParse(new StringReader(sample), errors, fixture.template, false)
        if(!errors.hasErrors()) {
            parser.readNextSampleRows();
        }

        then:
        errors.errors.size() == expectedErrorCount

        where:
        desc                   | row | column | newValue | expectedErrorCount
        "bad constant section" | 0   | 0      | "x"      | 1
        "bad constant name"    | 0   | 1      | "x"      | 1
        "bad fixed column"     | 2   | 0      | "x"      | 1
        "bad dynamic column"   | 2   | 4      | "x"      | 1
        "duplicate column"     | 2   | 4      | "EC50"   | 1
        "duplicate row number" | 4   | 0      | "1"      | 1
        "extra column"         | 3   | 6      | "1"      | 1
        "missing row number"   | 3   | 0      | ""       | 1
    }

    void 'test parsing cell containing #cellString'() {
        when:
        ExperimentMeasure measure = ExperimentMeasure.build()
        Result result = service.createResult(null, measure, cellString, 1, null)

        then:
        result.valueNum == expectedValue
        result.valueMax == maxVal
        result.valueMin == minVal
        result.qualifier == expectedQualifier
        result.valueDisplay == displayValue

        where:
        desc                  | cellString  | expectedValue | expectedQualifier | minVal | maxVal | displayValue
        "simple scalar"       | "1"         | 1.0           | "= "              | null   | null   | "1.0"
        "scientific notation" | "1e4"       | 1e4           | "= "              | null   | null   | "10000.0"
        "sci notation2"       | "7.58e-005" | 7.58e-5f      | "= "              | null   | null   | "7.58E-5"
        "including qualifier" | "<10"       | 10.0          | "< "              | null   | null   | "<10.0"
        "spaced qualifier"    | ">> 10"     | 10.0          | ">>"              | null   | null   | ">>10.0"
        "range"               | "2-3"       | null          | null              | 2.0    | 3.0    | "2.0-3.0"
        "nonrange"            | "non-info"  | null          | null              | null   | null   | "non-info"
        "free text"           | "free"      | null          | null              | null   | null   | "free"
    }

    void 'test creating measure result'() {
        when:
        def substance = Substance.build()
        substance.save()

        then:
        substance.id != null

        when:

        def resultType = Element.build(label: "x")
        def experimentMeasure = ExperimentMeasure.build(resultType: resultType)
        def row = new Row(rowNumber: 1, sid: substance.id, cells: [new RawCell(columnName: "x", value: "5")], replicate: 1)

        def errors = new ImportSummary()

        def results = service.createResults([row], [experimentMeasure], errors, [:])

        then:
        !errors.hasErrors()
        results.size() == 1
        Result result = results.first()
        result.replicateNumber == 1
        result.qualifier == "= "
        result.valueNum == 5.0
        result.resultType == resultType
        result.substanceId == substance.id
    }

    void 'test creating measure and item result'() {
        when:

        def substance = Substance.build()
        substance.save()

        def attribute = Element.build(label: "item", expectedValueType: NUMERIC)
        def item = this.service.itemService.getLogicalItems([AssayContextItem.build(attributeType: AttributeType.Free, valueType: ValueType.NONE, valueDisplay: null, attributeElement: attribute)])[0]
        def resultType = Element.build(label: "measure")
        def experimentMeasure = ExperimentMeasure.build(resultType: resultType)

        // construct a row of two cells: a measurement and an associated context
        def mCell = new RawCell(columnName: "measure", value: "5")
        def iCell = new RawCell(columnName: "item", value: "<15")
        def row = new Row(rowNumber: 1, replicate: 1, sid: substance.id, cells: [mCell, iCell])

        def errors = new ImportSummary()

        def itemsByMeasure = [:]
        itemsByMeasure.put(experimentMeasure, [item])

        def results = service.createResults([row], [experimentMeasure], errors, itemsByMeasure)

        then:
        !errors.hasErrors()
        results.size() == 1
        Result rMeasure = results.first()
        rMeasure.replicateNumber == 1
        rMeasure.qualifier == "= "
        rMeasure.valueNum == 5.0
        rMeasure.resultType == resultType
        rMeasure.substanceId == substance.id
        rMeasure.resultContextItems.size() == 1
        ResultContextItem rci = rMeasure.resultContextItems.first()
        rci.qualifier == "< "
        rci.valueNum == 15.0
        rci.attributeElement == attribute
    }

    void 'test parse element with external URL #desc'() {
        given:
        Element attributeElement = Element.build(externalURL: "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", expectedValueType: EXTERNAL_ONTOLOGY)
        final AssayContextItem assayContextItem = AssayContextItem.build(attributeElement: attributeElement, attributeType: AttributeType.Fixed, valueDisplay: "ttt", extValueId: cellString)
        ItemService.Item item = new ItemService.Item(id: "I${assayContextItem.id}", type: assayContextItem.attributeType, contextItems: [assayContextItem], attributeElement: assayContextItem.attributeElement, assayContext: assayContextItem.assayContext)

        when:

        ImportSummary errors = new ImportSummary()

        ResultContextItem resultContextItem = service.createResultItem(cellString, item, errors)

        then:
        !errors.hasErrors()
        resultContextItem.valueNum == expectedValue
        resultContextItem.valueMax == maxVal
        resultContextItem.valueMin == minVal
        resultContextItem.qualifier == expectedQualifier
        resultContextItem.valueDisplay == valueDisplay
        resultContextItem.extValueId == cellString

        where:
        desc            | cellString | expectedValue | expectedQualifier | minVal | maxVal | valueDisplay
        "simple scalar" | "1"        | null          | null              | null   | null   | "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=1"
    }

    void 'test parsing context item containing #cellString'() {
        when:

        def item = this.service.itemService.getLogicalItems([AssayContextItem.build(attributeElement: Element.build(expectedValueType: NUMERIC), attributeType: AttributeType.Free, valueDisplay: null)])[0]
        ImportSummary errors = new ImportSummary()

        ResultContextItem resultContextItem = service.createResultItem(cellString, item, errors)

        then:
        !errors.hasErrors()
        resultContextItem.valueNum == expectedValue
        resultContextItem.valueMax == maxVal
        resultContextItem.valueMin == minVal
        resultContextItem.qualifier == expectedQualifier
        resultContextItem.valueDisplay == valueDisplay

        where:
        desc                  | cellString | expectedValue | expectedQualifier | minVal | maxVal | valueDisplay
        "simple scalar"       | "1"        | 1.0           | "= "              | null   | null   | "1.0"
        "scientific notation" | "1e4"      | 1e4           | "= "              | null   | null   | "10000.0"
        "including qualifier" | "<10"      | 10.0          | "< "              | null   | null   | "<10.0"
        "spaced qualifier"    | ">> 10"    | 10.0          | ">>"              | null   | null   | ">>10.0"
        "range"               | "2-3"      | null          | null              | 2.0    | 3.0    | "2.0-3.0"
        "free text"           | "free"     | null          | null              | null   | null   | "free"
    }

    void 'test parse context item from element list'() {
        setup:
        ImportSummary errors = new ImportSummary()

        when:
        def tubaElement = Element.build(label: "tuba")

        def attribute = Element.build(expectedValueType: ELEMENT)
        def context = AssayContext.build()
        def tubaItem = AssayContextItem.build(
                attributeElement: attribute,
                attributeType: AttributeType.List,
                valueElement: tubaElement,
                valueDisplay: tubaElement.label,
                assayContext: context
        )
        def trumpetElement = Element.build(label: "trumpet")
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: trumpetElement, valueDisplay: trumpetElement.label, assayContext: context)
        def item = this.service.itemService.getLogicalItems([tubaItem, trumpetItem])[0]

        ResultContextItem c0 = service.createResultItem("tuba", item, errors)

        then:
        !errors.hasErrors()
        c0.valueElement == tubaElement

        when:
        ResultContextItem c1 = service.createResultItem("trumpet", item, errors)

        then:
        !errors.hasErrors()
        c1.valueElement == trumpetElement

        when:
        service.createResultItem("pony", item, errors)

        then:
        errors.hasErrors()
    }

    void 'test parse context item from numeric value list'() {
        setup:
        ImportSummary errors = new ImportSummary()

        when:
        def attribute = Element.build(expectedValueType: NUMERIC)
        def context = AssayContext.build()
        def small = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 1e2, qualifier: '= ', valueDisplay: 'someValue')
        def large = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 2e2, qualifier: '= ', valueDisplay: 'someValue')
        def item = this.service.itemService.getLogicalItems([large, small])[0]

        ResultContextItem c0 = service.createResultItem("1e2", item, errors)

        then:
        !errors.hasErrors()
        c0.valueNum == 1e2

        when:
        ResultContextItem c1 = service.createResultItem("100", item, errors)

        then:
        !errors.hasErrors()
        c1.valueNum == 1e2

        when:
        ResultContextItem c2 = service.createResultItem("200", item, errors)

        then:
        !errors.hasErrors()
        c2.valueNum == 2e2

        when:
        ResultContextItem c3 = service.createResultItem("200.1", item, errors)

        then:
        !errors.hasErrors()
        c3.valueNum == 2e2

        when:
        service.createResultItem("pony", item, errors)

        then:
        errors.hasErrors()

    }

    void 'test parse context item from free text list'() {
        setup:
        ImportSummary errors = new ImportSummary()

        when:
        def attribute = Element.build(expectedValueType: ELEMENT)
        def context = AssayContext.build()
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueDisplay: "trumpet", valueElement: Element.build(label: "trumpet"))
        def tubaItem = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueDisplay: "tuba", valueElement: Element.build(label: "tuba"))
        def item = this.service.itemService.getLogicalItems([trumpetItem, tubaItem])[0]

        ResultContextItem c0 = service.createResultItem("trumpet", item, errors)

        then:
        !errors.hasErrors()
        c0.valueDisplay == "trumpet"

        when:
        ResultContextItem c1 = service.createResultItem("tuba", item, errors)

        then:
        !errors.hasErrors()
        c1.valueDisplay == "tuba"

        when:
        service.createResultItem("pony", item, errors)

        then:
        errors.hasErrors()
    }

    Result createResult() {
        return new Result(substanceId: 100, resultType: Element.build(), measure: ExperimentMeasure.build())
    }

    ResultContextItem createContextItem(params) {
        ResultContextItem item = new ResultContextItem(params)
        Result result = params.result;
        result.resultContextItems.add(item)
        return item
    }

    void 'test different parents in dup check'() {
        setup:
        ImportSummary errors = new ImportSummary()

        Element childElement = Element.build();

        Result parent1 = createResult()
        Result parent2 = createResult()
        ExperimentMeasure measure1 = ExperimentMeasure.build()
        ExperimentMeasure measure2 = ExperimentMeasure.build()
        Result child1 = new Result(substanceId: 100, resultType: childElement, measure: measure1)
        Result child2 = new Result(substanceId: 100, resultType: childElement, measure: measure2)

        ResultHierarchy link1 = new ResultHierarchy(result: child1, parentResult: parent1)
        ResultHierarchy link2 = new ResultHierarchy(result: child2, parentResult: parent2)
        child1.resultHierarchiesForResult.add(link1)
        parent1.resultHierarchiesForParentResult.add(link1)
        child2.resultHierarchiesForResult.add(link2)
        parent2.resultHierarchiesForParentResult.add(link2)

        assert child1.resultHierarchiesForResult.size() == 1
        assert child2.resultHierarchiesForResult.size() == 1

        when:
        service.checkForDuplicates(errors, [parent1, parent2, child1, child2])

        then:
        !errors.hasErrors()
    }

    void 'test duplicate check'() {
        setup:
        ImportSummary errors = new ImportSummary()

        Result result1 = createResult()
        ResultContextItem item1 = createContextItem(result: result1, valueNum: 2.0)

        Result result2 = createResult()
        ResultContextItem item2 = createContextItem(result: result2, valueNum: 3.0)

        Result result3 = new Result(substanceId: result1.substanceId, resultType: result1.resultType, measure: result1.measure)
        ResultContextItem item3 = createContextItem(result: result3, valueNum: 2.0)

        List<Result> results = [result1, result2]

        when:
        service.checkForDuplicates(errors, results)

        then:
        !errors.hasErrors()

        when:
        results.add(result3)
        service.checkForDuplicates(errors, results)

        then:
        errors.errors.size() == 1
    }
/*
    void 'test null values'() {
        setup:
        ResultsService service = new ResultsService();
        ImportSummary errors = new ImportSummary()

        // three nested measures
        Measure parentMeasure = Measure.build(resultType: Element.build(label: "parentCol"))
        Measure childMeasure = Measure.build(resultType: Element.build(label: "childCol"))
        Measure child2Measure = Measure.build(resultType: Element.build(label: "child2Col"))
        ExperimentMeasure parentExpMeasure = ExperimentMeasure.build(measure: parentMeasure)
        ExperimentMeasure childExpMeasure = ExperimentMeasure.build(measure: childMeasure, parent: parentExpMeasure)
        ExperimentMeasure child2ExpMeasure = ExperimentMeasure.build(measure: child2Measure, parent: childExpMeasure)

        Row childRow = new Row(cells: [ new RawCell(columnName: "childCol", value: "1") ])

        when:
        Collection<Result> results = service.extractResultFromEachRow(parentExpMeasure, [childRow], [:], new IdentityHashMap(), errors, [:])

        then:
        results.size() == 1
        Result parentResult = results.first()
        parentResult.valueDisplay == "NA"

        parentResult.resultHierarchiesForParentResult.size() == 1
        Result childResult = parentResult.resultHierarchiesForParentResult.first().result;
        childResult.valueDisplay == "1.0"

        // make sure that this child has no more children
        childResult.resultHierarchiesForParentResult.size() == 0
    }
*/

    Row makeRow(Map map) {
        List cells = map.collect { k, v -> new RawCell(columnName: k, value: v) }
        return new Row(cells: cells)
    }

    void 'test duplicate result type names'() {
        setup:
        ImportSummary errors = new ImportSummary()

        // a result type is used by two different measures
        // there are two parents, which each have a child.  Both children have the same result type
        ExperimentMeasure parent1Measure = ExperimentMeasure.build(resultType: Element.build(label: "parent1"))
        ExperimentMeasure parent2Measure = ExperimentMeasure.build(resultType: Element.build(label: "parent2"))

        Element duplicatedResultType = Element.build(label: "childCol")
        ExperimentMeasure child1Measure = ExperimentMeasure.build(resultType: duplicatedResultType, parent: parent1Measure)
        ExperimentMeasure child2Measure = ExperimentMeasure.build(resultType: duplicatedResultType, parent: parent2Measure)

        when:
        Collection<Result> results = service.extractResultFromEachRow(parent1Measure, [makeRow(["parent1": "1", "childCol": "2"])], [:], new IdentityHashMap(), errors, [:])

        then:
        !errors.hasErrors()
        results.size() == 1
        results.first().resultHierarchiesForParentResult.size() == 1
    }
}
