package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.*
import bard.db.experiment.results.RawCell
import bard.db.experiment.results.Row
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(ServiceUnitTestMixin)
@Build([Assay, Measure, AssayContext, AssayContextItem, AssayContextMeasure, Element, Substance, Experiment, ExperimentMeasure])
@Mock([Assay, Measure, AssayContext, AssayContextItem, AssayContextMeasure, Element, Substance, Experiment, ExperimentMeasure])
@Unroll
@TestFor(ResultsService)
class ResultsServiceSpec extends Specification {

    void setup() {
        ItemService itemService = new ItemService()  // Setup logic here
        service.itemService = itemService
    }

    @Unroll
    void 'test create parent child measure with relationship expressed by #desc'() {
        when:
        def substance = Substance.build()
        substance.save()

        then:
        substance.id != null

        when:

        def parentResultType = Element.build(label: "parent")
        def parentMeasure = Measure.build(resultType: parentResultType)
        def parentCell = new RawCell(columnName: "parent", value: "1")

        def childResultType = Element.build(label: "child")
        def childMeasure = Measure.build(resultType: childResultType)
        def childCell = new RawCell(columnName: "child", value: "1")

        ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build(measure: parentMeasure)
        ExperimentMeasure childExperimentMeasure = ExperimentMeasure.build(parent: parentExperimentMeasure, measure: childMeasure, parentChildRelationship: HierarchyType.CALCULATED_FROM)
        parentExperimentMeasure.childMeasures.add(childExperimentMeasure)

        List<Row> rows;
        if (onSameLine) {
            def row = new Row(rowNumber: 1, sid: substance.id, cells: [parentCell, childCell], replicate: 1)
            rows = [row]
        } else {
            def row0 = new Row(rowNumber: 1, sid: substance.id, cells: [parentCell], replicate: 1)
            def row1 = new Row(rowNumber: 2, parentRowNumber: 1, sid: substance.id, cells: [childCell], replicate: 1)
            rows = [row0, row1]
        }

        def errors = new ResultsService.ImportSummary()

        def results = service.createResults(rows, [parentExperimentMeasure, childExperimentMeasure], errors, [:])

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
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: assayContext, attributeType: AttributeType.Free, attributeElement: Element.build(label: "cell line"), valueDisplay: null)
        Measure measure = Measure.build(resultType: Element.build(label: "ec50"))
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: measureContext, measure: measure)
        AssayContextItem measureContextItem = AssayContextItem.build(assayContext: measureContext, attributeType: AttributeType.Free, attributeElement: Element.build(label: "hill slope"), valueDisplay: null)
        measureContext.assayContextMeasures = [assayContextMeasure] as Set
        assay.assayContexts = [assayContext, measureContext]
        assay.measures = [measure] as Set
        experiment.experimentMeasures = [ExperimentMeasure.build(measure: measure)] as Set


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
        Measure inhibitionMeasure = Measure.build()
        Measure ec50Measure = Measure.build()

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

    @Unroll
    void 'test parsing experiment level #desc'() {
        setup:
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()
        Element attribute = Element.build(label: "column")
        def item = service.itemService.getLogicalItems([AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.Free, valueDisplay: null)])[0]

        when:
        String sample = ",Experiment ID,123\n,column," + cellString + "\n"
        BufferedReader reader = new BufferedReader(new StringReader(sample))
        ResultsService.InitialParse initialParse = service.parseConstantRegion(new ResultsService.LineReader(reader), errors, [item], false)

        then:
        initialParse.contexts.size() == 1
        ExperimentContext context = initialParse.contexts.first()
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
        def attribute = Element.build(label: "column")
        def context = AssayContext.build(contextName: "instrument")
        def tubaItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: tubaElement, valueDisplay: tubaElement.label, assayContext: context)
        def trumpetElement = Element.build(label: "trumpet")
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: trumpetElement, valueDisplay: trumpetElement.label, assayContext: context)
        def item = this.service.itemService.getLogicalItems([tubaItem, trumpetItem])[0]

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        when:
        String sample = ",Experiment ID,123\n,column,trumpet\n"
        BufferedReader reader = new BufferedReader(new StringReader(sample))

        ResultsService.InitialParse initialParse = service.parseConstantRegion(new ResultsService.LineReader(reader), errors, [item], false)

        then:
        !errors.hasErrors()

        initialParse.contexts.size() == 1
        ExperimentContext expContext = initialParse.contexts.first()
        expContext.contextName == "instrument"
        expContext.contextItems.size() == 1
        ExperimentContextItem expItem = expContext.contextItems.first()
        expItem.attributeElement == attribute
        expItem.valueElement == trumpetElement
    }

    void 'test initial parse'() {
        when:
        def fixture = createSampleFile()

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()
        ResultsService.InitialParse result = service.initialParse(new StringReader(fixture.sample), errors, fixture.template, false)

        then:
        result.experimentId == 123
        result.rows.size() == 2
        !errors.hasErrors()

        when:
        Row row0 = result.rows.get(0)
        Row row1 = result.rows.get(1)

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
    }

    @Unroll("test parsing problem: #desc")
    void 'test parse failures'() {
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

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()
        ResultsService.InitialParse result = service.initialParse(new StringReader(sample), errors, fixture.template, false)

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

    @Unroll("test parsing cell containing #cellString")
    void 'test parse measure cell'() {
        when:
        Measure measure = Measure.build()
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
        def measure = Measure.build(resultType: resultType)
        def experimentMeasure = ExperimentMeasure.build(measure: measure)
        def row = new Row(rowNumber: 1, sid: substance.id, cells: [new RawCell(columnName: "x", value: "5")], replicate: 1)

        def errors = new ResultsService.ImportSummary()

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

        def attribute = Element.build(label: "item")
        def item = this.service.itemService.getLogicalItems([AssayContextItem.build(attributeType: AttributeType.Free, attributeElement: attribute, valueDisplay: null)])[0]
        def resultType = Element.build(label: "measure")
        def measure = Measure.build(resultType: resultType)
        def experimentMeasure = ExperimentMeasure.build(measure: measure)

        // construct a row of two cells: a measurement and an associated context
        def mCell = new RawCell(columnName: "measure", value: "5")
        def iCell = new RawCell(columnName: "item", value: "<15")
        def row = new Row(rowNumber: 1, replicate: 1, sid: substance.id, cells: [mCell, iCell])

        def errors = new ResultsService.ImportSummary()

        def itemsByMeasure = [:]
        itemsByMeasure.put(measure, [item])

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
        Element attributeElement = Element.build(externalURL: "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=")
        final AssayContextItem assayContextItem = AssayContextItem.build(attributeElement: attributeElement, attributeType: AttributeType.Fixed, valueDisplay: "ttt", extValueId: cellString)
        ItemService.Item item = new ItemService.Item(id: "I${assayContextItem.id}", type: assayContextItem.attributeType, contextItems: [assayContextItem], attributeElement: assayContextItem.attributeElement, assayContext: assayContextItem.assayContext)

        when:

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        ResultContextItem resultContextItem = service.createResultItem(cellString, item, errors)

        then:
        !errors.hasErrors()
        resultContextItem.valueNum == expectedValue
        resultContextItem.valueMax == maxVal
        resultContextItem.valueMin == minVal
        resultContextItem.qualifier == expectedQualifier
        resultContextItem.valueDisplay == valueDisplay
        resultContextItem.extValueId==cellString

        where:
        desc            | cellString | expectedValue | expectedQualifier | minVal | maxVal | valueDisplay
        "simple scalar" | "1"        | null          | null              | null   | null   | "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=1"
    }

    @Unroll("test parsing context item containing #cellString")
    void 'test parse non-element context item cell'() {
        when:

        def item = this.service.itemService.getLogicalItems([AssayContextItem.build(attributeElement: Element.build(), attributeType: AttributeType.Free, valueDisplay: null)])[0]
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

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
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        when:
        def tubaElement = Element.build(label: "tuba")

        def attribute = Element.build()
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
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        when:
        def attribute = Element.build()
        def context = AssayContext.build()
        def small = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 1e2, qualifier: '= ')
        def large = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 2e2, qualifier: '= ')
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
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        when:
        def attribute = Element.build()
        def context = AssayContext.build()
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueDisplay: "trumpet")
        def tubaItem = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueDisplay: "tuba")
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
        return new Result(substanceId: 100, resultType: Element.build(), measure: Measure.build())
    }

    ResultContextItem createContextItem(params) {
        ResultContextItem item = new ResultContextItem(params)
        Result result = params.result;
        result.resultContextItems.add(item)
        return item
    }

    void 'test different parents in dup check'() {
        setup:
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        Element childElement = Element.build();

        Result parent1 = createResult()
        Result parent2 = createResult()
        Measure measure1 = Measure.build()
        Measure measure2 = Measure.build()
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
        service.checkForDuplicates(errors, [child1, child2])

        then:
        !errors.hasErrors()
    }

    void 'test duplicate check'() {
        setup:
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        Result result1 = createResult()
        ResultContextItem item1 = createContextItem(result: result1, valueNum: 2.0)

        Result result2 = createResult()
        ResultContextItem item2 = createContextItem(result: result2, valueNum: 3.0)

        List<Result> results = [result1, result2]

        when:
        service.checkForDuplicates(errors, results)

        then:
        !errors.hasErrors()

        when:
        results.add(result1)
        service.checkForDuplicates(errors, results)

        then:
        errors.errors.size() == 1
    }
/*
    void 'test null values'() {
        setup:
        ResultsService service = new ResultsService();
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

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
        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()

        // a result type is used by two different measures
        // there are two parents, which each have a child.  Both children have the same result type
        Measure parent1Measure = Measure.build(resultType: Element.build(label: "parent1"))
        Measure parent2Measure = Measure.build(resultType: Element.build(label: "parent2"))
        Element duplicatedResultType = Element.build(label: "childCol")
        Measure child1Measure = Measure.build(resultType: duplicatedResultType)
        Measure child2Measure = Measure.build(resultType: duplicatedResultType)
        ExperimentMeasure parent1ExpMeasure = ExperimentMeasure.build(measure: parent1Measure)
        ExperimentMeasure parent2ExpMeasure = ExperimentMeasure.build(measure: parent2Measure)
        ExperimentMeasure.build(measure: child1Measure, parent: parent1ExpMeasure)
        ExperimentMeasure.build(measure: child2Measure, parent: parent2ExpMeasure)

        when:
        Collection<Result> results = service.extractResultFromEachRow(parent1ExpMeasure, [makeRow(["parent1": "1", "childCol": "2"])], [:], new IdentityHashMap(), errors, [:])

        then:
        !errors.hasErrors()
        results.size() == 1
        results.first().resultHierarchiesForParentResult.size() == 1
    }
}
