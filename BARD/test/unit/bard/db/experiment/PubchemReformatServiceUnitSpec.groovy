package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.enums.ValueType
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static bard.db.enums.ExpectedValueType.NUMERIC

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/14/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([Assay, AssayContext, AssayContextItem, AssayContextMeasure, Measure, Element, Experiment, ExperimentMeasure])
@Build([Assay, AssayContext, AssayContextItem, AssayContextMeasure, Measure, Element, Experiment, ExperimentMeasure])
class PubchemReformatServiceUnitSpec extends Specification {
    static TABLE_COLUMNS = [
            "TID",
            "TIDNAME",
            "PARENTTID",
            "RESULTTYPE",
            "STATS_MODIFIER",
            "CONTEXTTID",
            "CONTEXTITEM",
            "CONCENTRATION",
            "CONCENTRATIONUNIT",
            "PANELNO",
            "ATTRIBUTE1",
            "VALUE1",
            "ATTRIBUTE2",
            "VALUE2",
            "SERIESNO",
            "QUALIFIERTID",
            "EXCLUDED_POINTS_SERIES_NO","RELATIONSHIP"];

    def fillInRows(List<Map> rows) {
        for (row in rows) {
            for (column in row.keySet()) {
                assert TABLE_COLUMNS.contains(column)
            }

            for (column in TABLE_COLUMNS) {
                if (!row.containsKey(column)) {
                    row[column] = null;
                }
            }
        }

        return rows;
    }

    def 'test parsing context item in column'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        def rows = [
                [TID: 2, RESULTTYPE: "AC50"],
                [TID: 4, CONTEXTTID: 2, CONTEXTITEM: "comment", QUALIFIERTID: 5],
                [TID: 5]
        ]

        when:
        PubchemReformatService.ResultMap map = service.convertToResultMap("100", fillInRows(rows))

        then:
        map.records.size() == 1
        PubchemReformatService.ResultMapRecord record = map.records["AC50"].first()
        record.contextItemColumns.size() == 1
        record.staticContextItems.size() == 0
        PubchemReformatService.ResultMapContextColumn col = record.contextItemColumns.first()
        col.attribute == "comment"
        col.qualifierTid == "5"
        col.tid == "4"
    }

    def 'test basic result map entry'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        def rows = [
                [TID: 1, RESULTTYPE: "parent", STATS_MODIFIER: "stats", CONTEXTITEM: "attr1", CONCENTRATION: 1, ATTRIBUTE1: "attr2", VALUE1: "value2", ATTRIBUTE2: "attr3", VALUE2: "value3", QUALIFIERTID: 3, SERIESNO: 10],
                [TID: 3],
                [TID: 4, RESULTTYPE: "child", PARENTTID: 1]
        ]

        when:
        PubchemReformatService.ResultMap map = service.convertToResultMap("100", fillInRows(rows))

        then:
        map.records.size() == 2
        map.records["parent (stats)"].size() == 1
        map.records["child"].size() == 1

        PubchemReformatService.ResultMapRecord parent = map.records["parent (stats)"].first()
        PubchemReformatService.ResultMapRecord child = map.records["child"].first()

        parent.qualifierTid == "3"
        parent.tid == "1"
        parent.staticContextItems.size() == 0
        parent.contextItemColumns.size() == 0
        parent.resultType == "parent"
        parent.parentTid == null
        parent.series == 10

        child.series == null
        child.parentTid == "1"
    }

    def 'test converting row'() {
        when:
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100", [new PubchemReformatService.ResultMapRecord(series: 5, tid: "2", resultType: "AC50")])

        List rows = map.getValues([PUBCHEM_ACTIVITY_OUTCOME: "1", PUBCHEM_ACTIVITY_SCORE: "92.2", PUBCHEM_SID: "100", "2": "97.8"], "AC50", null, null)

        then:
        rows.size() == 1
        Map row = rows.first()
        row["Replicate #"] == "5"
        row["AC50"] == "97.8"
    }

    def 'test full handling of missing values'() {
        setup:
        // copy the pubchem input file to a known path
        InputStream inputStream = PubchemReformatServiceUnitSpec.getClassLoader().getResourceAsStream("bard/db/experiment/pubchem-input.txt")
        assert inputStream != null

        new File("out").mkdirs();
        new File("out/PubchemReformatServiceUnitSpec-in.txt").withOutputStream { fos ->
            IOUtils.copy(inputStream, fos)
        }

        // set up the service
        PubchemReformatService service = new PubchemReformatService()

        // and the result map we're using
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100", [
                new PubchemReformatService.ResultMapRecord(tid: "-1", resultType: "pubchem outcome"),
                new PubchemReformatService.ResultMapRecord(tid: "0", resultType: "pubchem score"),
                new PubchemReformatService.ResultMapRecord(tid: "1", resultType: "parent"),
                new PubchemReformatService.ResultMapRecord(tid: "2", resultType: "child", parentTid: "1", staticContextItems: [concentration: "100"]),
        ])

        // and finally the experiment with the two measures, and one context item
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay)
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context, valueType: ValueType.NONE, valueDisplay: null, attributeType: AttributeType.Free, attributeElement: Element.build(label: "concentration", expectedValueType: NUMERIC))
        Measure childMeasure = Measure.build(assay: assay, resultType: Element.build(label: "child"))
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: context, measure: childMeasure)

        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure parentExpMeasure = ExperimentMeasure.build(experiment: experiment, measure: Measure.build(resultType: Element.build(label: "parent")))
        ExperimentMeasure childExpMeasure = ExperimentMeasure.build(experiment: experiment, measure: childMeasure, parent: parentExpMeasure)

        when:
        service.convert(experiment, "out/PubchemReformatServiceUnitSpec-in.txt", "out/PubchemReformatServiceUnitSpec-out.txt", map);

        then:
        assertFilesMatch("bard/db/experiment/expected-conversion-output.txt", "out/PubchemReformatServiceUnitSpec-out.txt")
    }

    def 'test missing columns in result map'() {
        setup:
        // copy the pubchem input file to a known path
        InputStream inputStream = PubchemReformatServiceUnitSpec.getClassLoader().getResourceAsStream("bard/db/experiment/pubchem-input.txt")
        assert inputStream != null

        new File("out").mkdirs();
        new File("out/PubchemReformatServiceUnitSpec-in.txt").withOutputStream { fos ->
            IOUtils.copy(inputStream, fos)
        }

        // set up the service
        PubchemReformatService service = new PubchemReformatService()

        // and the result map we're using
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100", [
                new PubchemReformatService.ResultMapRecord(tid: "-1", resultType: "pubchem outcome"),
                new PubchemReformatService.ResultMapRecord(tid: "0", resultType: "pubchem score"),
        ])

        Experiment experiment = Experiment.build()

        when:
        service.convert(experiment, "out/PubchemReformatServiceUnitSpec-in.txt", "out/PubchemReformatServiceUnitSpec-out.txt", map);

        then:
        thrown(PubchemReformatService.MissingColumnsException)
    }

    boolean assertFilesMatch(String expected, String pathToVerify) {
        InputStream inputStream = PubchemReformatServiceUnitSpec.getClassLoader().getResourceAsStream(expected)
        assert inputStream != null
        Reader reader = new BufferedReader(new InputStreamReader(inputStream))
        def expectedLines = reader.readLines()
        reader.close()

        def actual = new File(pathToVerify).readLines()
        for (int i = 0; i < Math.max(expectedLines.size(), actual.size()); i++) {
            assert expectedLines[i] == actual[i]
        }

        return true;
    }

    def 'test adding NA for missing values'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        when:
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100", [
                new PubchemReformatService.ResultMapRecord(tid: "0", resultType: "x"),
                new PubchemReformatService.ResultMapRecord(tid: "1", resultType: "y", parentTid: "0"),
                new PubchemReformatService.ResultMapRecord(tid: "2", resultType: "z", parentTid: "1")
        ])

        Map pubchemRow = ["1": "100"]
        service.naMissingValues(pubchemRow, map)

        then:
        pubchemRow["1"] == "100"
        pubchemRow["0"] == "NA"
        pubchemRow["2"] == null
    }

    def 'test converting pubchem outcomes'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        when:
        Map map = service.convertPubchemRowToMap(
                ["85789806", "", "44483406", "1", "0", "", "", "", "-1.483", "5"],
                ["PUBCHEM_SID", "PUBCHEM_EXT_DATASOURCE_REGID", "PUBCHEM_CID", "PUBCHEM_ACTIVITY_OUTCOME", "PUBCHEM_ACTIVITY_SCORE", "PUBCHEM_ACTIVITY_URL", "PUBCHEM_ASSAYDATA_COMMENT", "PUBCHEM_ASSAYDATA_REVOKE", "1", "2"])

        then:
        map["-1"] == "Inactive"
        map["0"] == "0"
        map["1"] == "-1.483"
        map["2"] == "5"
    }

    def 'test fail if columns not used'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        def rows = [
                [TID: 1, RESULTTYPE: "parent"],
                [TID: 2]
        ]

        when:
        service.convertToResultMap("100", fillInRows(rows))

        then:
        thrown(PubchemReformatService.MissingColumnsException)
    }

    def 'test recreating measures from result map'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        Element cellCount = Element.build(label: "cell count", expectedValueType: NUMERIC)

        Collection<PubchemReformatService.MappedStub> newMeasures = [
                new PubchemReformatService.MappedStub(resultType: Element.build(), parentChildRelationship: HierarchyType.CALCULATED_FROM,
                        contextItems: [ (cellCount): ["100.0", "200.0"]])
        ]

        when:
        service.recreateMeasures(experiment, newMeasures)

        then:
        assay.assayContexts.size() == 1
        AssayContext context = assay.assayContexts.first()
        context.assayContextMeasures.size() == 1

        experiment.experimentMeasures.size() == 1
        assay.measures.size() == 1
    }

    def 'test recreating measures on an existing experiment'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()
        Assay assay = Assay.build()
        Measure oldMeasure = Measure.build(assay: assay)
        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure oldExperimentMeasure = ExperimentMeasure.build(experiment: experiment, measure: oldMeasure)
        Element cellCount = Element.build(label: "cell count", expectedValueType: NUMERIC)

        Collection<PubchemReformatService.MappedStub> newMeasures = [
                new PubchemReformatService.MappedStub(resultType: Element.build(), parentChildRelationship: HierarchyType.CALCULATED_FROM,
                        contextItems: [ (cellCount): ["100.0", "200.0"]])
        ]

        when:
        service.recreateMeasures(experiment, newMeasures)

        then:
        assay.assayContexts.size() == 1
        AssayContext context = assay.assayContexts.first()
        context.assayContextMeasures.size() == 1

        experiment.experimentMeasures.size() == 1
        !experiment.experimentMeasures.contains(oldExperimentMeasure)
        // there is the original measure, and a new one used by this experiment
        assay.measures.size() == 2
    }

    def 'test creating measure hierarchy from result map'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        when:
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100", [
                new PubchemReformatService.ResultMapRecord(tid: "0", resultType: "outcome"),
                new PubchemReformatService.ResultMapRecord(tid: "1", resultType: "activity", statsModifier: "mean", parentTid: "0"),
                new PubchemReformatService.ResultMapRecord(tid: "2", resultType: "activity", parentTid: "1", staticContextItems: ["cell count":"100"]),
                new PubchemReformatService.ResultMapRecord(tid: "3", resultType: "activity", parentTid: "1", staticContextItems: ["cell count":"100"])
        ])
        def measures = service.createMeasures(map)

        then:
        measures.size() == 1
        PubchemReformatService.MeasureStub grandparent = measures.first()
        grandparent.resultType == "outcome"
        grandparent.statsModifier == null
        grandparent.children.size() == 1
        grandparent.contextItems.size() == 0

        PubchemReformatService.MeasureStub parent = grandparent.children.first()
        parent.resultType == "activity"
        parent.statsModifier == "mean"
        parent.children.size() == 1
        parent.contextItems.size() == 0

        PubchemReformatService.MeasureStub child = parent.children.first()
        child.resultType == "activity"
        child.statsModifier == null
        child.children.size() == 0
        child.contextItems.size() == 1
        child.contextItems["cell count"].size() == 2
    }
}
