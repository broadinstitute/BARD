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

    List<String> columns;
    ImportSummary summary;

    Set<Long> sampleIds = new java.util.HashSet()
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

        Long currentSampleId = null;
        List<Row> rows = []

        while (true) {
            Row row;

            boolean keepGoing = true;

            if(lastReadRow != null) {
                row = lastReadRow
                lastReadRow = null;
            } else {
                List<String> values = reader.readLine() as List
                if (values == null) {
                    reachedEnd = true;
                    break;
                }

                keepGoing = preprocessLine(columns, values, summary, reader.lineNumber)

                if (keepGoing) {
                    // pass to the callback
                    row = lineCallback(reader.lineNumber, values)
                    if(row == null) {
                        continue
                    }
                }
            }

            if(keepGoing) {
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
                    break
                }
            }

            if (summary.tooMany()) {
                break
            }
        }

        // double check to make sure we have only rows for a single sample
        Set uniqueSamples = new HashSet(rows.collect { it.sid })
        if (uniqueSamples.size() > 1) {
            throw new RuntimeException("Expected a single sample, but got "+uniqueSamples);
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
