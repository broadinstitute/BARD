package bard.db.experiment

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/14/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
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
            "QUALIFIERTID"] ;

    def fillInRows(List<Map> rows) {
        for(row in rows) {
            for(column in row.keySet()) {
                assert TABLE_COLUMNS.contains(column)
            }

            for(column in TABLE_COLUMNS) {
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
        PubchemReformatService.ResultMap map = service.convertToResultMap("100",fillInRows(rows))

        then:
        map.records.size() == 2
        map.records["parent"].size() == 1
        map.records["child"].size() == 1

        PubchemReformatService.ResultMapRecord parent = map.records["parent"].first()
        PubchemReformatService.ResultMapRecord child = map.records["child"].first()

        parent.qualifierTid == "3"
        parent.tid == "1"
        parent.staticContextItems.size() == 3
        parent.contextItemColumns.size() == 0
        parent.resultType == "parent"
        parent.parentTid == null
        parent.series == 10

        child.series == null
        child.parentTid == "1"
    }

    def 'test converting row'() {
        when:
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100",[new PubchemReformatService.ResultMapRecord(series:  5, tid: "2", resultType: "AC50" )])

        List rows = map.getValues([PUBCHEM_ACTIVITY_OUTCOME: "1", PUBCHEM_ACTIVITY_SCORE: "92.2",PUBCHEM_SID: "100", "2": "97.8"], "AC50", null, null)

        then:
        rows.size() == 1
        Map row = rows.first()
        row["Replicate #"] == "5"
        row["AC50"] == "97.8"
    }

    def 'test adding NA for missing values'() {
        setup:
        PubchemReformatService service = new PubchemReformatService()

        when:
        PubchemReformatService.ResultMap map = new PubchemReformatService.ResultMap("100",[
                new PubchemReformatService.ResultMapRecord(tid: "0", resultType: "x" ),
                new PubchemReformatService.ResultMapRecord(tid: "1", resultType: "y", parentTid: "0"),
                new PubchemReformatService.ResultMapRecord(tid: "2", resultType: "z", parentTid: "1")
        ])

        Map pubchemRow = ["1":"100"]
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
            ["85789806","","44483406","1","0","","","","-1.483","5"],
            ["PUBCHEM_SID","PUBCHEM_EXT_DATASOURCE_REGID","PUBCHEM_CID","PUBCHEM_ACTIVITY_OUTCOME","PUBCHEM_ACTIVITY_SCORE","PUBCHEM_ACTIVITY_URL","PUBCHEM_ASSAYDATA_COMMENT","PUBCHEM_ASSAYDATA_REVOKE","1","2"])

        then:
        map["-1"] == "Inactive"
        map["0"] == "0"
        map["1"] == "-1.483"
        map["2"] == "5"
    }
}
