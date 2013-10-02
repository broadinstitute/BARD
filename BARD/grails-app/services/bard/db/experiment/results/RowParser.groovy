package bard.db.experiment.results

import bard.db.experiment.ExperimentContext
import bard.db.experiment.ResultsService

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 9/30/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
class RowParser {
    String experimentName;
    Long experimentId;

    List<ExperimentContext> contexts = []

    int linesParsed;

    List<String> columns;
    ImportSummary summary;
    List<List<String>> topLines;

    Set<Long> sampleIds = new java.util.HashSet()
    Long currentSampleId = null;
    ResultsService.LineReader reader;
    Closure lineCallback
    Row lastReadRow = null;
    boolean reachedEnd = false;

    Object[] safeParse(ImportSummary errors, List<String> values, int lineNumber, List<Closure> fns) {
        boolean hadFailure = false;

        Object[] parsed = new Object[fns.size()]
        for (int i = 0; i < fns.size(); i++) {
            try {
                parsed[i] = fns[i](values[i])
            } catch (Exception ex) {
                errors.addError(lineNumber, i, "Could not parse \"${values[i]}\"")
                hadFailure = true
//                ex.printStackTrace()
            }
        }

        if (hadFailure) {
            return null;
        } else {
            return parsed;
        }
    }


    public void setup(ResultsService.LineReader reader, List<String> columns, ImportSummary summary) {
        this.summary = summary;
        this.reader = reader
        this.columns = columns

        def parseInt = { x -> Integer.parseInt(x) }
        def parseOptInt = { x ->
            if (x.trim().length() > 0) {
                return Integer.parseInt(x)
            }
        }
        def parseLong = { x -> Long.parseLong(x) }

        // all data rows
        Set usedRowNumbers = [] as Set
        lineCallback = { int lineNumber, List<String> values ->
            def parsed = safeParse(summary, values, lineNumber, [parseInt, parseLong, parseOptInt, parseOptInt])

            if (parsed == null) {
                // if we got errors parsing the fixed columns, don't proceed to the rest of the columns
                return null;
            }

            Integer rowNumber = parsed[0]
            Long sid = parsed[1]
            Integer replicate = parsed[2]
            Integer parentRowNumber = parsed[3]

            if (sid <= 0) {
                summary.addError(lineNumber, 0, "Invalid substance id ${sid}")
                return null;
            }

            if (usedRowNumbers.contains(rowNumber)) {
                summary.addError(lineNumber, 0, "Row number ${rowNumber} was duplicated")
                return null;
            }
            usedRowNumbers.add(rowNumber)

            Row row = new Row(lineNumber: lineNumber, rowNumber: rowNumber, replicate: replicate, parentRowNumber: parentRowNumber, sid: sid)

            // parse the dynamic columns
            for (int i = 0; i < columns.size(); i++) {
                String cellString = values[i + ResultsService.FIXED_COLUMNS.size()];
                if (cellString.isEmpty())
                    continue

                String column = columns.get(i);
                row.cells.add(new RawCell(columnName: column, value: cellString));
            }

            return row;
        }
    }

    /*
        Returns null if reached end of file.  Otherwise a list of Row objects
     */
    public List<Row> readNextSampleRows( ) {
        if(reachedEnd) {
            return null;
        }

        List<Row> rows = []
        if(lastReadRow != null) {
            rows.add(lastReadRow)
        }

        while (true) {
            List<String> values = reader.readLine() as List
            if (values == null) {
                reachedEnd = true;
                break;
            }

            boolean good = preprocessLine(columns, values, summary, reader.lineNumber)

            if (good) {
                // pass to the callback
                Row row = lineCallback(reader.lineNumber, values)
                if(row == null) {
                    continue
                }

                // make sure the row is for the current sample
                if(currentSampleId == null || row.sid == currentSampleId) {
                    rows.add(row)
                    if(currentSampleId == null) {
                        currentSampleId = row.sid
                        if(sampleIds.contains(currentSampleId)) {
                            summary.addError(reader.lineNumber, 0, "Encountered sample ID ${currentSampleId} in two places in file.  All records for a specific sample must be contigous")
                        } else {
                            sampleIds.add(currentSampleId)
                        }
                    }
                    currentSampleId = row.sid
                } else {
                    // if the row is for a new sample, flush this row by returning it
                    lastReadRow = row
                    currentSampleId = null
                    break
                }
            }

            if (summary.tooMany()) {
                break
            }
        }

        return rows;
    }

    boolean preprocessLine(List<String> columns, List<String> values, ImportSummary summary, int lineNumber) {
        int expectedColumnCount = columns.size() + ResultsService.FIXED_COLUMNS.size();

        // verify and reshape columns
        while (values.size() < expectedColumnCount) {
            values.add("")
        }

        // verify there aren't too many columns
        while (values.size() > expectedColumnCount) {
            String value = values.remove(values.size() - 1)
            if (value.trim().length() != 0) {
                summary.addError(lineNumber, values.size() + 1, "Found \"${value}\" in extra column")
            }
        }

        // now that values is guaranteed to be the right length, make the entire row isn't empty
        boolean allEmpty = true;
        for (cell in values) {
            if (!cell.isEmpty()) {
                allEmpty = false;
                break;
            }
        }

        return !allEmpty;
    }
}
