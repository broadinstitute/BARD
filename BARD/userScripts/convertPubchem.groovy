import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import bard.db.registration.AttributeType
import bard.db.registration.ExternalReference
import groovy.sql.Sql
import org.hibernate.classic.Session
import org.hibernate.jdbc.Work

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

class ResultMapRecord {
    Integer series
    String tid;
    String parentTid;

    // if value0Tid is null, then use value0
    String attribute0;
    Integer value0Tid;
    String value0

    String attribute1;
    String value1;

    String attribute2;
    String value2;

    String resultType
    String statsModifier

    String qualifier

}

String makeLabel(String resultType, String statsModifier) {
    if (statsModifier != null)
        return "${resultType} (${statsModifier})"
    return resultType
}

class ResultMap {
    Map<String, Collection<ResultMapRecord>> records = [:]

    String makeLabel(String resultType, String statsModifier) {
        if (statsModifier != null)
            return "${resultType} (${statsModifier})"
        return resultType
    }

    // TODO: Add support for qualifier
    List<Map<String,String>> getValues(Map<String,String> pubchemRow, String resultType, String statsModifier, String parentTid) {
        List rows = []
        String label = makeLabel(resultType, statsModifier)
        Collection<ResultMapRecord> records = records.get(label).findAll { it.parentTid == parentTid }
        for(record in records) {
            Map kvs = ["Replicate #": record.series, "TID": record.tid]
            kvs[label] = pubchemRow[record.tid]
            addContextValues(pubchemRow, record, kvs)
            rows.add(kvs)
        }
        return rows
    }

    void addContextValues(Map<String,String> pubchemRow,ResultMapRecord record, Map<String,String> kvs) {
        if (record.attribute0 != null) {
            if (record.value0Tid != null) {
                kvs[record.attribute0] = pubchemRow[record.value0Tid.toString()]
            } else {
                kvs[record.attribute0] = record.value0
            }
        }

        if (record.attribute1 != null) {
            kvs[record.attribute1] = record.value1
        }

        if (record.attribute2 != null) {
            kvs[record.attribute2] = record.value2
        }
    }
}

class CapCsvWriter {
    CSVWriter writer
    int rowCount = 0;
    int columnCount;
    Map<String, Integer> nameToIndex = [:];

    CapCsvWriter(Long experimentId, List<String> dynamicColumns, Writer writer) {
        this.writer = new CSVWriter(writer)
        columnCount = dynamicColumns.size()
        for(int i=0;i<columnCount;i++) {
            nameToIndex.put(dynamicColumns[i], i)
        }

        this.writer.writeNext(["","Experiment ID",experimentId.toString()].toArray(new String[0]))
        this.writer.writeNext([].toArray(new String[0]))
        List header = ["Row #","Substance","Replicate #","Parent Row #"]
        header.addAll(dynamicColumns)
        this.writer.writeNext(header.toArray(new String[0]))
    }

    int addRow(Long substanceId, Integer parentRow, Integer replicate, Map<String,String> row) {
        rowCount++;
        int rowNumber = rowCount;

        List<String> cols = new ArrayList()
        for(int i=0;i<columnCount;i++)
            cols.add("")

        row.each{ k, v -> if (nameToIndex.containsKey(k)) { cols[nameToIndex[k]] = v } }

        List fullRow = [rowNumber.toString(), substanceId.toString(), replicate?.toString(), parentRow?.toString()]
        fullRow.addAll(cols)
        writer.writeNext(fullRow.toArray(new String[0]))

        return rowNumber
    }

    void close() {
        writer.close()
    }
}

void convertRow(Collection<ExperimentMeasure> measures, Long substanceId, Map<String,String> pubchemRow, ResultMap map, CapCsvWriter writer, Integer parentRow, String parentTid) {
    for(expMeasure in measures) {
        String resultType = expMeasure.measure.resultType.label
        String statsModifier = expMeasure.measure.statsModifier?.label

        List rows = map.getValues(pubchemRow, resultType, statsModifier, parentTid)
        for(row in rows) {
            int rowNumber = writer.addRow(substanceId, parentRow, row["Replicate #"], row)

            // write out the children
            println("${rowNumber}: ${row}")
            convertRow(expMeasure.childMeasures, substanceId, pubchemRow, map, writer, rowNumber, row["TID"])
        }
    }
}

List constructCapColumns(Experiment experiment) {
    Set colNames = [] as Set

    experiment.experimentMeasures.each {
        // add measure names
        colNames.add(makeLabel(it.measure.resultType.label, it.measure.statsModifier?.label))
        // add context items
        it.measure.assayContextMeasures.each {
            it.assayContext.assayContextItems.findAll { it.attributeType != AttributeType.Fixed } .each {
                colNames.add(it.attributeElement.label)
            }
        }
    }

    return new ArrayList(colNames)
}

// TODO check units
public ResultMap loadMap(Connection connection, Long aid) {
    ResultMap map = new ResultMap()
    Sql sql = new Sql(connection)
    List records = []
    sql.eachRow("SELECT TID, PARENTTID, RESULTTYPE, STATS_MODIFIER, CONTEXTTID, CONTEXTITEM, CONCENTRATION, PANELNO, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2, SERIESNO FROM southern.result_map WHERE AID = ?", [aid]) {
        assert it.PANELNO == null
        def rec = new ResultMapRecord(
                tid: it.TID,
                series : it.SERIESNO,
                attribute0 : it.CONTEXTITEM,
                value0Tid : it.TID == it.CONTEXTTID ? null : it.CONTEXTTID,
                value0 : it.CONCENTRATION,
                attribute1: it.ATTRIBUTE1,
                value1 : it.VALUE1,
                attribute2 : it.ATTRIBUTE2,
                value2 : it.VALUE2,
                resultType: it.RESULTTYPE,
                statsModifier: it.STATS_MODIFIER,
                parentTid: it.PARENTTID?.toString())
        records.add(rec)
    }

    map.records = records.groupBy { makeLabel(it.resultType, it.statsModifier) }
    return map
}

public void convert(Experiment experiment, String pubchemFilename, ResultMap map) {
    List dynamicColumns = constructCapColumns(experiment)

    CapCsvWriter writer = new CapCsvWriter(experiment.id, dynamicColumns, new FileWriter("exp-${experiment.id}.csv"))
    CSVReader reader = new CSVReader(new FileReader(pubchemFilename))
    List<String> header = reader.readNext()
    Collection<ExperimentMeasure> rootMeasures = experiment.experimentMeasures.findAll { it.parent == null }
    while(true) {
        List<String> row = reader.readNext()
        if (row == null)
            break

        Long substanceId = Long.parseLong(row[0])
        outcome = row[3]
        activity = row[4]

        Map pubchemRow = [:]
        pubchemRow["-1"] = outcome
        pubchemRow["0"] = activity
        for(int i=0;i<header.size();i++) {
            pubchemRow[header[i]] = row[i]
        }

        convertRow(rootMeasures, substanceId, pubchemRow, map, writer, null, null)
    }

    writer.close()
}

Long expId = 2949
String pubchemFilename = "/Users/pmontgom/data/pubchem-conversion/0000001_0001000/721.csv"
ResultMap map;
Experiment experiment = Experiment.get(expId)
ExternalReference ref = experiment.getExternalReferences().find {it.externalSystem.systemName == "PubChem"}
Long aid = Long.parseLong(ref.extAssayRef.replace("aid=", ""));
Experiment.withSession { Session session ->
    session.doWork(new Work() {
        void execute(Connection connection) throws SQLException {
             map = loadMap(connection, aid);
        }
    })
}
convert(experiment, pubchemFilename, map)
