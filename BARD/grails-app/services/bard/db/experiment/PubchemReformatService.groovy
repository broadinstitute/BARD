package bard.db.experiment

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.HierarchyType
import bard.db.enums.ValueType
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import bard.db.registration.ExternalReference
import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.commons.GrailsApplication

import java.sql.Connection
import java.sql.DriverManager
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/14/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
class PubchemReformatService {
    static final int SCREENING_CONCENTRATION_ID = 971;

    GrailsApplication grailsApplication;

    static class MissingColumnsException extends RuntimeException {
        MissingColumnsException(String s) {
            super(s)
        }
    }

    final static Map<String, String> pubchemOutcomeTranslation = new HashMap();
    final static Set<String> pubchemOutcomeValues = new HashSet();
    static {
        pubchemOutcomeTranslation.put("1", "Inactive");
        pubchemOutcomeTranslation.put("2", "Active");
        pubchemOutcomeTranslation.put("3", "Inconclusive");
        pubchemOutcomeTranslation.put("4", "Unspecified");
        pubchemOutcomeTranslation.put("5", "Probe");
        pubchemOutcomeValues.addAll(pubchemOutcomeTranslation.values())
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

        String parentChildRelationship;

        String resultType
        String statsModifier

        String qualifierTid
        Long eid

        Set<ResultMapContextColumn> contextItemColumns = [] as Set;
        Map<String, String> staticContextItems = [:];

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
                    ", eid="+eid +
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

        CapCsvWriter(Long experimentId, Map<String, String> experimentContextItems, List<String> dynamicColumns, Writer rawWriter) {
            writer = new CSVWriter(rawWriter)
            columnCount = dynamicColumns.size()
            for (int i = 0; i < columnCount; i++) {
                nameToIndex.put(dynamicColumns[i], i)
            }

            writer.writeNext(["", "Experiment ID", experimentId.toString()].toArray(new String[0]))
            experimentContextItems.each { k, v ->
                writer.writeNext(["", k, v].toArray(new String[0]))
            }
            writer.writeNext([].toArray(new String[0]))
            List header = ["Row #", "Substance", "Replicate #", "Parent Row #"]
            header.addAll(dynamicColumns)
            writer.writeNext(header.toArray(new String[0]))
        }

        int addRow(Long substanceId, Integer parentRow, String replicate, Map<String, String> row) {
            rowCount++;
            int rowNumber = rowCount;

            List<String> cols = new ArrayList()
            for (int i = 0; i < columnCount; i++)
                cols.add("")

            row.each { k, v ->
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

        public ResultMap(String aid, Collection<ResultMapRecord> rs, Set tids = null) {
            this.aid = aid;
            records = rs.groupBy { makeLabel(it.resultType, it.statsModifier) }
            recordsByParentTid = rs.groupBy { it.parentTid }

            if (tids == null) {
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

        Collection<Long> getPanelEids() {
            return ((allRecords.findAll {it.eid != null}).collect { it.eid } as Set)
        }

        Collection<ResultMapRecord> getChildRecords(String tid) {
            return recordsByParentTid.get(tid);
        }

        public int getNumberOfCustomColumns() {
            return (allRecords.findAll { it.tid > 0 }).size()
        }

        public boolean hasTid(String tid) {
            return tids.contains(tid);
        }

        /**
         * construct a list of CAP result rows for the given resultType, statsModifier and the parentTid
         */
        List<Map<String, String>> getValues(Map<String, String> pubchemRow, String resultType, String statsModifier, String parentTid, Long eidToInclude) {
            List<Map<String, String>> rows = []
            String label = makeLabel(resultType, statsModifier)
            Collection<ResultMapRecord> records = records.get(label).findAll { it.parentTid == parentTid }

            // each record represents a column in the pubchem file
            // loop through all the child columns of the given parent (parentTid)
            for (record in records) {
                if(record.eid != null && record.eid != eidToInclude) {
                    continue;
                }

                Map<String, String> kvs = ["Replicate #": record.series?.toString(), "TID": record.tid]

                // get the value for the current column (record) in the current row (pubchemRow)
//                println("looking for pubchemRow[${record.tid}] = ${pubchemRow[record.tid]}")
                String measureValue = pubchemRow[record.tid]

                if (measureValue != null && measureValue.length() > 0) {
                    if (record.qualifierTid != null) {
                        measureValue = pubchemRow[record.qualifierTid] + measureValue
                    }
                    kvs[label] = measureValue
                    addContextValues(pubchemRow, record, kvs)
                }

                rows.add(kvs)
            }

            return rows
        }

        void addContextValues(Map<String, String> pubchemRow, ResultMapRecord record, Map<String, String> kvs) {
            for (staticItem in record.staticContextItems.entrySet()) {
                kvs[staticItem.key] = staticItem.value
            }

            for (columnItem in record.contextItemColumns) {
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

    boolean memoizedHasNonBlankChild(Map<String, String> pubchemRow, ResultMap map, String tid, Map cache, Set seen) {
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
            for (record in map.getChildRecords(tid)) {
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
    void naMissingValues(Map<String, String> pubchemRow, ResultMap map) {
        List needsNa = []
        Map cache = [:]
        map.getTids().each { tid ->
            def v = pubchemRow[tid]
            if (StringUtils.isBlank(v) && memoizedHasNonBlankChild(pubchemRow, map, tid, cache, new HashSet())) {
                needsNa.add(tid)
            }
        }

        for (key in needsNa) {
            pubchemRow[key] = "NA"
        }
    }

    void convertRow(Collection<ExperimentMeasure> measures, Long substanceId, Map<String, String> pubchemRow, ResultMap map, CapCsvWriter writer, Integer parentRow, String parentTid, Long eidToInclude) {
        //println("convertRow: ${measures.collect { it.measure.resultType.label } }, ${parentRow}, ${parentTid}")
        for (expMeasure in measures) {
            String resultType = expMeasure.resultType?.label
            String statsModifier = expMeasure.statsModifier?.label

            List rows = map.getValues(pubchemRow, resultType, statsModifier, parentTid, eidToInclude)
//            println("Rows: ${rows}")
            for (row in rows) {
                int rowNumber = writer.addRow(substanceId, parentRow, row["Replicate #"], row)

                convertRow(expMeasure.childMeasures, substanceId, pubchemRow, map, writer, rowNumber, row["TID"], eidToInclude)
            }
        }
    }

    List constructCapColumns(Experiment experiment) {
        Set colNames = [] as Set

        experiment.experimentMeasures.each {
            // add measure name
            colNames.add(makeLabel(it.resultType?.label, it.statsModifier?.label))
            // add context items
            it.assayContextExperimentMeasures.each {
                it.assayContext.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }.each {
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
        List<ResultMapRecord> records = [];
        Set<String> unusedTids = new HashSet();
        Set<String> tids = new HashSet()

        // Keep track which TIDs we've used for something
        for (row in rows) {
            tids.add(row.TID.toString())
            unusedTids.add(row.TID.toString())

            if (row.EXCLUDED_POINTS_SERIES_NO != null || row.CONTEXTITEM == "do not import") {
                unusedTids.remove(row.TID.toString())
            }

        }

        // first pass: create all the items for result types
        for (row in rows) {
            if (row.RESULTTYPE != null) {
                if (!(row.CONTEXTTID == null || row.CONTEXTTID == row.TID)) {
                    throw new RuntimeException("aid=${aid} tid=${row.TID} appears to be both a result type and a context item")
                }
                ResultMapRecord record = new ResultMapRecord(series: row.SERIESNO,
                        tid: row.TID,
                        parentTid: row.PARENTTID?.toString(),
                        resultType: row.RESULTTYPE,
                        parentChildRelationship: row.RELATIONSHIP,
                        statsModifier: row.STATS_MODIFIER,
                        qualifierTid: row.QUALIFIERTID,
                        eid: row.PANELNO)
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
        for (row in rows) {
            if (row.CONTEXTTID != null && row.TID != row.CONTEXTTID) {
                if(row.RESULTTYPE != null) {
                    throw new RuntimeException("expected resulttype == null for TID ${row.TID} because CONTEXTTID != null")
                }
                ResultMapContextColumn col = new ResultMapContextColumn(attribute: row.CONTEXTITEM, tid: row.TID, qualifierTid: row.QUALIFIERTID)
                ResultMapRecord record = byTid[row.CONTEXTTID.toString()]
                if(record == null) {
                    throw new RuntimeException("Could not find tid ${row.CONTEXTTID} referenced by tid ${row.TID}")
                }
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
        byLabel.entrySet().findAll { it.value.size() == 1 }.each {
            it.value[0].staticContextItems.clear()
        }

        ResultMap map = new ResultMap(aid, records, tids)
        //println("map: ${map}")
        return map;
    }

    //TODO: fix concentration unit check
    public ResultMap loadMap(Connection connection, Long aid) {
        Sql sql = new Sql(connection)
        List rows = sql.rows("SELECT TID, TIDNAME, PARENTTID, RESULTTYPE, STATS_MODIFIER, CONTEXTTID, CONTEXTITEM, CONCENTRATION, CONCENTRATIONUNIT, PANELNO, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2, SERIESNO, QUALIFIERTID, EXCLUDED_POINTS_SERIES_NO, RELATIONSHIP FROM result_map WHERE AID = ?", [aid])
        ResultMap map = convertToResultMap(aid.toString(), rows)
        return map
    }

    public ResultMap loadMap(Long aid) {
        // the authoritative result map is in a different schema -- and we shouldn't
        // be making this call often, so just create/destroy the connection on each call
        String connectString = grailsApplication.config.bard.resultMapJdbcUrl
        if (connectString == null) {
            throw new RuntimeException("connectString bard.resultMapJdbcUrl is null")
        }

        Connection connection = DriverManager.getConnection(connectString, grailsApplication.config.bard.resultMapUsername, grailsApplication.config.bard.resultMapPassword)
        try {
            ResultMap map = loadMap(connection, aid);
            return map;
        } finally {
            connection.close();
        }
    }

    protected mapOutcomeToString(String outcome) {
        if (!StringUtils.isBlank(outcome) && !pubchemOutcomeValues.contains(outcome)) {
            String origOutcome = outcome
            outcome = pubchemOutcomeTranslation[origOutcome];
            if (outcome == null) {
                throw new RuntimeException("Did not know the name of a pubchem outcome: ${origOutcome}");
            }
        }
        return outcome
    }

    public Map convertPubchemRowToMap(PubchemHeader header, List<String> row, String outcomeTidOverride) {
        String activity = row[header.activityColumn]

        Map pubchemRow = [:]
        pubchemRow["-1"] = mapOutcomeToString(row[header.outcomeColumn])
        pubchemRow["0"] = activity
        for (int i = 0; i < header.headers.size(); i++) {
            String tid = header.headers[i]
            String value = row[i]
            if(tid.equals(outcomeTidOverride)) {
                pubchemRow["-1"] = mapOutcomeToString(value)
            } else {
                pubchemRow[tid] = value
            }
        }
        return pubchemRow;
    }

    public void convert(Experiment experiment, String pubchemFilename, String outputFilename, ResultMap map) {
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

        PubchemHeader header = new PubchemHeader(transformHeaderToOriginalConvention(reader.readNext() as List))

        for (tid in header.tids) {
            if (!map.hasTid(tid)) {
                throw new MissingColumnsException("Result map of ${map.aid} missing tid ${tid}")
            }
        }

        Collection<ExperimentMeasure> rootMeasures = experiment.experimentMeasures.findAll { it.parent == null }

        String overrideOutcomeTid = null;
        // check to see if this experiment has an outcome specific to it.
        map.allRecords.each {
            if (it.resultType == "PubChem outcome" && it.eid == experiment.id) {
                overrideOutcomeTid = it.tid
            }
        }

        while (true) {
            List<String> row = reader.readNext()
            if (row == null)
                break

            Long substanceId = Long.parseLong(row[header.sidColumn])
            Map pubchemRow = convertPubchemRowToMap(header, row, overrideOutcomeTid);

            naMissingValues(pubchemRow, map)
            convertRow(rootMeasures, substanceId, pubchemRow, map, writer, null, null, experiment.id)
        }

        writer.close()
    }

    // the original format of pubchem results where to have some extra columns, and then have each TID column only identified by a number.  At some point (around May) they appear
    // to have switched to a format where the tid names are listed instead of the numbers.   This method attempts to change those labels to numbers so the rest of the
    // pubchem conversion can work as before.
    List<String> transformHeaderToOriginalConvention(List<String> header) {
        String headerLine = header.join(",")
        if(headerLine.startsWith("PUBCHEM_SID,PUBCHEM_CID,PUBCHEM_ACTIVITY_OUTCOME,PUBCHEM_ACTIVITY_SCORE,PUBCHEM_ACTIVITY_URL,PUBCHEM_ASSAYDATA_COMMENT")) {
            List<String> tids = []
            for(int i=0;i<header.size()-6;i++) {
                tids << (1+i).toString()
            }
            return header.subList(0,6) + tids
        } else if (headerLine.startsWith("PUBCHEM_SID,PUBCHEM_EXT_DATASOURCE_REGID,PUBCHEM_CID,PUBCHEM_ACTIVITY_OUTCOME,PUBCHEM_ACTIVITY_SCORE,PUBCHEM_ACTIVITY_URL,PUBCHEM_ASSAYDATA_COMMENT,PUBCHEM_ASSAYDATA_REVOKE")) {
            return header
        } else {
            throw new RuntimeException("Did not understand recognize header format: ${header}")
        }
    }

    void convert(Long expId, String pubchemFilename, String outputFilename) {
        Experiment experiment = Experiment.get(expId)
        ExternalReference ref = experiment.getExternalReferences().find { it.externalSystem.systemName == "PubChem" }
        Long aid = Long.parseLong(ref.extAssayRef.replace("aid=", ""));
        ResultMap map = loadMap(aid)
        convert(experiment, pubchemFilename, outputFilename, map)
    }

    static class MeasureStub {
        String resultType
        String statsModifier
        String parentChildRelationship;
        Collection<MeasureStub> children = [];
        Map<String, Collection<String>> contextItems = [:]
        Collection<String> contextItemColumns = []


        @Override
        public java.lang.String toString() {
            return "MeasureStub{" +
                    "resultType='" + resultType + '\'' +
                    ", statsModifier='" + statsModifier + '\'' +
                    ", parentChildRelationship='" + parentChildRelationship + '\'' +
                    ", children=" + children +
                    ", contextItems=" + contextItems +
                    ", contextItemColumns=" + contextItemColumns +
                    '}';
        }
    }

    static class MappedStub {
        Element resultType
        Element statsModifier
        HierarchyType parentChildRelationship
        MappedStub parent;
        Collection<MappedStub> children = [];
        Map<Element, Collection<String>> contextItems = [:]
        Collection<Element> contextItemColumns = []


        @Override
        public java.lang.String toString() {
            return "MappedStub{" +
                    "resultType=" + resultType +
                    ", statsModifier=" + statsModifier +
                    ", parentChildRelationship=" + parentChildRelationship +
                    ", children=" + children +
                    ", contextItems=" + contextItems +
                    ", contextItemColumns=" + contextItemColumns +
                    '}';
        }
    }

    public MappedStub mapStub(MeasureStub stub) {
        MappedStub mapped = new MappedStub()
        mapped.resultType = Element.findByLabel(stub.resultType)
        if (mapped.resultType == null) {
            throw new RuntimeException("Could not find result type ${stub.resultType}")
        }
        if (stub.statsModifier) {
            mapped.statsModifier = Element.findByLabel(stub.statsModifier)
            if (mapped.statsModifier == null)
                throw new RuntimeException("Could not find stats modifier ${stub.statsModifier}")
        }

        String relationship = stub.parentChildRelationship
        if (relationship != null && ((relationship.toLowerCase() == "derives") || (relationship.toLowerCase() == "calculated from")))
            mapped.parentChildRelationship = HierarchyType.CALCULATED_FROM
        else if (relationship != null && (relationship.toLowerCase() == "supported by"))
            mapped.parentChildRelationship = HierarchyType.SUPPORTED_BY
        else if (relationship == null)
            mapped.parentChildRelationship = null;
        else
            throw new RuntimeException("Could not parse relationship ${relationship}");

        for (attribute in stub.contextItems.keySet()) {
            Element attributeElement = Element.findByLabel(attribute)
            if (attributeElement == null) {
                throw new RuntimeException("Could not find element with label ${attribute}")
            }
            mapped.contextItems[attributeElement] = stub.contextItems[attribute]
        }
        mapped.contextItemColumns = stub.contextItemColumns.collect {
            Element e = Element.findByLabel(it)
            if (e == null) {
                throw new RuntimeException("Could not find element with label ${it} for contextColumn")
            }
            return e
        }

        mapped.children = stub.children.collect {
            MappedStub mappedChild = mapStub(it)
            mappedChild.parent = mapped
            return mappedChild
        }

        return mapped
    }

    static class MeasureAndParents {
        MeasureStub measure;
        Collection<String> tids
    }

    Object getUniqueValue(Collection values) {
        Set set = new HashSet()
        set.addAll(values)
        if (set.size() != 1) {
            throw new RuntimeException("Expected a single value but got: ${set}")
        }

        return set.first()
    }

    MeasureStub createMeasure(Collection<ResultMapRecord> resultTypes) {
        MeasureStub measure = new MeasureStub()
        measure.resultType = (getUniqueValue(resultTypes.collect { it.resultType }))
        measure.statsModifier = (getUniqueValue(resultTypes.collect { it.statsModifier }))
        measure.parentChildRelationship = getUniqueValue(resultTypes.collect { it.parentChildRelationship })
        //println("resultTypes=${resultTypes}")
        measure.contextItems = resultTypes.collectMany { it.staticContextItems.entrySet() }.groupBy { it.key }.collectEntries { key, value -> [key, value.collect { it.value }] }
        //println("contextItems=${measure.contextItems}")
        measure.contextItemColumns = resultTypes.collectMany { it.contextItemColumns.collect { x -> x.attribute } }
        return measure
    }

    Collection<MeasureAndParents> getChildMeasures(ResultMap resultMap, Collection<String> parentTids) {
        Collection<ResultMapRecord> childResultTypes = resultMap.allRecords.findAll { parentTids.contains(it.parentTid) }
        Map uniqueResultTypes = childResultTypes.groupBy { [it.resultType, it.statsModifier] }
        Collection<MeasureAndParents> children = uniqueResultTypes.values().collect { Collection<ResultMapRecord> records ->
            new MeasureAndParents(measure: createMeasure(records), tids: records.collect { it.tid })
        }
        return children;
    }

    void addChildMeasures(ResultMap resultMap, Collection<MeasureStub> childrenDest, Collection<ResultMapRecord> children) {
        children.each { MeasureAndParents measureAndParents ->
            childrenDest.add(measureAndParents.measure)
            Collection<ResultMapRecord> nextChildren = getChildMeasures(resultMap, measureAndParents.tids)
            addChildMeasures(resultMap, measureAndParents.measure.children, nextChildren)
        }
    }

    Collection<MeasureStub> createMeasures(ResultMap resultMap) {
        Collection<MeasureStub> measures = []
        addChildMeasures(resultMap, measures, getChildMeasures(resultMap, [null]))
        return measures
    }

    String makeMeasureKey(ExperimentMeasure measure) {
        String prefix = ""
        if (measure.parent != null) {
            prefix = makeMeasureKey(measure.parent)
        }
        return "${prefix}/${measure.resultType?.id},${measure.statsModifier?.id}"
    }

    String makeMeasureKey(MappedStub stub) {
        String prefix = ""
        if (stub.parent != null) {
            prefix = makeMeasureKey(stub.parent)
        }
        return "${prefix}/${stub.resultType?.id},${stub.statsModifier?.id}"
    }

    // recreate experiment measures on an already existing experiment/assay (Given a ResultMap)
    void recreateMeasures(Experiment experiment, ResultMap resultMap) {
        Collection<MappedStub> newMeasures = createMeasures(resultMap).collect { mapStub(it) }
        for (m in newMeasures)
        //println("newMeasure: ${m.resultType.label} children: ${m.children.collect {it.resultType.label} }")
            recreateMeasures(experiment, newMeasures)
    }

    // recreate experiment measures on an already existing experiment/assay
    void recreateMeasures(Experiment experiment, Collection<MappedStub> newMeasures) {
        Map<String, ExperimentMeasure> measureByKey = [:]
        for (measure in experiment.experimentMeasures) {
            measureByKey[makeMeasureKey(measure)] = measure
            //println("existing measure: ${measure.resultType.label}")
        }

        ExperimentMeasure.withSession { s -> s.flush() }

        // delete all existing experiment measures
        for (ExperimentMeasure experimentMeasure in new ArrayList(experiment.experimentMeasures)) {
            experiment.removeFromExperimentMeasures(experimentMeasure)
            experimentMeasure.delete()
        }

        ExperimentMeasure.withSession { s -> s.flush() }

        // now, create the experiment measures anew
        createExperimentMeasures(experiment, measureByKey, newMeasures, null)
    }

    void createExperimentMeasures(Experiment experiment, Map<String, ExperimentMeasure> measureByKey, Collection<MappedStub> newMeasures, ExperimentMeasure parentExperimentMeasure) {
        for (newMeasure in newMeasures) {
            ExperimentMeasure experimentMeasure = new ExperimentMeasure(dateCreated: new Date())
            if (parentExperimentMeasure != null) {
                experimentMeasure.parentChildRelationship = newMeasure.parentChildRelationship
                parentExperimentMeasure.addToChildMeasures(experimentMeasure)
            }
            experimentMeasure.resultType = newMeasure.resultType
            experimentMeasure.statsModifier = newMeasure.statsModifier
            experiment.addToExperimentMeasures(experimentMeasure)

            experimentMeasure.save(failOnError: true)

            createExperimentMeasures(experiment, measureByKey, newMeasure.children, experimentMeasure)

            Collection<Element> elementKeys = newMeasure.contextItems.keySet().findAll { newMeasure.contextItems[it].size() > 1 }
            if (elementKeys.size() > 0 || newMeasure.contextItemColumns.size() > 0) {
                String contextTitle = "annotations for ${experimentMeasure.resultType.label}"

                // try to find an existing context that is shaped exactly like we want
                AssayContext assayContext = findAssayContextForResultType(experiment.assay, elementKeys, newMeasure.contextItems, newMeasure.contextItemColumns, contextTitle)
                if(assayContext == null) {
                    // if couldn't find one, create a new context
                    assayContext = createAssayContextForResultType(experiment.assay, elementKeys, newMeasure.contextItems, newMeasure.contextItemColumns, contextTitle)
                }

                // regardless if whether it was created or found, link it to this measure
                AssayContextExperimentMeasure assayContextExperimentMeasure = new AssayContextExperimentMeasure()
                assayContext.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)
                experimentMeasure.addToAssayContextExperimentMeasures(assayContextExperimentMeasure)

                assayContextExperimentMeasure.save(failOnError: true)
            }
        }
    }

    boolean assayContextMatches(AssayContext context, String contextName, Set<Element> attributeKeys) {
        if(context.contextName != contextName)
            return false;

        Set<Element> nonfixedAttributes = (context.contextItems.findAll {it.attributeType != AttributeType.Fixed} . collect {it.attributeElement} ) as Set

        if(nonfixedAttributes.size() != attributeKeys.size())
            return false;

        return attributeKeys.containsAll(nonfixedAttributes)
    }

    AssayContext findAssayContextForResultType(Assay assay, Collection<Element> attributeKeys, Map<Element, Collection<String>> attributeValues, Collection<Element> freeAttributes, String contextName) {
        for(context in assay.contexts) {
            if(assayContextMatches(context, contextName, (attributeKeys as Set) + (freeAttributes as Set)))
                return context;
        }
        return null;
    }

    AssayContext createAssayContextForResultType(Assay assay, Collection<Element> attributeKeys, Map<Element, Collection<String>> attributeValues, Collection<Element> freeAttributes, String contextName) {
        AssayContext assayContext = new AssayContext()
        assayContext.contextName = contextName
        assayContext.contextType = ContextType.EXPERIMENT

        for (attribute in attributeKeys) {
            if (attribute.id == SCREENING_CONCENTRATION_ID) {
                // handle screening concentration as a special case:  Collapse all the values to a range instead a list
                float maxConcentration = Collections.max(attributeValues[attribute].collect { Float.parseFloat(it) }) * 10

                AssayContextItem item = new AssayContextItem();
                item.attributeType = AttributeType.Range
                item.valueType = ValueType.RANGE
                item.attributeElement = attribute
                item.valueMin = 0
                item.valueMax = maxConcentration
                item.valueDisplay = [item.valueMin, item.valueMax].join(' - ')

                assayContext.addToAssayContextItems(item)
            } else {
                Set uniqueValues = new HashSet(attributeValues[attribute])
                for (value in uniqueValues) {
                    AssayContextItem item = new AssayContextItem();
                    item.attributeType = AttributeType.List
                    item.attributeElement = attribute
                    item.valueDisplay = item.deriveDisplayValue()

                    assignValue(item, value)

                    assayContext.addToAssayContextItems(item)
                }
            }
        }

        for (freeAttribute in freeAttributes) {
            AssayContextItem item = new AssayContextItem();
            item.attributeType = AttributeType.Free
            item.attributeElement = freeAttribute
            assayContext.addToAssayContextItems(item)
            item.valueDisplay = item.deriveDisplayValue()
        }

        assay.addToAssayContexts(assayContext)

        assayContext.save(failOnError: true)
        return assayContext
    }

    public void assignValue(AssayContextItem item, String value) {
        // if term is an external identifier
        if (item.attributeElement.externalURL != null) {
            item.extValueId = value
        } else {
            // try parsing as a number
            boolean populatedValueNum = false
            try {
                item.valueNum = Float.parseFloat(value)
                item.qualifier = "= "
                populatedValueNum = true
            } catch (NumberFormatException ex) {
            }

            // if that doesn't work, look it up as a dictionary element
            if (!populatedValueNum) {
                item.valueElement = Element.findByLabel(value)
                if (item.valueElement == null) {
                    throw new RuntimeException("Could not find element with label ${value}")
                }
            }
        }

        item.valueDisplay = value
    }
}

class PubchemHeader {
    List<String> headers;
    int sidColumn;
    int outcomeColumn;
    int activityColumn;
    List<String> tids;

    public PubchemHeader(List<String> headers) {
        this.headers = headers
        sidColumn = headers.indexOf("PUBCHEM_SID")
        outcomeColumn = headers.indexOf("PUBCHEM_ACTIVITY_OUTCOME")
        activityColumn = headers.indexOf("PUBCHEM_ACTIVITY_SCORE")

        tids = []
        Pattern numberPattern = Pattern.compile("\\d+")
        for (int i = 0; i < headers.size(); i++) {
            if (numberPattern.matcher(headers[i]).matches()) {
                tids.add(headers[i])
            }
        }
    }
}