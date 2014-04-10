package adf

import au.com.bytecode.opencsv.CSVWriter
import bard.db.experiment.JsonResult
import bard.db.experiment.JsonSubstanceResults
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.apache.commons.lang3.StringUtils

import java.util.zip.GZIPInputStream

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/9/14
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
class JsonTransform {
    // step 1: construct abstract tree from results
    // step 2: construct boxes from abstract tree
    // step 3: walk through results a 2nd time.  For each node, look up boxes and if the box exists, row to map for box

    def run() {
        transform(new File('exp-1462-20131027-144131.json.gz'))
    }

    public write(List<JsonSubstanceResults> results) {
        AbstractResultTree tree = constructTree(results);
        // open files
        // write headers
        for(result in results) {
            Map<Box, List<Map>> rows = createRowsPerBox(result.rootElem, boxByPath)
            // write out each set of rows for each box
            // result.sid, rows
        }
        // close files
    }

    def callPerSid(jsonFile, nextRecordCallback) {
        GZIPInputStream gis = new java.util.zip.GZIPInputStream(jsonFile.newInputStream())
        final ObjectMapper mapper = new ObjectMapper()
        final ObjectReader objectReader = mapper.reader(JsonSubstanceResults)
        gis.eachLine { String line ->
            if (StringUtils.isNotBlank(line)) {
                nextRecordCallback(objectReader.readValue(line))
            }
        }

    }

    static class BoxesWriter {
        Map<Path, Box> boxByPath;
        Map<Box,CSVWriter> writerMap

        protected void writeRow(CSVWriter writer, List values) {
            String[] row = new String[values.size()];
            for(int i=0;i<row.length;i++) {
                def x = values.get(i);
                row[i] = x==null?"":x.toString();
            }
            writer.writeNext(row)
        }

        public BoxesWriter(String prefix, Map<Path, Box> boxByPath) {
            this.boxByPath = boxByPath
            this.writerMap = [:]

            int count = 0;
            for (Path path in boxByPath.keySet()) {
                Box box = boxByPath.get(path)
                List columnNames = box.getColumnNames();
                if(columnNames.size() == 0)
                    continue;

                CSVWriter writer = new CSVWriter(new FileWriter(prefix+(count+1)+".txt"));

                List<String> columns = ["sid", "resultId", "parentResultId"] + columnNames

                writeRow(writer, columns)
                writerMap.put(box, writer);

                count ++;
            }
        }

        public void close() {
            writerMap.values().each { it.close() }
        }

        long nextResultId = 1
        protected long getNextResultId() {
            long i = nextResultId
            nextResultId++;
            return i
        }

        protected Map<Box, List<BoxRow>> createRowsPerBox(List<JsonResult> results, Map<Path, Box> boxByPath) {
            Box rootBox = boxByPath[null]
            Map<Box, List<BoxRow>> perBoxData = [:]

            // add an intial empty row
            BoxRow nextRow = new BoxRow(getNextResultId(), 0)
            perBoxData[rootBox] = [nextRow]
            // assumes all root elements should be in the same box
            addRowsToBox(null, rootBox, results, boxByPath, perBoxData, nextRow)

            return perBoxData;
        }

        protected void addRowsToBox(Path parentPath, Box box, List<JsonResult> results, Map<Path, Box> boxByPath, Map<Box, List<BoxRow>> perBoxData, BoxRow row) {
            for(result in results) {
                Path path = new Path(result, parentPath);

                Box differentBox = boxByPath.get(path)
                if(differentBox != null) {
                    // if this result is contained in a different box, recurse into it
                    // first defining a new row to put the data into
                    List otherRows = perBoxData[differentBox]
                    if(otherRows == null) {
                        otherRows = []
                        perBoxData[differentBox] = otherRows
                    }
                    def nextRow = new BoxRow(getNextResultId(), row.resultId)
                    otherRows.add(nextRow)
                    addRowsToBox(path, differentBox, [result], boxByPath, perBoxData, nextRow);
                } else {
                    // otherwise add this result to the current row
                    row.put(new ResultKey(result), result.valueDisplay)
                    result.contextItems.each {
                        row.put(new ResultKey(it), it.valueDisplay)
                    }
                }
                addRowsToBox(path, box, result.related, boxByPath, perBoxData, row);
            }
        }

        public void write(JsonSubstanceResults results) {
            Map<Box, List<Map>> rowsPerBox = createRowsPerBox(results.rootElem, boxByPath);
            rowsPerBox.each { box, rows ->
                CSVWriter writer = writerMap[box]
                if(writer == null)
                    return;

                rows.each { row ->
                    List fullRow = [results.sid.toString(), row.resultId.toString(), row.parentResultId == 0 ? "" : row.parentResultId.toString()]
                    fullRow.addAll(
                        box.columns.collect { column ->
                            return row[column]
                        })

                    fullRow.addAll(
                            box.contextItems.collect { column ->
                                return row[column]
                            })

                    writeRow(writer, fullRow);
                }
            }
        }
    }

    def transform(File jsonFile) {
        List<JsonSubstanceResults> jsonSubstanceResultsList = []
        callPerSid(jsonFile) { results ->
            jsonSubstanceResultsList << results
        }

        // create the generalized form of the result tree
        AbstractResultTree tree = constructTree(jsonSubstanceResultsList);

        // based on the tree, map this tree to boxes
        Map<Path, Box> boxByPath = tree.constructBoxes()

        // open all the files
        BoxesWriter writer = new BoxesWriter(boxByPath)
        callPerSid(jsonFile, { results ->
            writer.write(results)
        })

        writer.close()
    }

    public AbstractResultTree constructTree(List<JsonSubstanceResults> results) {
        AbstractResultTree tree = new AbstractResultTree();

        for(result in results) {
            tree.update(result);
        }

        return tree;
    }

}
