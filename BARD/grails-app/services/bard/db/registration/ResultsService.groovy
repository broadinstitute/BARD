package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.HierarchyType
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy
import bard.db.experiment.Substance

import java.util.regex.Matcher
import java.util.regex.Pattern

class ResultsService {

    static String NUMBER_PATTERN_STRING = "[+-]?[0-9]+(\\.[0-9]*)?([Ee][+-]?[0-9]+)?"

    // pattern matching a number
    static Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_PATTERN_STRING)

    static String QUALIFIER_PATTERN_STRING = (Result.QUALIFIER_VALUES.collect{ "(?:${it.trim()})"}).join("|")

    // pattern matching a qualifier followed by a number
    static Pattern QUALIFIED_NUMBER_PATTERN = Pattern.compile("(${QUALIFIER_PATTERN_STRING})?\\s*(${NUMBER_PATTERN_STRING})")

    // pattern matching a range of numbers.  Doesn't actually check that the two parts are numbers
    static Pattern RANGE_PATTERN = Pattern.compile("([^-]+)-(.*)")

    static String DELIMITER = ","

    static String EXPERIMENT_ID_LABEL = "Experiment ID"
    static String EXPERIMENT_NAME_LABEL = "Experiment Name"
    static List FIXED_COLUMNS = ["Row #", "Substance", "Replicate #", "Parent Row #"]
    static int MAX_ERROR_COUNT = 100;

    ItemService itemService
    PugService pugService

    static boolean isNumber(value) {
        return NUMBER_PATTERN.matcher(value).matches()
    }

    static def parseListValue(Column column, String value, List<AssayContextItem> contextItems) {
        if (isNumber(value)) {
            float v = Float.parseFloat(value)
            float smallestDelta = Float.MAX_VALUE
            float closestValue = Float.NaN

            contextItems.each {
                def delta = Math.abs(it.valueNum - v)
                if (delta < smallestDelta) {
                    smallestDelta = delta
                    closestValue = it.valueNum
                }
            }
            return new Cell(value: closestValue, qualifier: "=", column: column)
        } else {
            def labelMap = [:]
            contextItems.each {
                if(it.valueElement != null)
                    labelMap[it.valueElement.label] = it.valueElement
            }
            Element element = labelMap[value]
            if (element == null) {
                return "Could not find \"${value}\" among values in list: ${labelMap.keySet()}"
            }
            return new Cell(element: element, column: column)
        }
    }

    static def makeItemParser(ItemService.Item item) {
        return { Column column, String value ->
            if (item.type == AttributeType.List) {
                return parseListValue(column, value, item.contextItems)
            } else if (item.type == AttributeType.Free) {
                return parseNumberOrRange(column, value)
            } else if (item.type == AttributeType.Range) {
                Double rangeMin = item.contextItems[0].valueMin
                Double rangeMax = item.contextItems[0].valueMax
                Double rangeName = item.attributeElement.label

                float floatValue = Float.parseFloat(value)

                if (floatValue < rangeMin || floatValue > rangeMax) {
                    return "The value \"${floatValue}\" outside of allowed range (${rangeMin} - ${rangeMax}) for ${rangeName}"
                }

                return new Cell(value: floatValue, column: column)
            } else {
                throw new RuntimeException("Did not know how to handle attribute type "+item.type)
            }
        }
    }

    static def parseQualifiedNumber(Column column, String value) {
        Matcher matcher = QUALIFIED_NUMBER_PATTERN.matcher(value)

        if (matcher.matches()) {
            String foundQualifier = matcher.group(1)
            if (foundQualifier == null) {
                foundQualifier = "="
            }

            float a
            try
            {
                a = Float.parseFloat(matcher.group(2));
            }
            catch(NumberFormatException e)
            {
                return "Could not parse \"${matcher.group(2)}\" as a number"
            }

            Cell cell = new Cell(value: a, qualifier: foundQualifier, column: column)

            return cell
        } else {
            return "Could not parse \"${value}\" as a number with optional qualifier"
        }
    }

    static def parseRange(Column column, String value) {
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

            Cell cell = new Cell(minValue: minValue, maxValue: maxValue, column: column)
            return cell
        }

        return "Expected a range, but got \"${value}\""
    }

    static def parseNumberOrRange(Column column, String value) {
        if (RANGE_PATTERN.matcher(value).matches()) {
            return parseRange(column, value)
        } else {
            return parseQualifiedNumber(column, value)
        }
    }

    static class Column {
        String name;

        // if this column represents a measurement
        Measure measure;

        // if this column represents a context item
        ItemService.Item item;

        Closure parser;

        // return a string if error.  Otherwise returns a Cell
        def parseValue(String value) {
            return parser(this, value)
        }

        public Column(String name, Measure measure) {
            this.name = name
            this.measure = measure
            this.parser = { Column column, String value -> parseNumberOrRange(column, value) }
        }

        public Column(String name, ItemService.Item item) {
            this.item = item;
            this.name = name;
            this.parser = makeItemParser(item)
        }
    }

    static class Row {
        int lineNumber;

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

        public Element getAttributeElement() {
            return column.item.attributeElement;
        }
    }

    static class ImportSummary {
        def resultsCreated;
        def errors = []

        void addError(int line, int column, String message) {
            if (!tooMany()) {
                if (line != 0) {
                    errors << "On line ${line}, column ${column+1}: ${message}"
                } else {
                    errors << message
                }
            }
        }

        boolean hasErrors() {
            return errors.size() > 0
        }

        boolean tooMany() {
            return errors.size() > MAX_ERROR_COUNT;
        }
    }


    static class Template {
        Experiment experiment;
        List<Column> constantItems;
        List<Column> columns;

        List asTable() {
            def lines = []

            lines.add(["",EXPERIMENT_ID_LABEL, experiment.id])
            lines.add(["",EXPERIMENT_NAME_LABEL, experiment.experimentName])

            // add the fields for values that are constant across entire experiment
            constantItems.each { lines.add(["",it.name]) }
            lines.add([])

            // add the first line of the header
            def row = []
            row.addAll(FIXED_COLUMNS)
            columns.each {row.add(it.name)}
            lines.add(row)
            lines.add(["1"])

            return lines
        }

        List<Column> select(List<String> names) {
            def byName = [:]
            columns.each { byName[it.name] = it}
            return names.collect {byName.get(it)}
        }
    }

    List generateMaxSchemaComponents(Experiment experiment) {
        def assay = experiment.assay

        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def measureItems = assayItems.findAll { it.assayContext.assayContextMeasures.size() > 0 }
        assayItems.removeAll(measureItems)

        return [itemService.getLogicalItems(assayItems), experiment.experimentMeasures.collect {it.measure} as List, itemService.getLogicalItems(measureItems)]
    }

    Template generateMaxSchema(Experiment experiment) {
        def (experimentItems, measures, measureItems) = generateMaxSchemaComponents(experiment)
        return generateSchema(experiment, experimentItems, measures, measureItems)
    }

    /**
     * Construct list of columns that a result upload could possibly contain
     */
    Template generateSchema(Experiment experiment, List<ItemService.Item> constantItems, List<Measure> measures, List<ItemService.Item> measureItems) {
        List<Column> constants = []
        List<Column> columns = []
        Set<String> usedNames = [] as Set

        // add all the non-fixed context items
        for(item in constantItems) {
            String name = item.attributeElement.label
            usedNames.add(name)

            Column column = new Column(name, item)
            constants.add(column)
        }

        // add all of the measurements
        for(measure in measures) {
            String name = measure.displayLabel
            usedNames.add(name)

            Column column = new Column(name, measure)
            columns.add(column)
        }

        // add all the measure context items
        for(item in measureItems) {
            String name = item.attributeElement.label
            usedNames.add(name)

            Column column = new Column(name, item)
            columns.add(column)
        }

        return new Template(experiment: experiment, constantItems: constants, columns: columns)
    }

    public static class InitialParse {
        String experimentName;
        Long experimentId;
        List<ExperimentContext> contexts

        List<Row> rows;
    }

    static class LineReader {
        BufferedReader reader;
        int lineNumber = 0;

        String readLine() {
            lineNumber ++;
            return reader.readLine();
        }
    }

    boolean allEmptyColumns(String line) {
        String[] columns = line.split(DELIMITER);
        for(column in columns) {
            if (!column.isEmpty())
                return false
        }
        return true
    }

    InitialParse parseConstantRegion(LineReader reader,  ImportSummary errors, List<Column> experimentItemDefs) {
        Map<String,Column> nameToColumn = [:]
        Map<AssayContext,Collection<Cell>> groupedByContext = [:]
        Map header = [:]
        InitialParse result = new InitialParse()

        // populate map so we can look up columns by name
        experimentItemDefs.each {nameToColumn[it.name] = it}

        while(true) {
            String line = reader.readLine();
            if (line == null)
                break;

            // initial header stops on first empty line
            if (allEmptyColumns(line)) {
                break;
            }

            String [] values = line.split(DELIMITER)
            if (values.length != 3) {
                errors.addError(reader.lineNumber, values.length, "Wrong number of columns in initial header.  Expected 3 but got ${values.length} columns")
                continue
            }

            if (!values[0].isEmpty()) {
                errors.addError(reader.lineNumber, 0, "First column should be empty in the constant section at top of table")
                continue
            }

            String key = values[1]
            if (key == EXPERIMENT_ID_LABEL) {
                result.experimentId = Long.parseLong(values[2])
            } else if (key == EXPERIMENT_NAME_LABEL) {
                result.experimentName = values[2]
            } else if (nameToColumn.get(key)) {
                Column column = nameToColumn.get(key)

                def parsed = column.parseValue(values[2])
                if (parsed instanceof Cell) {
                    List group = groupedByContext[column.item.assayContext]
                    if (group == null) {
                        group = []
                        groupedByContext[column.item.assayContext] = group
                    }
                    group.add(parsed)
                } else {
                    errors.addError(0, 0, parsed)
                }
            } else {
                errors.addError(reader.lineNumber, 1, "Unknown name \"${key}\" in constant section")
                continue
            }

            String value = values[2]
            header.put(key, value)
        }

        // translate cells into grouped context items
        result.contexts = []
        groupedByContext.values().each { Collection<Cell> cells ->
            ExperimentContext context = new ExperimentContext()
            for(cell in cells) {
                ExperimentContextItem item = new ExperimentContextItem(attributeElement: cell.attributeElement,
                        experimentContext: context,
                        valueElement: cell.element,
                        valueNum: cell.value,
                        valueMin: cell.minValue,
                        valueMax: cell.maxValue,
                        qualifier: cell.qualifier)
                context.experimentContextItems.add(item)
            }
            result.contexts.add(context)
        }

        return result
    }

    void foo() {
        template.constantItems.each {nameToColumn[it.name] = it}
        template.columns.each { if(it.item != null) { nameToColumn[it.name] = it } }
        constants.entrySet().each { Map.Entry entry ->
            if (entry.key == EXPERIMENT_ID_LABEL) {

            } else if (entry.key == EXPERIMENT_NAME_LABEL) {

            } else {
                Column column = nameToColumn[entry.key]
                if (column == null) {
                    errors.addError(0, 0, "Did not know how to handle \"${entry.key}\" in the experiment level items")
                } else {
                }
            }
        }
    }

    void forEachDataRow(LineReader reader, List<Column> columns, ImportSummary errors, Closure fn) {
        int expectedColumnCount = columns.size() + FIXED_COLUMNS.size();

        while(true) {
            String line = reader.readLine();
            if (line == null)
                break;

            List values = line.split(DELIMITER)

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

    Object[] safeParse(ImportSummary errors, List<String> values, int lineNumber, List<Closure> fns) {
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

    List<Column> parseTableHeader(LineReader reader, Template template, ImportSummary errors)      {
        String header = reader.readLine()
        def columnNames = header.split(DELIMITER)

        // validate the fixed columns are where they should be
        for(int i = 0;i<FIXED_COLUMNS.size();i++) {
            if (columnNames.size() < i || columnNames[i] != FIXED_COLUMNS[i]) {
                errors.addError(reader.lineNumber, i, "Expected "+FIXED_COLUMNS[i]+" in column header at position "+(i+1))
            }
        }

        if (errors.hasErrors())
            return null

        def byName = [:]
        def seenColumns = [] as Set
        template.columns.each { byName[it.name] = it }

        def columns = []
        for(int i=FIXED_COLUMNS.size();i<columnNames.length;i++) {
            def name = columnNames[i]

            if (seenColumns.contains(name))
            {
                errors.addError(reader.lineNumber, i, "Duplicated column name \"${name}\"")
                continue
            }
            seenColumns.add(name)

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

    boolean isLinked(Column measureColumn, Column itemColumn) {
        def key = new Tuple(measureColumn.measure, itemColumn.contextItem)
    }

    void associateItemToResults(List<Result> results, List<Column> columns, Cell cell, Closure isLinked) {
        assert columns.size() == results.size()

        for(int i=0;i<results.size();i++) {
            Column column = columns.get(i)
            Result result = results.get(i)

            if (isLinked(column.measure, cell.column.item)) {
                ResultContextItem item = new ResultContextItem(result: result, attributeElement: cell.column.item.attributeElement, valueNum: cell.value, qualifier: cell.qualifier, valueMin: cell.minValue, valueMax: cell.maxValue, valueElement: cell.element)
                result.resultContextItems.add(item)
            }
        }
    }

    Collection<Result> createResults(InitialParse parse, ImportSummary errors, Map<AssayContextItem, Collection<Measure>> measuresPerContextItem) {
        def rowByNumber = [:]
        parse.rows.each {
            rowByNumber[it.rowNumber] = it
        }

        def resultsByRowNumber = [:]
        def resultByCell = [:]
        def cellByResult = [:]

        // construct all the Result objects (one per cell belonging to a measure)
        for(row in parse.rows) {
            def substance = Substance.get(row.sid)
            if(substance == null) {
                errors.addError(row.lineNumber, 0, "Could not find substance with id ${row.sid}")
                continue
            }

            def results = []
            for(cell in row.cells) {
                if (cell.column.measure != null) {
                    String qualifier = cell.qualifier.length() == 1 ? cell.qualifier +" " : cell.qualifier;
                    def result = new Result(qualifier: qualifier, valueNum: cell.value, statsModifier: cell.column.measure.statsModifier, resultType: cell.column.measure.resultType, replicateNumber: row.replicate, substance: substance, dateCreated: new Date(), resultStatus: "Pending")
                    results << result
                    resultByCell[cell] = result
                    cellByResult[result] = cell
                }
            }
            resultsByRowNumber[row.rowNumber] = results
        }

        // create the parent/child links between measures
        for(row in parse.rows) {
            if (row.parentRowNumber == null)
                continue

            def results = []
            ResultsService.Row parentRow = rowByNumber[row.parentRowNumber]
            if (parentRow == null) {
                errors.addError(row.lineNumber, 0, "Could not find row ${row.parentRowNumber} but this row ${row.rowNumber} is a child")
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
                if (cell.column.item != null) {
                    associateItemToResults(results, results.collect {cellByResult[it].column},  cell, isLinked);
                }
            }
        }

        return resultByCell.values()
    }

    InitialParse initialParse(Reader input, ImportSummary errors, Template template) {
        LineReader reader = new LineReader(reader: new BufferedReader(input))

        // first section
        List potentialExperimentColumns = []
        potentialExperimentColumns.addAll(template.constantItems)
        template.columns.each { if(it.item != null) { potentialExperimentColumns.add(it) } }
        InitialParse result = parseConstantRegion(reader, errors, potentialExperimentColumns)
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
        Set usedRowNumbers = [] as Set
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

            if (usedRowNumbers.contains(rowNumber)) {
                errors.addError(lineNumber, 0, "Row number ${rowNumber} was duplicated")
                return
            }
            usedRowNumbers.add(rowNumber)

            Row row = new Row (lineNumber: lineNumber, rowNumber: rowNumber, replicate: replicate, parentRowNumber: parentRowNumber, sid: sid)

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

        result.rows = rows

        return result
    }

    Map<AssayContextItem, Collection<Measure>> getItemsForMeasures(Assay assay) {
        Map result = [:]

        assay.assayContextItems.each {
            def collection
            if(result.containsKey(it)){
                collection = result[it]
            } else {
                collection = [] as Set
                result[it] = collection
            }
            it.assayContext.assayContextMeasures.each {
                collection.add(it.measure)
            }
        }

        return result
    }

    Map<AssayContextItem, Collection<Measure>> findRelationships(Experiment experiment) {
        def map = [:]

        experiment.assay.assayContextItems.each {
            def measures = it.assayContext.assayContextMeasures.collect { it.measure }
            map[it] = measures
        }

        return map
    }

    ImportSummary importResults(Experiment experiment, InputStream input) {
        ImportSummary errors = new ImportSummary()

        Template template = generateMaxSchema(experiment)
        def measuresPerContextItem = findRelationships(experiment)

        def parsed = initialParse(new InputStreamReader(input), errors, template)
        if (parsed != null) {
            def missingSids = pugService.validateSubstanceIds( parsed.rows.collect {it.sid} )
            missingSids.each { errors.addError(0,0, "Could not find substance with id ${it}")}

            def results = createResults(parsed, errors, measuresPerContextItem)

            // and persist these results to the DB
            results.each {
                it.experiment = experiment
                if(!it.save()) {
                    throw new RuntimeException(it.errors.toString())
                }
            }

            parsed.contexts.each {
                it.experiment = experiment
                if(!it.save()) {
                    throw new RuntimeException(it.errors.toString())
                }
            }

            errors.resultsCreated = results.size()
        }

        return errors
    }
}
