package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.HierarchyType
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy

import java.util.regex.Pattern

class ResultsService {

    static Pattern RANGE_PATTERN = Pattern.compile("([^-]+)-(.*)")

    static class Column {
        String name;

        // if this column represents a measurement
        Measure measure;

        // if this column represents a context item
        AssayContextItem contextItem;

        // return a string if error.  Otherwise returns a cell
        def parseValue(String value) {
            String foundQualifier = null

            for(qualifier in Result.QUALIFIER_VALUES) {
                qualifier = qualifier.trim()
                if (value.startsWith(qualifier) && (foundQualifier == null || foundQualifier.length() < qualifier.length()) ) {
                    foundQualifier = qualifier
                }
            }

            if (foundQualifier == null) {
                foundQualifier = "="
            } else {
                value = value.substring(foundQualifier.length()).trim()
            }

            def rangeMatch = RANGE_PATTERN.matcher(value)
            if (rangeMatch.matches()) {
                float minValue, maxValue
                try
                {
                    minValue = Float.parseFloat(rangeMatch.group(1));
                    maxValue = Float.parseFloat(rangeMatch.group(2));
                }
                catch(NumberFormatException e)
                {
                    return "Could not parse \"${value}\" as a range"
                }

                Cell cell = new Cell(minValue: minValue, maxValue: maxValue)
                cell.column = this
                return cell
            } else {
                float a
                try
                {
                    a = Float.parseFloat(value);
                }
                catch(NumberFormatException e)
                {
                    return "Could not parse \"${value}\" as a number"
                }

                Cell cell = new Cell(value: a, qualifier: foundQualifier)
                cell.column = this

                return cell
            }
        }
    }

    static class Row {
        Integer rowNumber;
        Integer replicate;
        Integer parentRowNumber;
        Long sid;

        List<Cell> cells = [];
    }

    static class Cell {
        Column column;

        String qualifier;
        Float value;

        Float minValue;
        Float maxValue;

        Element element;
    }

    static String ASSAY_ID_LABEL = "Assay ID"
    static List FIXED_COLUMNS = ["Row #", "Substance", "Replicate #", "Parent Row #"]
    static int MAX_ERROR_COUNT = 100;

    static class Template {
        Assay assay;
        List<Column> constantItems;
        List<Column> columns;

        List asTable() {
            def lines = []

            lines.add(["",ASSAY_ID_LABEL, assay.id])
            lines.add(["","Experiment Name"])
            lines.add(["","Run Date"])

            // add the fields for values that are constant across entire experiment
            constantItems.each { lines.add(["",it.name]) }
            lines.add([])

            // add the first line of the header
            def row = []
            row.addAll(FIXED_COLUMNS)
            columns.each {row.add(it.name)}
            lines.add(row)

            return lines
        }

        List<Column> select(List<String> names) {
            def byName = [:]
            columns.each { byName[it.name] = it}
            return names.collect {byName.get(it)}
        }
    }

    Template generateMaxSchema(Assay assay) {
        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def measureItems = assayItems.findAll { it.assayContext.assayContextMeasures.size() > 0 }
        assayItems.removeAll(measureItems)

        return generateSchema(assay, assayItems as List, assay.measures as List, measureItems as List)
    }

    /**
     * Construct list of columns that a result upload could possibly contain
     */
    Template generateSchema(Assay assay, List<AssayContextItem> constantItems, List<Measure> measures, List<AssayContextItem> measureItems) {
        List<Column> constants = []
        List<Column> columns = []
        Set<String> usedNames = [] as Set

        // add all the non-fixed context items
        for(item in constantItems) {
            String name = item.attributeElement.label
            usedNames.add(name)

            Column column = new Column(name: name, contextItem: item)
            constants.add(column)
        }

        // add all of the measurements
        for(measure in measures) {
            String name = measure.displayLabel
            usedNames.add(name)

            Column column = new Column(name: name, measure: measure)
            columns.add(column)
        }

        // add all the measure context items
        for(item in measureItems) {
            String name = item.attributeElement.label
            usedNames.add(name)

            Column column = new Column(name: name, contextItem: item)
            columns.add(column)
        }

        return new Template(assay: assay, constantItems: constants, columns: columns)
    }

    public static class InitialParse {
        def errors;
        Map constants;
        List<Row> rows;
    }


    public static class ErrorCollector {
        def errors = []

        void addError(int line, int column, String message) {
            errors << "${line}:${column} ${message}"
        }

        boolean hasErrors() {
            return errors.size() > 0
        }

        boolean tooMany() {
            return errors.size() > MAX_ERROR_COUNT;
        }
    }

    static class LineReader {
        BufferedReader reader;
        int lineNumber = 0;

        String readLine() {
            lineNumber ++;
            return reader.readLine();
        }
    }

    Map<String, String> parseConstantRegion(LineReader reader,  ErrorCollector errors) {
        Map header = [:]

        while(true) {
            String line = reader.readLine();
            if (line == null)
                break;

            // initial header stops on first empty line
            if (line.trim().length() == 0) {
                break;
            }

            String [] values = line.split("\t")
            if (values.length != 3) {
                errors.addError(line, 1, values.length, "Wrong number of columns in initial header.  Expected 3 but got ${values.length} columns")
                continue
            }

            String key = values[1]
            String value = values[2]
            header.put(key, value)
        }

        return header
    }

    void forEachDataRow(LineReader reader, List<Column> columns, ErrorCollector errors, Closure fn) {
        int expectedColumnCount = columns.size() + FIXED_COLUMNS.size();

        while(true) {
            String line = reader.readLine();
            if (line == null)
                break;

            List values = line.split("\t")

            // verify and reshape columns
            while(values.size() < expectedColumnCount) {
                values.add("")
            }

            while(values.size() > expectedColumnCount) {
                String value = values.remove(values.size()-1)
                if (value.trim().length() != 0) {
                    errors.addError(reader.lineNumber, values.size()+1, "Found \"${value}\" in extra column")
                }
            }

            // now that values is guaranteed to be the right length, pass to the callback
            fn(reader.lineNumber, values)

            if (errors.tooMany())
                break
        }
    }

    def safeParse(ErrorCollector errors, List<String> values, int lineNumber, List<Closure> fns) {
        boolean hadFailure = false;

        Object[] parsed = new Object[fns.size()]
        for(int i=0;i<fns.size();i++) {
            try {
                parsed[i] = fns[i](values[i])
            } catch(Exception ex) {
                errors.addError(lineNumber, i, "Could not parse \"${values[i]}\"")
                hadFailure = true
                ex.printStackTrace()
            }
        }

        if (hadFailure) {
            return null;
        } else {
            return parsed;
        }
    }

    List<Column> parseTableHeader(LineReader reader, Template template, ErrorCollector errors)      {
        String header = reader.readLine()
        def columnNames = header.split("\t")

        // validate the fixed columns are where they should be
        for(int i = 0;i<FIXED_COLUMNS.size();i++) {
            if (columnNames.size() < i || columnNames[i] != FIXED_COLUMNS[i]) {
                errors.addError(reader.lineNumber, i, "Expected "+FIXED_COLUMNS[i]+" in column header at position "+(i+1))
            }
        }

        if (errors.hasErrors())
            return null

        def byName = [:]
        template.columns.each { byName[it.name] = it }

        def columns = []
        for(int i=FIXED_COLUMNS.size();i<columnNames.length;i++) {
            def name = columnNames[i]
            def column = byName.get(name)
            if (column == null) {
                errors.addError(reader.lineNumber, i, "Invalid column name \"${name}\"")
            } else {
                columns.add(column)
            }
        }

        return columns
    }

    def addHierachyRelationships(List<Result> childResults, List<Measure> childMeasures, List<Result> parentResults, List<Measure> parentMeasures, def getMeasureRelationship) {
        for(int i=0;i<childMeasures.size();i++){
            for(int j=0;j<parentMeasures.size();j++) {
                HierarchyType relationship = getMeasureRelationship(parentMeasures.get(j), childMeasures.get(i))

                Result childResult = childResults.get(i)
                Result parentResult = parentMeasures.get(j)

                ResultHierarchy resultHierarchy = new ResultHierarchy(hierarchyType: relationship, result: childResult, parentResult: parentResult)
                childResult.resultHierarchiesForParentResult.add(resultHierarchy)
                parentResult.resultHierarchiesForResult.add(resultHierarchy)
            }
        }
    }

    def isLinked(Column measureColumn, Column itemColumn) {
        def key = new Tuple(measureColumn.measure, itemColumn.contextItem)
    }

    def associateItemToResults(List<Result> results, List<Column> columns, Cell cell, Closure isLinked) {
        assert columns.size() == results.size()

        for(int i=0;i<results.size();i++) {
            Column column = columns.get(i)
            Result result = results.get(i)

            if (isLinked(column.measure, cell.column.contextItem)) {
                ResultContextItem item = new ResultContextItem(result: result, attributeElement: cell.column.contextItem.attributeElement, valueNum: cell.value, qualifier: cell.qualifier, valueMin: cell.minValue, valueMax: cell.maxValue, valueElement: cell.element)
                result.resultContextItems.add(item)
            }
        }
    }

    def createResults(InitialParse parse, ErrorCollector errors, Map<AssayContextItem, Collection<Measure>> measuresPerContextItem) {
        def rowByNumber = [:]
        parse.rows.each {
            rowByNumber[it.rowNumber] = it
        }

        def resultsByRowNumber = [:]
        def resultByCell = [:]

        // construct all the Result objects (one per cell belonging to a measure)
        for(row in parse.rows) {
            def results = []
            for(cell in row.cells) {
                if (cell.column.measure != null) {
                    def result = new Result(qualifier: cell.qualifier, valueNum: cell.value, statsModifier: cell.column.measure.statsModifier, resultType: cell.column.measure.resultType)
                    results << result
                    resultByCell[cell] = result
                }
            }
            resultsByRowNumber[row.rowNumber] = results
        }

        // create the parent/child links between measures
        for(row in parse.rows) {
            def results = []
            ResultsService.Row parentRow = rowByNumber[row.parentRowNumber]
            if (parentRow == null) {
                errors.addError(0, 0, "Could not find row ${row.parentRowNumber} but this row has child rows")
            } else {
                // project cells to results and link the two rows
                addHierachyRelationships(row.cells.collect {resultByCell[it]}, parentRow.cells.collect {resultByCell[it]})
            }
        }

        // and finally create the context items
        def isLinked = { measure, contextItem ->
            if(measuresPerContextItem.containsKey(contextItem)) {
                return measuresPerContextItem[contextItem].contains(measure)
            }
        }
        for(row in parse.rows) {
            def results = resultsByRowNumber.get(row.rowNumber)
            for(cell in row.cells) {
                if (cell.column.contextItem != null) {
                    associateItemToResults(results, cell, isLinked);
                }
            }
        }
    }

    InitialParse initialParse(Reader input, Template template) {
        LineReader reader = new LineReader(reader: new BufferedReader(input))
        ErrorCollector errors = new ErrorCollector()

        // first section
        Map constants = parseConstantRegion(reader, errors)
        if (errors.hasErrors())
            return

        // main header
        List<Column> columns = parseTableHeader(reader, template, errors)
        if (errors.hasErrors())
            return

        def parseInt = {x->Integer.parseInt(x)}
        def parseOptInt = {x-> if(x.trim().length() > 0) { return Integer.parseInt(x) } }
        def parseLong = {x->Long.parseLong(x)}

        // all data rows
        List rows = []
        forEachDataRow(reader, columns, errors) { int lineNumber, List<String> values ->
            def parsed = safeParse(errors, values, lineNumber, [ parseInt, parseLong, parseOptInt, parseOptInt ])

            if (parsed == null) {
                // if we got errors parsing the fixed columns, don't proceed to the rest of the columns
                return
            }

            Integer rowNumber = parsed[0]
            Long sid = parsed[1]
            Integer replicate = parsed[2]
            Integer parentRowNumber = parsed[3]

            Row row = new Row (rowNumber: rowNumber, replicate: replicate, parentRowNumber: parentRowNumber, sid: sid)

            // parse the dynamic columns
            for(int i=0;i<columns.size();i++) {
                String cellString = values[i+FIXED_COLUMNS.size()];
                if (cellString.isEmpty())
                    continue

                Column column = columns.get(i);
                def parseResult = column.parseValue(cellString);
                if (!(parseResult instanceof Cell)) {
                    errors.addError(lineNumber, i+FIXED_COLUMNS.size(), parseResult);
                } else {
                    row.cells.add(parseResult)
                }
            }

            rows.add(row)
        }

        return new InitialParse(constants: constants, errors: errors, rows: rows)
    }

}
