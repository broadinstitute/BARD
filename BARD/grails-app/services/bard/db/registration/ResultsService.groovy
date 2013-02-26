package bard.db.registration

import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.HierarchyType
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy
import bard.db.experiment.Substance

import java.util.regex.Matcher
import java.util.regex.Pattern

class ResultsService {

    // the number of lines to show the user after upload completes
    static int LINES_TO_SHOW_USER = 10;

    static String NUMBER_PATTERN_STRING = "[+-]?[0-9]+(\\.[0-9]*)?([Ee][+-]?[0-9]+)?"

    // pattern matching a number
    static Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_PATTERN_STRING)

    static String QUALIFIER_PATTERN_STRING = (Result.QUALIFIER_VALUES.collect{ "(?:${it.trim()})"}).join("|")

    // pattern matching a qualifier followed by a number
    static Pattern QUALIFIED_NUMBER_PATTERN = Pattern.compile("(${QUALIFIER_PATTERN_STRING})?\\s*(${NUMBER_PATTERN_STRING})")

    // pattern matching a range of numbers.  Doesn't actually check that the two parts are numbers
    static Pattern RANGE_PATTERN = Pattern.compile("([^-]+)-(.*)")

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
            return new Cell(value: closestValue, qualifier: "= ", column: column)
        } else {
            def labelMap = [:]
            contextItems.each {
                if(it.valueDisplay != null)
                    labelMap[it.valueDisplay.trim()] = it
            }
            AssayContextItem selectedItem = labelMap[value.trim()]
            if (selectedItem == null) {
                return "Could not find \"${value}\" among values in list: ${labelMap.keySet()}"
            }
            return new Cell(element: selectedItem.valueElement, valueDisplay: selectedItem.valueDisplay, column: column)
        }
    }

    static def makeItemParser(ItemService.Item item) {
        return { Column column, String value ->
            if (item.type == AttributeType.List) {
                return parseListValue(column, value, item.contextItems)
            } else if (item.type == AttributeType.Free) {
                return parseAnything(column, value)
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
                foundQualifier = "= "
            }

            if (foundQualifier.length() < 2) {
                foundQualifier += " "
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

    static def parseAnything(Column column, String value) {
        if (RANGE_PATTERN.matcher(value).matches()) {
            return parseRange(column, value)
        } else if(QUALIFIED_NUMBER_PATTERN.matcher(value).matches()) {
            return parseQualifiedNumber(column, value)
        } else {
            // assume it's free text and we take it literally
            Cell cell = new Cell(valueDisplay:value, column: column)
            return cell
        }
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
            this.parser = { Column column, String value -> parseAnything(column, value) }
        }

        public Column(String name, ItemService.Item item) {
            this.item = item;
            this.name = name;
            this.parser = makeItemParser(item)
        }

        public String toString() {
            return "${name}"
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

        String valueDisplay;

        public Element getAttributeElement() {
            return column.item.attributeElement;
        }

        String getValueDisplay() {
            if (valueDisplay != null) {
                return valueDisplay;
            }

            if (value != null) {
                if (qualifier == "= ") {
                    return value.toString()
                } else {
                    return "${qualifier.trim()}${value}"
                }
            }

            if (minValue != null) {
                return "${minValue}-${maxValue}";
            }

            if (element != null) {
                return element.label
            }

            return null;
        }

        String toString() {
            "Cell(${column})"
        }
    }

    static class ImportSummary {
        def errors = []

        // these are just collected for purposes of reporting the import summary at the end
        int linesParsed = 0;
        int resultsCreated = 0;
        int experimentAnnotationsCreated = 0;
        Map<String, Integer> resultsPerLabel = [:]
        Set<Long> substanceIds = [] as Set

        int resultsWithRelationships = 0;
        int resultAnnotations = 0;

        List<List> topLines = []

        public int getSubstanceCount() {
            return substanceIds.size()
        }

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
        int linesParsed;
        List<Row> rows;
        List<List<String>> topLines;
    }

    static class LineReader {
        CSVReader reader;
        int lineNumber = 0;

        List<List<String>> topLines = []

        String [] readLine() {
            lineNumber ++;
            String [] line = reader.readNext()

            if (line != null && topLines.size() < LINES_TO_SHOW_USER)
                topLines.add(line)

            return line;
        }

        public LineReader(BufferedReader reader) {
            this.reader = new CSVReader(reader);
        }
    }

    boolean allEmptyColumns(String[] columns) {
        for(column in columns) {
            if (!column.isEmpty())
                return false
        }
        return true
    }

    InitialParse parseConstantRegion(LineReader reader, ImportSummary errors, List<Column> experimentItemDefs) {
        Map<String,Column> nameToColumn = [:]
        Map<AssayContext,Collection<Cell>> groupedByContext = [:]
        Map header = [:]
        InitialParse result = new InitialParse()

        // populate map so we can look up columns by name
        experimentItemDefs.each {nameToColumn[it.name] = it}

        while(true) {
            String[] values = reader.readLine();
            if (values == null)
                break;

            // initial header stops on first empty line
            if (allEmptyColumns(values)) {
                break;
            }

            for(int i=3;i<values.length;i++) {
                if (!values[i].isEmpty()) {
                    errors.addError(reader.lineNumber, values.length, "Wrong number of columns in initial header.  Expected 3 but found value in column ${values[i]}")
                }
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

    void forEachDataRow(LineReader reader, List<Column> columns, ImportSummary errors, Closure fn) {
        int expectedColumnCount = columns.size() + FIXED_COLUMNS.size();

        while(true) {
            List<String> values = reader.readLine();
            if (values == null)
                break;

            // verify and reshape columns
            while(values.size() < expectedColumnCount) {
                values.add("")
            }

            // verify there aren't too many columns
            while(values.size() > expectedColumnCount) {
                String value = values.remove(values.size()-1)
                if (value.trim().length() != 0) {
                    errors.addError(reader.lineNumber, values.size()+1, "Found \"${value}\" in extra column")
                }
            }

            // now that values is guaranteed to be the right length, make the entire row isn't empty
            boolean allEmpty = true;
            for(cell in values) {
                if (!cell.isEmpty()) {
                    allEmpty = false;
                    break;
                }
            }

            // pass to the callback
            if (!allEmpty)
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
//                ex.printStackTrace()
            }
        }

        if (hadFailure) {
            return null;
        } else {
            return parsed;
        }
    }

    List<Column> parseTableHeader(LineReader reader, Template template, ImportSummary errors)      {
        List<String> columnNames = reader.readLine()

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
        for(int i=FIXED_COLUMNS.size();i<columnNames.size();i++) {
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

    void associateItemToResults(List<Result> results, List<Column> columns, Cell cell, Closure isLinked) {
        assert columns.size() == results.size()

        for(int i=0;i<results.size();i++) {
            Column column = columns.get(i)
            Result result = results.get(i)

            boolean linked = isLinked(column.measure, cell.column.item)
            if (linked) {
                ResultContextItem item = new ResultContextItem(result: result,
                        attributeElement: cell.column.item.attributeElement,
                        valueNum: cell.value,
                        qualifier: cell.qualifier,
                        valueMin: cell.minValue,
                        valueMax: cell.maxValue,
                        valueElement: cell.element,
                        valueDisplay: cell.valueDisplay)
                result.resultContextItems.add(item)
            }
        }
    }

    Map<Number, Collection<Number>> constructChildMap(Collection<Row> rows) {
        Map map = [:]
        for(row in rows) {
            if (row.parentRowNumber != null) {
                List<Number> childrenIds = map[row.parentRowNumber]

                if (childrenIds == null) {
                    childrenIds = new ArrayList();
                    map[row.parentRowNumber] = childrenIds
                }
                childrenIds.add(row.rowNumber)
            }
        }

        return map
    }


    Collection<Result> createResults(InitialParse parse, ImportSummary errors, Map<ItemService.Item, Collection<Measure>> measuresPerItem, Collection<ExperimentMeasure> experimentMeasures) {
        def rowByNumber = [:]
        parse.rows.each {
            rowByNumber[it.rowNumber] = it
        }

        def resultsByRowNumber = [:]
        def resultByCell = new IdentityHashMap()
        def cellByResult = new IdentityHashMap()

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
                    def result = new Result(qualifier: cell.qualifier,
                            valueDisplay: cell.valueDisplay,
                            valueNum: cell.value,
                            valueMin: cell.minValue,
                            valueMax: cell.maxValue,
                            statsModifier: cell.column.measure.statsModifier,
                            resultType: cell.column.measure.resultType,
                            replicateNumber: row.replicate,
                            substance: substance,
                            dateCreated: new Date(),
                            resultStatus: "Pending")
                    results << result
                    resultByCell[cell] = result
                    cellByResult[result] = cell
                }
            }
            resultsByRowNumber[row.rowNumber] = results
        }

        // validate all parent rows exist
        for(row in parse.rows) {
            if (row.parentRowNumber != null && !resultsByRowNumber.containsKey(row.parentRowNumber)) {
                errors.addError(row.lineNumber, 0, "Could not find row ${row.parentRowNumber} but this row ${row.rowNumber} is a child")
            }
        }

        // create the parent/child links between measures
        createResultHierarchy(parse, rowByNumber, experimentMeasures, errors, resultByCell)

        // and finally create the context items
        def isLinked = { measure, item ->
            if(measuresPerItem.containsKey(item)) {
                return measuresPerItem[item].contains(measure)
            }
            return false;
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

    private void createResultHierarchy(InitialParse parse, Map rowByNumber, Collection<ExperimentMeasure> experimentMeasures, ImportSummary errors, Map resultByCell) {
        Map<Number, Collection<Number>> parentToChildRows = constructChildMap(parse.rows)
        for (row in parse.rows) {
            // find all the cells which might have a parent-child relationship either due to being on the same
            // row, or due to the rows being linked by parent id
            List<Cell> possiblyRelatedCells = new ArrayList(row.cells)
            Collection<Number> childIds = parentToChildRows[row.rowNumber]
            if (childIds != null) {
                for (childId in childIds) {
                    possiblyRelatedCells.addAll(rowByNumber[childId].cells)
                }
            }

            // group the cells by measure
            Map<Measure, Collection<Cell>> cellsByMeasure = possiblyRelatedCells.groupBy { it.column.measure }

            for (experimentMeasure in experimentMeasures) {
                if (experimentMeasure.parent != null) {
                    Collection<Cell> parentCells = cellsByMeasure[experimentMeasure.parent.measure];
                    Collection<Cell> childCells = cellsByMeasure[experimentMeasure.measure];

                    if (parentCells != null && childCells != null) {
                        if (parentCells.size() > 1) {
                            errors.addError(row.lineNumber, 0, "Parent child relationship between ${parentCells} and ${childCells} is ambiguous.  There are multiple possible ways to assign relationships between these cells")
                        } else if (parentCells.size() == 1) {
                            Cell parentCell = parentCells.first();
                            Result parentResult = resultByCell[parentCell];

                            if (parentResult == null) {
                                throw new RuntimeException("Could not find result that came from parent ${parentCell}")
                            }

                            for (childCell in childCells) {
                                Result childResult = resultByCell[childCell];

                                if (childResult == null) {
                                    throw new RuntimeException("Could not find result that came from ${childCell}")
                                }

                                linkResults(experimentMeasure.parentChildRelationship, errors, row.lineNumber, childResult, parentResult)
                            }
                        }
                    }
                }
            }
        }
    }

    private void linkResults(relationship, ImportSummary errors, int lineNumber, Result childResult, Result parentResult) {
        HierarchyType hierarchyType = HierarchyType.getByValue(relationship);
        if (hierarchyType == null) {
            // hack until values are consistent in database
            if (relationship == "has Child") {
                hierarchyType = HierarchyType.Child;
            } else if (relationship == "Derived from") {
                hierarchyType = HierarchyType.Derives;
            } else {
                errors.addError(lineNumber, 0, "Experiment measures had the relationship ${relationship} which was unrecognized");
                return;
            }
        }

        ResultHierarchy resultHierarchy = new ResultHierarchy(hierarchyType: hierarchyType, result: childResult, parentResult: parentResult, dateCreated: new Date())
        childResult.resultHierarchiesForResult.add(resultHierarchy)
        parentResult.resultHierarchiesForParentResult.add(resultHierarchy)
    }

    InitialParse initialParse(Reader input, ImportSummary errors, Template template) {
        LineReader reader = new LineReader(new BufferedReader(input))

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

            if (sid <= 0) {
                errors.addError(lineNumber, 0, "Invalid substance id ${sid}")
                return
            }

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
        result.linesParsed = reader.lineNumber
        result.topLines = reader.topLines

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

    Map<ItemService.Item, Collection<Measure>> findRelationships(Experiment experiment) {
        def map = [:]

        itemService.getLogicalItems(experiment.assay.assayContextItems).each {
            def measures = it.assayContext.assayContextMeasures.collect { it.measure }
            map[it] = measures
        }

        return map
    }

    ImportSummary importResults(Experiment experiment, InputStream input) {
        ImportSummary errors = new ImportSummary()

        Template template = generateMaxSchema(experiment)
        def measuresPerItem = findRelationships(experiment)

        def parsed = initialParse(new InputStreamReader(input), errors, template)
        if (parsed != null && !errors.hasErrors()) {
            errors.linesParsed = parsed.linesParsed

            // populate the top few lines in the summary.
            errors.topLines = parsed.topLines

            def missingSids = pugService.validateSubstanceIds( parsed.rows.collect {it.sid} )

            missingSids.each {
                errors.addError(0, 0, "Could not find substance with id ${it}")
            }

            if (!errors.hasErrors())
            {
                def results = createResults(parsed, errors, measuresPerItem, experiment.experimentMeasures)

                if (!errors.hasErrors()) {
                    // and persist these results to the DB
                    Collection<ExperimentContext>contexts = parsed.contexts;

                    persist(experiment, results, errors, contexts)
                }
            }
        }

        return errors
    }

    private void persist(Experiment experiment, Collection<Result> results, ImportSummary errors, List<ExperimentContext> contexts) {
        deleteExperimentResults(experiment)

//        def relationships = [] as Set

        results.each {
            // get an id assigned before adding to set
            def t0 = new ArrayList(it.resultHierarchiesForParentResult)
            def t1 = new ArrayList(it.resultHierarchiesForResult)

//            it.resultHierarchiesForParentResult = [] as Set
//            it.resultHierarchiesForResult = [] as Set
            it.save(validate: false)
//            it.resultHierarchiesForParentResult = new LinkedHashSet(t0)
//            it.resultHierarchiesForResult = new LinkedHashSet(t1)
//            relationships.addAll(t0)
//            relationships.addAll(t1)

            assert it.id != null
            it.experiment = experiment
            experiment.addToResults(it)

            String label = it.displayLabel
            Integer count = errors.resultsPerLabel.get(label)
            if (count == null) {
                count = 0
            }
            errors.resultsPerLabel.put(label, count + 1)

            errors.substanceIds.add(it.substance.id)

            if (it.resultHierarchiesForParentResult.size() > 0 || it.resultHierarchiesForResult.size() > 0)
                errors.resultsWithRelationships ++;

            errors.resultAnnotations += it.resultContextItems.size()
        }

        // have to do this because relationships were removed above before save was called, so were unable to cascade
//        relationships.each {
//            it.save(validate: false)
//            assert it.id != null
//        }

        contexts.each {
            it.experiment = experiment
            experiment.addToExperimentContexts(it)

            errors.experimentAnnotationsCreated += it.contextItems.size()
        }

        errors.resultsCreated = results.size()
    }

    /* removes all data that gets populated via upload of results.  (That is, bard.db.experiment.ExperimentContextItem, bard.db.experiment.ExperimentContext, Result and bard.db.experiment.ResultContextItem */
    public void deleteExperimentResults(Experiment experiment) {
        // this is probably ridiculously slow, but my preference would be allow DB constraints to cascade the deletes, but that isn't in place.  So
        // walk the tree and delete all the objects.

        new ArrayList(experiment.experimentContexts).each { context ->
            new ArrayList(context.experimentContextItems).each { item ->
                context.removeFromExperimentContextItems(item)
                item.delete()
            }
            experiment.removeFromExperimentContexts(context)
            context.delete()
        }

        new ArrayList(experiment.results).each { result ->
            new ArrayList(result.resultContextItems).each { item ->
                result.removeFromResultContextItems(item)
                item.delete()
            }
            experiment.removeFromResults(result)
        }
    }
}
