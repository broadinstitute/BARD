package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Unroll

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(ServiceUnitTestMixin)
@Build([Assay, Measure, AssayContext, AssayContextItem, AssayContextMeasure, Element])
@Unroll
class ResultsServiceSpec extends spock.lang.Specification {

    void setup() {
        // Setup logic here
    }

    void 'test create template from assay'() {
        when:
        // an assay with 1 measure, and two contexts, one of which is associated with the measure
        Assay assay = Assay.build()
        AssayContext assayContext = AssayContext.build(assay: assay)
        AssayContext measureContext = AssayContext.build(assay: assay)
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: assayContext, attributeType: AttributeType.Free, attributeElement: Element.build(label:"cell line"))
        Measure measure = Measure.build(resultType: Element.build(label:"ec50"))
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: measureContext, measure: measure)
        AssayContextItem measureContextItem = AssayContextItem.build(assayContext: measureContext, attributeType: AttributeType.Free, attributeElement: Element.build(label:"hill slope"))
        measureContext.assayContextMeasures = [assayContextMeasure] as Set
        assay.assayContexts = [assayContext, measureContext]
        assay.measures = [measure] as Set

        ResultsService service = new ResultsService();

        ResultsService.Template template = service.generateMaxSchema(assay)

        then:
        template.constantItems.size() == 1
        template.constantItems.get(0).name == "cell line"
        template.assay == assay
        template.columns.size() == 2
        template.columns.get(0).name == "ec50"
        template.columns.get(1).name == "hill slope"
    }

    void 'test initial parse'() {
        when:
        Assay assay = Assay.build()
        Measure inhibitionMeasure = Measure.build()
        Measure ec50Measure = Measure.build()

        def columns = [new ResultsService.Column(name: "Inhibition", measure: inhibitionMeasure), new ResultsService.Column(name: "EC50", measure: ec50Measure)]
        def constantItems = []
        ResultsService.Template template = new ResultsService.Template(assay: assay, columns: columns, constantItems: constantItems)

        String sample = "\tAssay ID\t123\n"+
                "\n" +
                "Row #\tSubstance\tReplicate #\tParent Row #\tInhibition\tEC50\n" +
                "1\t100\t\t\t10\t\n" +
                "2\t100\t\t1\t\t5\n"

        ResultsService service = new ResultsService();

        ResultsService.InitialParse result = service.initialParse(new StringReader(sample), template)

        then:
        result.constants.size() == 1
        result.constants.get("Assay ID") == "123"
        result.rows.size() == 2
        !result.errors.hasErrors()

        when:
        ResultsService.Row row0 = result.rows.get(0)
        ResultsService.Row row1 = result.rows.get(1)

        then:
        row0.parentRowNumber == null
        row0.cells.size() == 1
        row0.cells.get(0).value == 10
        row0.sid == 100
        row0.rowNumber == 1
        row0.replicate == null

        row1.parentRowNumber == 1
        row1.cells.size() == 1
        row1.cells.get(0).value == 5
    }

    void 'test parse measure cell'() {
        when:
        Measure measure = Measure.build()
        ResultsService.Column column = new ResultsService.Column(measure: measure)

        ResultsService.Cell cell = column.parseValue(cellString)

        then:
        cell.value == expectedValue
        cell.maxValue == maxVal
        cell.minValue == minVal
        cell.qualifier == expectedQualifier

        where:
        desc                     | cellString | expectedValue | expectedQualifier | minVal | maxVal
        "simple scalar"          | "1"        | 1.0           | "="               | null   | null
        "scientific notation"    | "1e4"      | 1e4           | "="               | null   | null
        "including qualifier"    | "<10"      | 10.0          | "<"               | null   | null
        "spaced qualifier"       | ">> 10"    | 10.0          | ">>"              | null   | null
        "range"                  | "2-3"      | null          | null              | 2.0    | 3.0
    }

/*
    void 'test parse context item cell'() {
        when:
        Measure measure = Measure.build()
        ResultsService.Column column = new ResultsService.Column(measure: measure)

        then:
        column.getError(cellString) == null

        when:
        ResultsService.Cell cell = column.parse(cellString)

        then:
        cell.value == expectedValue
        cell.maxValue == maxVal
        cell.minValue == minVal
        cell.qualifier == expectedQualifier

        where:
        desc                     | cellString | expectedValue | expectedQualifier | minVal | maxVal | element
        "simple scalar"          | "1"        | 1.0           | null              | null   | null   | null
        "scientific notation"    | "1e4"      | 1e4           | null              | null   | null   | null
        "including qualifier"    | "<10"      | 10.0          | "<"               | null   | null   | null
        "spaced qualifier"       | ">> 10"    | 10.0          | ">>"              | null   | null   | null
        "range"                  | "2-3"      | null          | null              | 2.0    | 3.0    | null
    }
*/

/*
    void 'test parse failures'() {
        where:
        desc                     | row | column | newValue | expectedErrorCount
        "bad constant section"   | 0   | 0      | "x"      | 1
        "bad constant name"      | 0   | 1      | "x"      | 1
        "bad fixed column"       | 2   | 0      | "x"      | 1
        "bad dynamic column"     | 2   | 4      | "x"      | 1
        "duplicate column"       | 2   | 4      | "EC50"   | 1
        "duplicate row number"   | 4   | 0      | "1"      | 1
        "extra column"           | 3   | 6      | "1"      | 1
        "missing row number"     | 3   | 0      | ""       | 1
    }
    */
}
