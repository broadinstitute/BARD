package bard.db.experiment

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import bard.db.registration.AttributeType
import bard.db.registration.ExternalReference
import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils
import org.hibernate.classic.Session
import org.hibernate.jdbc.Work

import java.sql.Connection
import java.sql.SQLException
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/14/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
class PubchemReformatService {

    static class MissingColumnsException extends RuntimeException {
        MissingColumnsException(String s) {
            super(s)
        }
    }

    final static Map<String,String> pubchemOutcomeTranslation = new HashMap();
    static {
        pubchemOutcomeTranslation.put("1", "Inactive");
        pubchemOutcomeTranslation.put("2", "Active");
        pubchemOutcomeTranslation.put("3", "Inconclusive");
        pubchemOutcomeTranslation.put("4", "Unspecified");
        pubchemOutcomeTranslation.put("5", "Probe");
    }

    static class ResultMapContextColumn {
        String attribute;
        String tid;
        String qualifierTid
    }

    static class ResultMapRecord {
        Integer series
        String tid;
        String parentTid;

        String resultType
        String statsModifier

        String qualifierTid

        Set<ResultMapContextColumn> contextItemColumns = [] as Set;
        Map<String,String> staticContextItems = [:];

        @Override
        public String toString() {
            return "ResultMapRecord{" +
                    "series=" + series +
                    ", tid='" + tid + '\'' +
                    ", parentTid='" + parentTid + '\'' +
                    ", resultType='" + resultType + '\'' +
                    ", statsModifier='" + statsModifier + '\'' +
                    ", qualifierTid='" + qualifierTid + '\'' +
                    ", contextItemColumns=" + contextItemColumns +
                    ", staticContextItems=" + staticContextItems +
                    '}';
        }
    }

    /**
     * Handles the writing of the CAP formatted results file.  Construct an instance and call addRow per row in the pubchem file.
     */
    static class CapCsvWriter {
        CSVWriter writer
        int rowCount = 0;
        int columnCount;
        Map<String, Integer> nameToIndex = [:];

        CapCsvWriter(Long experimentId, Map<String,String> experimentContextItems, List<String> dynamicColumns, Writer rawWriter) {
            writer = new CSVWriter(rawWriter)
            columnCount = dynamicColumns.size()
            for(int i=0;i<columnCount;i++) {
                nameToIndex.put(dynamicColumns[i], i)
            }

            writer.writeNext(["","Experiment ID",experimentId.toString()].toArray(new String[0]))
            experimentContextItems.each { k, v ->
                writer.writeNext(["",k,v].toArray(new String[0]))
            }
            writer.writeNext([].toArray(new String[0]))
            List header = ["Row #","Substance","Replicate #","Parent Row #"]
            header.addAll(dynamicColumns)
            writer.writeNext(header.toArray(new String[0]))
        }

        int addRow(Long substanceId, Integer parentRow, String replicate, Map<String,String> row) {
            rowCount++;
            int rowNumber = rowCount;

            List<String> cols = new ArrayList()
            for(int i=0;i<columnCount;i++)
                cols.add("")

            row.each{ k, v ->
                if (v != null && !(v instanceof String)) {
                    throw new RuntimeException("${k} -> ${v} but has class ${v.getClass()}")
                }
                if (nameToIndex.containsKey(k)) {
                    cols[nameToIndex[k]] = v
                }
            }

            List fullRow = [rowNumber.toString(), substanceId.toString(), replicate?.toString(), parentRow?.toString()]
            fullRow.addAll(cols)
            writer.writeNext(fullRow.toArray(new String[0]))

            return rowNumber
        }

        void close() {
            writer.close()
        }
    }

    static class ResultMap {
        String aid;
        Map<String, Collection<ResultMapRecord>> records;
        Map<String, Collection<ResultMap>> recordsByParentTid;
        Collection<String> tids;
        Collection<ResultMapRecord> allRecords;

        public ResultMap(String aid, Collection<ResultMapRecord> rs, Set tids=null) {
            this.aid = aid;
            records = rs.groupBy { makeLabel(it.resultType, it.statsModifier) }
            recordsByParentTid = rs.groupBy {it.parentTid}
            
            if(tids == null) {
            tids = [] as Set
            rs.each {
                tids.add(it.tid)
                tids.add(it.qualifierTid)
                it.contextItemColumns.each {
                    tids.add(it.tid)
                    tids.add(it.qualifierTid)
                }
            }
            }
	    this.tids = tids

            allRecords = new ArrayList(rs);
            allRecords.sort { Integer.parseInt(it.tid) }
        }

        Collection<ResultMapRecord> getChildRecords(String tid) {
            return recordsByParentTid.get(tid);
        }

        public int getNumberOfCustomColumns() {
            return (allRecords.findAll {it.tid > 0}).size()
        }

        public boolean hasTid(String tid) {
            return tids.contains(tid);
        }

        /**
         * construct a list of CAP result rows for the given resultType, statsModifier and the parentTid
         */
        List<Map<String,String>> getValues(Map<String,String> pubchemRow, String resultType, String statsModifier, String parentTid) {
            List<Map<String,String>> rows = []
            String label = makeLabel(resultType, statsModifier)
            Collection<ResultMapRecord> records = records.get(label).findAll { it.parentTid == parentTid }

            // each record represents a column in the pubchem file
            // loop through all the child columns of the given parent (parentTid)
            for(record in records) {
                Map<String,String> kvs = ["Replicate #": record.series?.toString(), "TID": record.tid]

                // get the value for the current column (record) in the current row (pubchemRow)
                String measureValue = pubchemRow[record.tid]

                if (measureValue != null && measureValue.length() > 0)
                {
                    if (record.qualifierTid != null) {
                        measureValue = pubchemRow[record.qualifierTid]+measureValue
                    }
                    kvs[label] = measureValue
                    addContextValues(pubchemRow, record, kvs)
                }

                rows.add(kvs)
            }

            return rows
        }

        void addContextValues(Map<String,String> pubchemRow,ResultMapRecord record, Map<String,String> kvs) {
            for(staticItem in record.staticContextItems.entrySet()) {
                kvs[staticItem.key] = staticItem.value
            }

            for(columnItem in record.contextItemColumns) {
                String value = pubchemRow[columnItem.tid]
                if (columnItem.qualifierTid != null) {
                    value = pubchemRow[columnItem.qualifierTid] + value;
                }
                kvs[columnItem.attribute] = value
            }
        }

        @Override
        public String toString() {
            return "ResultMap{" +
                    "records=" + records +
                    '}';
        }
    }

    static String makeLabel(String resultType, String statsModifier) {
        if (statsModifier != null)
            return "${resultType} (${statsModifier})"
        return resultType
    }

    boolean memoizedHasNonBlankChild(Map<String,String> pubchemRow, ResultMap map, String tid, Map cache, Set seen) {
        if (cache.containsKey(tid)) {
            return cache[tid];
        }

        if (seen.contains(tid)) {
            throw new RuntimeException("Found cycle in ${map.aid}")
        }
        seen.add(tid);

        boolean found = false;
        if (!StringUtils.isBlank(pubchemRow[tid])) {
            found = true;
        } else {
            for(record in map.getChildRecords(tid)) {
                if (memoizedHasNonBlankChild(pubchemRow, map, record.tid, cache, seen)) {
                    found = true;
                    break;
                }
            }
        }

        cache[tid] = found
        return found;
    }

    /**
     * modify the pubchemRow to include a NA for all elements that are blank _BUT_ there is a non-blank child
     */
    void naMissingValues(Map<String,String> pubchemRow, ResultMap map) {
        List needsNa = []
        Map cache = [:]
        map.getTids().each {tid ->
            def v = pubchemRow[tid]
            if (StringUtils.isBlank(v) && memoizedHasNonBlankChild(pubchemRow, map, tid, cache, new HashSet())) {
                needsNa.add(tid)
            }
        }

        for(key in needsNa) {
            pubchemRow[key] = "NA"
        }
    }

    void convertRow(Collection<ExperimentMeasure> measures, Long substanceId, Map<String,String> pubchemRow, ResultMap map, CapCsvWriter writer, Integer parentRow, String parentTid) {
        for(expMeasure in measures) {
            String resultType = expMeasure.measure.resultType.label
            String statsModifier = expMeasure.measure.statsModifier?.label

            List rows = map.getValues(pubchemRow, resultType, statsModifier, parentTid)
            for(row in rows) {
                int rowNumber = writer.addRow(substanceId, parentRow, row["Replicate #"], row)

                convertRow(expMeasure.childMeasures, substanceId, pubchemRow, map, writer, rowNumber, row["TID"])
            }
        }
    }

    List constructCapColumns(Experiment experiment) {
        Set colNames = [] as Set

        experiment.experimentMeasures.each {
            // add measure name
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

    /**
     * Construct a ResultMap from a list of selected rows from the RESULT_MAP table
     */
    public ResultMap convertToResultMap(String aid, List rows) {
        Map byTid = [:]
        List<ResultMapRecord>  records = [];
        Set<String> unusedTids = new HashSet();
        Set<String> tids = new HashSet()

        // Keep track which TIDs we've used for something
        for(row in rows) {
	    tids.add(row.TID.toString())
            unusedTids.add(row.TID.toString())

            if (row.EXCLUDED_POINTS_SERIES_NO != null || row.CONTEXTITEM == "do not import") {
                unusedTids.remove(row.TID.toString())
            }

        }

        // first pass: create all the items for result types
        for(row in rows) {
            if (row.RESULTTYPE != null) {
                if( !( row.CONTEXTTID == null || row.CONTEXTTID == row.TID) ) { 
			throw new RuntimeException("aid=${aid} tid=${row.TID} appears to be both a result type and a context item")
		}
                ResultMapRecord record = new ResultMapRecord(series: row.SERIESNO,
                        tid: row.TID,
                        parentTid: row.PARENTTID?.toString(),
                        resultType: row.RESULTTYPE,
                        statsModifier: row.STATS_MODIFIER,
                        qualifierTid: row.QUALIFIERTID)
                records.add(record)

                if (record.qualifierTid != null)
                    unusedTids.remove(record.qualifierTid)
                unusedTids.remove(record.tid)

                if (row.CONTEXTITEM != null) {
                    record.staticContextItems[row.CONTEXTITEM] = row.CONCENTRATION?.toString()
                }
                if (row.ATTRIBUTE1 != null) {
                    record.staticContextItems[row.ATTRIBUTE1] = row.VALUE1
                }
                if (row.ATTRIBUTE2 != null) {
                    record.staticContextItems[row.ATTRIBUTE2] = row.VALUE2
                }

                byTid[record.tid.toString()] = record
            }
        }

        //second pass: associate context columns to rows
        for(row in rows) {
            if (row.CONTEXTTID != null && row.TID != row.CONTEXTTID) {
                assert row.RESULTTYPE == null
                ResultMapContextColumn col = new ResultMapContextColumn( attribute: row.CONTEXTITEM, tid: row.TID, qualifierTid: row.QUALIFIERTID)
                ResultMapRecord record = byTid[row.CONTEXTTID.toString()]
                record.contextItemColumns.add(col)

                unusedTids.remove(col.tid)
                if (col.qualifierTid != null)
                    unusedTids.remove(col.qualifierTid)
            }
        }

        if (unusedTids.size() != 0) {
            throw new MissingColumnsException("Did not know what to do with columns with tids: ${unusedTids}")
        }

	// now, finally, drop any context items for which we only have one result type.  Reason being that the transfer_result_map will
	// create these as "fixed", so don't bother to write these context items into the new file.
	def byLabel = records.groupBy { "${it.parentTid} ${makeLabel(it.resultType, it.statsModifier)} ${it.series}" }
	byLabel.entrySet().findAll { it.value.size() == 1 } . each {
		it.value[0].staticContextItems.clear()
	}

        ResultMap map = new ResultMap(aid, records, tids)
        return map;
    }

    //TODO: fix concentration unit check
    public ResultMap loadMap(Connection connection, Long aid) {
        Sql sql = new Sql(connection)
        List rows = sql.rows("SELECT TID, TIDNAME, PARENTTID, RESULTTYPE, STATS_MODIFIER, CONTEXTTID, CONTEXTITEM, CONCENTRATION, CONCENTRATIONUNIT, PANELNO, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2, SERIESNO, QUALIFIERTID, EXCLUDED_POINTS_SERIES_NO FROM result_map WHERE AID = ?", [aid])
        ResultMap map = convertToResultMap(aid.toString(), rows)
        return map
    }

    public ResultMap loadMap(Long aid) {
        ResultMap map;
        Experiment.withSession { Session session ->
            session.doWork(new Work() {
                void execute(Connection connection) throws SQLException {
                    map = loadMap(connection, aid);
                }
            })
        }
        return map;
    }

    public Map convertPubchemRowToMap(List<String> row, List<String> header) {
        String outcome = row[3]
        if (!StringUtils.isBlank(outcome)) {
            outcome = pubchemOutcomeTranslation[row[3]];
            if (outcome == null) {
                throw new RuntimeException("Did not know the name of a pubchem outcome: ${row[3]}");
            }
        }
        String activity = row[4]

        Map pubchemRow = [:]
        pubchemRow["-1"] = outcome
        pubchemRow["0"] = activity
        for(int i=0;i<header.size();i++) {
            pubchemRow[header[i]] = row[i]
        }
        return pubchemRow;
    }

    public void convert(Experiment experiment, String pubchemFilename, String outputFilename, ResultMap map){
        if (!map.hasTid("0"))
            throw new RuntimeException("Missing pubchem score from result map of ${map.aid}")

        if (!map.hasTid("-1"))
            throw new RuntimeException("Missing pubchem outcome from result map of ${map.aid}")

        List dynamicColumns = constructCapColumns(experiment)

        Map expItems = (experiment.experimentContexts.collectMany { ExperimentContext context ->
            context.experimentContextItems.collect { ExperimentContextItem item ->
                [item.attributeElement.label, item.valueDisplay]
            }
        }).collectEntries()

        CapCsvWriter writer = new CapCsvWriter(experiment.id, expItems, dynamicColumns, new FileWriter(outputFilename))
        CSVReader reader = new CSVReader(new FileReader(pubchemFilename))

        List<String> header = reader.readNext()
        Pattern numberPattern = Pattern.compile("\\d+")
        for(int i=0;i<header.size();i++) {
            if (numberPattern.matcher(header[i]).matches() && !map.hasTid(header[i])) {
                throw new MissingColumnsException("Result map of ${map.aid} missing tid ${header[i]}")
            }
        }

        Collection<ExperimentMeasure> rootMeasures = experiment.experimentMeasures.findAll { it.parent == null }
        while(true) {
            List<String> row = reader.readNext()
            if (row == null)
                break

            Long substanceId = Long.parseLong(row[0])
            Map pubchemRow = convertPubchemRowToMap(row, header);

            naMissingValues(pubchemRow, map)
            convertRow(rootMeasures, substanceId, pubchemRow, map, writer, null, null)
        }

        writer.close()
    }

    void convert(Long expId, String pubchemFilename, String outputFilename)  {
        Experiment experiment = Experiment.get(expId)
        ExternalReference ref = experiment.getExternalReferences().find {it.externalSystem.systemName == "PubChem"}
        Long aid = Long.parseLong(ref.extAssayRef.replace("aid=", ""));
        ResultMap map = loadMap(aid)
        convert(experiment, pubchemFilename, outputFilename, map)
    }
}
