package bard.db.experiment

import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.results.*
import bard.db.registration.*
import bard.hibernate.AuthenticatedUserRequired
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.io.IOUtils
import org.springframework.security.access.prepost.PreAuthorize

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class ResultsService {

    // the number of lines to show the user after upload completes
    static int LINES_TO_SHOW_USER = 20;

    static String NUMBER_PATTERN_STRING = "[+-]?[0-9]+(\\.[0-9]*)?([Ee][+-]?[0-9]+)?"

    // pattern matching a number
    static Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_PATTERN_STRING)

    static String QUALIFIER_PATTERN_STRING = (Result.QUALIFIER_VALUES.collect { "(?:${it.trim()})" }).join("|")

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
    ResultsExportService resultsExportService
    ArchivePathService archivePathService
    BulkResultService bulkResultService
    SpringSecurityService springSecurityService;

    public static class ImportOptions {
        boolean validateSubstances = true;
        boolean writeResultsToDb = true;
        boolean skipExperimentContexts = false;
    }

    static boolean isNumber(value) {
        return NUMBER_PATTERN.matcher(value).matches()
    }

    static def parseListValue(String value, List<AssayContextItem> contextItems) {
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
            return new Cell(value: closestValue, qualifier: "= ", valueDisplay: closestValue.toString())
        } else {
            def labelMap = [:]
            contextItems.each {
                if (it.valueDisplay != null)
                    labelMap[it.valueDisplay.trim()] = it
            }
            AssayContextItem selectedItem = labelMap[value.trim()]
            if (selectedItem == null) {
                return "Could not find \"${value}\" among values in list: ${labelMap.keySet()}"
            }
            return new Cell(element: selectedItem.valueElement, valueDisplay: selectedItem.valueDisplay)
        }
    }

    static def parseQualifiedNumber(String value) {
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
            try {
                a = Float.parseFloat(matcher.group(2));
            }
            catch (NumberFormatException e) {
                return "Could not parse \"${matcher.group(2)}\" as a number"
            }

            String valueDisplay = a.toString()
            if (foundQualifier != "= ") {
                valueDisplay = foundQualifier.trim() + valueDisplay
            }

            Cell cell = new Cell(value: a, qualifier: foundQualifier, valueDisplay: valueDisplay)

            return cell
        } else {
            return "Could not parse \"${value}\" as a number with optional qualifier"
        }
    }

    static def parseRange(String value) {
        def rangeMatch = RANGE_PATTERN.matcher(value)
        if (rangeMatch.matches()) {
            float minValue, maxValue
            try {
                minValue = Float.parseFloat(rangeMatch.group(1));
                maxValue = Float.parseFloat(rangeMatch.group(2));
            }
            catch (NumberFormatException e) {
                // if we fail to parse it as a range, treat it as free text
                return new Cell(valueDisplay: value)
            }

            Cell cell = new Cell(minValue: minValue, maxValue: maxValue, valueDisplay: "${minValue}-${maxValue}")
            return cell
        }

        return "Expected a range, but got \"${value}\""
    }

    static def parseAnything(String value) {
        if (QUALIFIED_NUMBER_PATTERN.matcher(value).matches()) {
            return parseQualifiedNumber(value)
        } else if (RANGE_PATTERN.matcher(value).matches()) {
            return parseRange(value)
        } else {
            // assume it's free text and we take it literally
            return new Cell(valueDisplay: value)
        }
    }

    static def parseNumberOrRange(String value) {
        if (RANGE_PATTERN.matcher(value).matches()) {
            return parseRange(value)
        } else {
            return parseQualifiedNumber(value)
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
                    errors << "On line ${line}, column ${column + 1}: ${message}"
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
        List<String> constantItems;
        List<String> columns;

        List asTable() {
            def lines = []

            lines.add(["", EXPERIMENT_ID_LABEL, experiment.id])
            lines.add(["", EXPERIMENT_NAME_LABEL, experiment.experimentName])

            // add the fields for values that are constant across entire experiment
            constantItems.each { lines.add(["", it]) }
            lines.add([])

            // add the first line of the header
            def row = []
            row.addAll(FIXED_COLUMNS)
            columns.each { row.add(it) }
            lines.add(row)
            lines.add(["1"])

            return lines
        }
    }

    List generateMaxSchemaComponents(Experiment experiment) {
        def assay = experiment.assay

        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def measureItems = assayItems.findAll { it.assayContext.assayContextExperimentMeasures.size() > 0 }
        assayItems.removeAll(measureItems)

        return [itemService.getLogicalItems(assayItems), experiment.experimentMeasures.collect { it } as List, itemService.getLogicalItems(measureItems)]
    }

    Template generateMaxSchema(Experiment experiment) {
        def (experimentItems, measures, measureItems) = generateMaxSchemaComponents(experiment)
        return generateSchema(experiment, experimentItems, measures, measureItems)
    }

    /**
     * Construct list of columns that a result upload could possibly contain
     */
    Template generateSchema(Experiment experiment, List<ItemService.Item> constantItems, List<ExperimentMeasure> experimentMeasures, List<ItemService.Item> measureItems) {
        Set<String> constants = [] as Set
        Set<String> columns = [] as Set

        // add all the non-fixed context items
        for (item in constantItems) {
            String name = item.attributeElement.label
            constants.add(name)
        }

        // add all of the measurements
        for (measure in experimentMeasures) {
            String name = measure.displayLabel
            columns.add(name)
        }

        // add all the measure context items
        for (item in measureItems) {
            String name = item.attributeElement.label
            columns.add(name)
        }

        return new Template(experiment: experiment, constantItems: constants as List, columns: columns as List)
    }

    public static class InitialParse {
        String experimentName;
        Long experimentId;
        List<ExperimentContext> contexts = []
        int linesParsed;
        List<Row> rows;
        List<List<String>> topLines;
    }

    static class LineReader {
        CSVReader reader;
        int lineNumber = 0;

        List<List<String>> topLines = []

        String[] readLine() {
            lineNumber++;
            String[] line = reader.readNext()

            if (line != null && topLines.size() < LINES_TO_SHOW_USER)
                topLines.add(line)

            return line;
        }

        public LineReader(BufferedReader reader) {
            this.reader = new CSVReader(reader);
        }
    }

    boolean allEmptyColumns(String[] columns) {
        for (column in columns) {
            if (!column.isEmpty())
                return false
        }
        return true
    }


    InitialParse parseConstantRegion(LineReader reader, ImportSummary errors, Collection<ItemService.Item> constantItems, boolean skipExperimentContextItems) {
        InitialParse result = new InitialParse()
        Map experimentAnnotations = [:]

        while (true) {
            String[] values = reader.readLine();
            if (values == null)
                break;

            // initial header stops on first empty line
            if (allEmptyColumns(values)) {
                break;
            }

            for (int i = 3; i < values.length; i++) {
                if (!values[i].isEmpty()) {
                    errors.addError(reader.lineNumber, values.length, "Wrong number of columns in initial header.  Expected 3 but found value in column ${values[i]}")
                }
                continue
            }

            if (!values[0].isEmpty()) {
                errors.addError(reader.lineNumber, 0, "First column should be empty in the constant section at top of table")
                continue
            }

            if (values.length == 2 || values[2].isEmpty()) {
                // we have no value for the given key
                continue
            }

            String value = values[2]
            String key = values[1]
            if (key == EXPERIMENT_ID_LABEL) {
                result.experimentId = Long.parseLong(value)
            } else if (key == EXPERIMENT_NAME_LABEL) {
                result.experimentName = value
            } else {
                experimentAnnotations.put(key, value)
            }
        }

        if (!skipExperimentContextItems) {
            Set<String> unusedKeyNames = new HashSet(experimentAnnotations.keySet())

            // walk through all the context items on the assay
            List<ExperimentContext> experimentContexts = []
            for (entry in (constantItems.groupBy { it.assayContext }).entrySet()) {
                AssayContext assayContext = entry.key;
                Collection<ItemService.Item> values = entry.value;

                ExperimentContext context = new ExperimentContext()
                context.setContextName(assayContext.contextName)
                for (item in values) {
                    String label = item.displayLabel;

                    unusedKeyNames.remove(label);
                    String stringValue = experimentAnnotations.get(label)
                    if (stringValue == null || stringValue.isEmpty())
                        continue;

                    ExperimentContextItem experimentContextItem = createExperimentContextItem(item, stringValue, errors)
                    if (experimentContextItem != null) {
                        context.contextItems.add(experimentContextItem)
                        experimentContextItem.experimentContext = context
                    }
                }

                if (context.experimentContextItems.size() > 0)
                    result.contexts.add(context)
            }

            for (unusedKey in unusedKeyNames) {
                errors.addError(0, 0, "Unknown field \"${unusedKey}\" in header")
            }
        }

        return result
    }

    void forEachDataRow(LineReader reader, List<String> columns, ImportSummary errors, Closure fn) {
        int expectedColumnCount = columns.size() + FIXED_COLUMNS.size();

        while (true) {
            List<String> values = reader.readLine();
            if (values == null)
                break;

            // verify and reshape columns
            while (values.size() < expectedColumnCount) {
                values.add("")
            }

            // verify there aren't too many columns
            while (values.size() > expectedColumnCount) {
                String value = values.remove(values.size() - 1)
                if (value.trim().length() != 0) {
                    errors.addError(reader.lineNumber, values.size() + 1, "Found \"${value}\" in extra column")
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

    List<String> parseTableHeader(LineReader reader, Template template, ImportSummary errors) {
        List<String> columnNames = reader.readLine()

        // validate the fixed columns are where they should be
        for (int i = 0; i < FIXED_COLUMNS.size(); i++) {
            if (columnNames.size() < i || columnNames[i] != FIXED_COLUMNS[i]) {
                errors.addError(reader.lineNumber, i, "Expected " + FIXED_COLUMNS[i] + " in column header at position " + (i + 1))
            }
        }

        if (errors.hasErrors())
            return null

        def seenColumns = [] as Set

        def columns = []
        for (int i = FIXED_COLUMNS.size(); i < columnNames.size(); i++) {
            def name = columnNames[i]

            if (seenColumns.contains(name)) {
                errors.addError(reader.lineNumber, i, "Duplicated column name \"${name}\"")
                columns.add("")
                continue
            }

            seenColumns.add(name)

            if (!template.columns.contains(name)) {
                errors.addError(reader.lineNumber, i, "Invalid column name \"${name}\"")
                columns.add("")
                continue;
            }

            columns.add(name)
        }

        return columns
    }

    Collection<Result> createResults(List<Row> rows, Collection<ExperimentMeasure> experimentMeasures, ImportSummary errors, Map<ExperimentMeasure, Collection<ItemService.Item>> itemsByMeasure) {
        validateParentRowsExist(rows, errors);
        if (errors.hasErrors())
            return []

        Map<Integer, Collection<Row>> byParent = rows.groupBy { it.parentRowNumber }

        // create a set which we'll use to track which cells were used at least once.  Since we're driven by
        // walking the measure tree, it's possible that some cells might not get consumed.  Those should be
        // errors.
        IdentityHashMap<RawCell, Row> unused = new IdentityHashMap();
        for (row in rows) {
            for (cell in row.cells) {
                unused.put(cell, row)
            }
        }

        // we're going to walk through the measure tree, taking items from the results now
        // that they've also been mapped into a tree

        // start with the rows with no parents because these must contain the root measures
        Collection<ExperimentMeasure> rootMeasures = experimentMeasures.findAll { it.parent == null }
        List<Result> results = []
        for (measure in rootMeasures) {
            results.addAll(extractResultFromEachRow(measure, byParent.get(null), byParent, unused, errors, itemsByMeasure))
        }

        for (cell in unused.keySet()) {
            Row row = unused.get(cell);
            errors.addError(row.lineNumber, 0, "Didn't know what to do with the value on line ${row.lineNumber} in column ${cell.columnName}");
        }
        // flatten results to include the top level elements as well as all reachable children
        Set<Result> allResults = new HashSet()
        addAllResults(allResults, results)

        return allResults
    }

    private addAllResults(Collection<Result> all, Collection<Result> toAdd) {
        for (result in toAdd) {
            if (!all.contains(result)) {
                all.add(result)
                addAllResults(all, result.resultHierarchiesForParentResult.collect { it.result })
            }
        }
    }

    Collection<Result> extractResultFromEachRow(ExperimentMeasure experimentMeasure, Collection<Row> rows, Map<Integer, Collection<Row>> byParent, IdentityHashMap<RawCell, Row> unused, ImportSummary errors, Map<ExperimentMeasure, ItemService.Item> itemsByMeasure) {
        List<Result> results = []

        String label = experimentMeasure.displayLabel

        for (row in rows) {
            // change this to a call to find
            RawCell cell = row.find(label)

            if (cell != null) {
                // mark this cell as having been consumed
                unused.remove(cell)
                String cellValue = cell.value

                Result result = createResult(row.replicate, experimentMeasure, cellValue, row.sid, errors)
                if (result == null)
                    continue;

                // children can be on the same row or any row that has this row as its parent
                // so combine those two collections
                List<Row> possibleChildRows = [row]
                Collection<Row> childRows = byParent[row.rowNumber]
                if (childRows != null) {
                    possibleChildRows.addAll(childRows)
                }

                // for each child measure, create a result per row in each of the child rows
                for (child in experimentMeasure.childMeasures) {
                    Collection<Result> resultChildren = extractResultFromEachRow(child, possibleChildRows, byParent, unused, errors, itemsByMeasure)

                    for (childResult in resultChildren) {
                        linkResults(child.parentChildRelationship, errors, 0, childResult, result);
                    }
                }

                // likewise create each of the context items associated with this measure
                results.add(result);
                for (item in itemsByMeasure[experimentMeasure]) {
                    RawCell itemCell = row.find(item.displayLabel)
                    if (itemCell != null) {
                        unused.remove(itemCell)
                        ResultContextItem resultItem = createResultItem(itemCell.value, item, errors)

                        if (resultItem != null) {
                            resultItem.result = result
                            result.resultContextItems.add(resultItem)
                        }
                    }
                }
            }
        }

        return results;
    }

    void validateParentRowsExist(Collection<Row> rows, ImportSummary errors) {
        def rowByNumber = [:]
        for (row in rows) {
            rowByNumber[row.rowNumber] = row
        }

        for (row in rows) {
            if (row.parentRowNumber != null && !rowByNumber.containsKey(row.parentRowNumber)) {
                errors.addError(row.lineNumber, 0, "Could not find row ${row.parentRowNumber} but this row ${row.rowNumber} is a child")
            }
        }
    }

    def parseContextItem(String stringValue, ItemService.Item item) {
        if (item.type == AttributeType.List) {
            return parseListValue(stringValue, item.contextItems)
        } else if (item.type == AttributeType.Free) {
            return parseAnything(stringValue)
        } else if (item.type == AttributeType.Range) {
            Double rangeMin = item.contextItems[0].valueMin
            Double rangeMax = item.contextItems[0].valueMax
            String rangeName = item.attributeElement.label

            float floatValue = Float.parseFloat(stringValue)

            if (floatValue < rangeMin || floatValue > rangeMax) {
                return "The value \"${floatValue}\" outside of allowed range (${rangeMin} - ${rangeMax}) for ${rangeName}"
            }

            return new Cell(value: floatValue,
                    valueDisplay: floatValue.toString())
        } else {
            throw new RuntimeException("Did not know how to handle attribute ${item.displayLabel} of type ${item.type} (value: ${stringValue}) ")
        }
    }

    ResultContextItem createResultItem(String stringValue, ItemService.Item assayItem, ImportSummary errors) {
        ResultContextItem item = null
        Element attributeElement = assayItem.attributeElement
        final String externalURL = attributeElement.externalURL
        if (externalURL) {
            item = new ResultContextItem()
            item.attributeElement = attributeElement
            item.extValueId = stringValue//external value id
            item.valueDisplay = attributeElement.externalURL + stringValue//external value id
        } else {
            def parsed = parseContextItem(stringValue, assayItem)
            //Experiment measures had the relationship is calculated from which was unrecognized
            if (parsed instanceof Cell) {
                Cell cell = parsed
                item = new ResultContextItem()
                //
                item.attributeElement = assayItem.attributeElement
                item.valueNum = cell.value
                item.qualifier = cell.qualifier
                item.valueMin = cell.minValue
                item.valueMax = cell.maxValue
                item.valueElement = cell.element
                Element unit = assayItem.attributeElement.unit;
                item.valueDisplay = cell.valueDisplay + (unit == null || cell.valueDisplay == "NA" ? "" : " ${unit.abbreviation}")
            } else {
                errors.addError(0, 0, parsed)
            }
        }
        return item
    }

    Result createResult(Integer replicate, ExperimentMeasure experimentMeasure, String valueString, Long substanceId, ImportSummary errors) {
        def parsed = parseAnything(valueString)

        if (parsed instanceof Cell) {
            Cell cell = parsed
            Element unit = experimentMeasure.resultType.unit;

            Result result = new Result()
            result.qualifier = cell.qualifier
            result.valueDisplay = cell.valueDisplay + ((unit == null || cell.valueDisplay == "NA") ? "" : " ${unit.abbreviation}")
            result.valueNum = cell.value
            result.valueMin = cell.minValue
            result.valueMax = cell.maxValue
            result.statsModifier = experimentMeasure.statsModifier
            result.resultType = experimentMeasure.resultType
            result.replicateNumber = replicate
            result.substanceId = substanceId
            result.dateCreated = new Date()
            result.resultStatus = "Pending"
            result.experimentMeasure = experimentMeasure
            return result;
        } else {
            errors.addError(0, 0, parsed)
            return null;
        }
    }

    private ExperimentContextItem createExperimentContextItem(ItemService.Item assayItem, String stringValue, ImportSummary errors) {
        def parsed = parseContextItem(stringValue, assayItem)

        if (parsed instanceof Cell) {
            Cell cell = parsed

            Element unit = assayItem.attributeElement.unit;
            String valueDisplay = cell.valueDisplay + (unit == null || cell.valueDisplay == "NA" ? "" : " ${unit.abbreviation}")

            ExperimentContextItem item = new ExperimentContextItem(attributeElement: assayItem.attributeElement,
                    valueElement: cell.element,
                    valueNum: cell.value,
                    valueMin: cell.minValue,
                    valueMax: cell.maxValue,
                    qualifier: cell.qualifier,
                    valueDisplay: valueDisplay)
        } else {
            errors.addError(0, 0, parsed)
            return null;
        }
    }

    private void linkResults(HierarchyType hierarchyType, ImportSummary errors, int lineNumber, Result childResult, Result parentResult) {
        ResultHierarchy resultHierarchy = new ResultHierarchy()
        resultHierarchy.hierarchyType = hierarchyType
        resultHierarchy.result = childResult
        resultHierarchy.parentResult = parentResult
        resultHierarchy.dateCreated = new Date()
        childResult.resultHierarchiesForResult.add(resultHierarchy)
        parentResult.resultHierarchiesForParentResult.add(resultHierarchy)
    }

    InitialParse initialParse(Reader input, ImportSummary errors, Template template, boolean skipExperimentContextItems) {
        LineReader reader = new LineReader(new BufferedReader(input))

        // first section
        List potentialExperimentColumns = itemService.getLogicalItems(template.experiment.assay.assayContexts.collectMany { AssayContext context ->
            context.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        })

        InitialParse result = parseConstantRegion(reader, errors, potentialExperimentColumns, skipExperimentContextItems)
        if (errors.hasErrors())
            return

        // main header
        List<String> columns = parseTableHeader(reader, template, errors)
        if (errors.hasErrors())
            return

        def parseInt = { x -> Integer.parseInt(x) }
        def parseOptInt = { x ->
            if (x.trim().length() > 0) {
                return Integer.parseInt(x)
            }
        }
        def parseLong = { x -> Long.parseLong(x) }

        // all data rows
        List rows = []
        Set usedRowNumbers = [] as Set
        forEachDataRow(reader, columns, errors) { int lineNumber, List<String> values ->
            def parsed = safeParse(errors, values, lineNumber, [parseInt, parseLong, parseOptInt, parseOptInt])

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

            Row row = new Row(lineNumber: lineNumber, rowNumber: rowNumber, replicate: replicate, parentRowNumber: parentRowNumber, sid: sid)

            // parse the dynamic columns
            for (int i = 0; i < columns.size(); i++) {
                String cellString = values[i + FIXED_COLUMNS.size()];
                if (cellString.isEmpty())
                    continue

                String column = columns.get(i);
                row.cells.add(new RawCell(columnName: column, value: cellString));
            }

            rows.add(row)
        }

        result.rows = rows
        result.linesParsed = reader.lineNumber
        result.topLines = reader.topLines

        return result
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    ImportSummary importResults(Long id, InputStream input, ImportOptions options = null) {
        if (options == null) {
            options = new ImportOptions();
        }
        Experiment experiment = Experiment.findById(id)
        String originalFilename = archivePathService.constructUploadResultPath(experiment)
        String exportFilename = archivePathService.constructExportResultPath(experiment)
        File archivedFile = archivePathService.prepareForWriting(originalFilename)

        OutputStream output = new GZIPOutputStream(new FileOutputStream(archivedFile));
        IOUtils.copy(input, output);
        input.close()
        output.close()

        ImportSummary summary = importResultsWithoutSavingOriginal(experiment, new GZIPInputStream(new FileInputStream(archivedFile)), originalFilename, exportFilename, options);
        if (summary.hasErrors()) {
            archivedFile.delete()
        }

        return summary;
    }

    Map<ExperimentMeasure, Collection<ItemService.Item>> constructItemsByMeasure(Experiment experiment) {
        Map<ExperimentMeasure, Collection<ItemService.Item>> itemsByMeasure = experiment.experimentMeasures.collectEntries { ExperimentMeasure em ->
            [em, em.assayContextExperimentMeasures.collectMany { AssayContextExperimentMeasure acm ->
                        itemService.getLogicalItems(acm.assayContext.contextItems)
                    }]
        }

        return itemsByMeasure
    }

    ImportSummary importResultsWithoutSavingOriginal(Experiment experiment, InputStream input, String originalFilename, String exportFilename, ImportOptions options) {
        ImportSummary errors = new ImportSummary()

        Template template = generateMaxSchema(experiment)
        Map<ExperimentMeasure, Collection<ItemService.Item>> itemsByMeasure = constructItemsByMeasure(experiment)

        def parsed = initialParse(new InputStreamReader(input), errors, template, options.skipExperimentContexts)
        if (parsed != null && !errors.hasErrors()) {
            errors.linesParsed = parsed.linesParsed

            // populate the top few lines in the summary.
            errors.topLines = parsed.topLines

            def missingSids = []
            if (options.validateSubstances)
                missingSids = pugService.validateSubstanceIds(parsed.rows.collect { it.sid })

            missingSids.each {
                errors.addError(0, 0, "Could not find substance with id ${it}")
            }

            if (!errors.hasErrors()) {
                def results = createResults(parsed.rows, experiment.experimentMeasures, errors, itemsByMeasure)

                if (!errors.hasErrors() && results.size() == 0) {
                    errors.addError(0, 0, "No results were produced")
                }

                if (!errors.hasErrors()) {
                    checkForDuplicates(errors, results)
                }

                if (!errors.hasErrors()) {
                    // and persist these results to the DB
                    Collection<ExperimentContext> contexts = parsed.contexts;

                    persist(experiment, results, errors, contexts, originalFilename, exportFilename, options)
                }
            }
        }

        return errors
    }

    private LogicalKey constructKey(Result result) {
        LogicalKey key = new LogicalKey()

        key.replicateNumber = result.replicateNumber
        key.substanceId = result.substanceId

        key.resultType = result.resultType
        key.statsModifier = result.statsModifier
        key.valueNum = result.valueNum
        key.qualifier = result.qualifier
        key.valueMin = result.valueMin
        key.valueMax = result.valueMax
        key.valueDisplay = result.valueDisplay
        key.measureId = result.experimentMeasure.id

        key.items = result.resultContextItems.collect(new HashSet(), {
            LogicalKeyItem item = new LogicalKeyItem()
            item.attributeElement = it.attributeElement
            item.valueNum = it.valueNum
            item.qualifier = it.qualifier
            item.valueMin = it.valueMin
            item.valueMax = it.valueMax
            item.valueElement = it.valueElement
            item.valueDisplay = it.valueDisplay

            return item
        })

        return key
    }

    private void checkForDuplicates(ImportSummary errors, Collection<Result> results) {
        Set<LogicalKey> seen = new HashSet()

        for (result in results) {
            LogicalKey key = constructKey(result)
            boolean added = seen.add(key)
            if (!added) {
                errors.addError(0, 0, "Found duplicate: ${key}")
            }
        }
    }

    private void persist(Experiment experiment, Collection<Result> results, ImportSummary errors, List<ExperimentContext> contexts, String originalFilename, String exportFilename, ImportOptions options) {
        deleteExperimentResults(experiment, options.skipExperimentContexts)

        results.each {
            String label = it.displayLabel
            Integer count = errors.resultsPerLabel.get(label)
            if (count == null) {
                count = 0
            }
            errors.resultsPerLabel.put(label, count + 1)

            errors.substanceIds.add(it.substanceId)

            if (it.resultHierarchiesForParentResult.size() > 0 || it.resultHierarchiesForResult.size() > 0)
                errors.resultsWithRelationships++;

            errors.resultAnnotations += it.resultContextItems.size()
        }

        if (!options.skipExperimentContexts) {
            contexts.each {
                it.experiment = experiment
                experiment.addToExperimentContexts(it)

                errors.experimentAnnotationsCreated += it.contextItems.size()
            }
        }

        errors.resultsCreated = results.size()

        if (options.writeResultsToDb)
            bulkResultService.insertResults(getUsername(), experiment, results)

        resultsExportService.dumpFromList(exportFilename, results)

        addExperimentFileToDb(experiment, originalFilename, exportFilename, errors.substanceCount)
    }

    private addExperimentFileToDb(Experiment experiment, String originalFilename, String exportFilename, long substanceCount) {
        ExperimentFile file = new ExperimentFile(experiment: experiment, originalFile: originalFilename, exportFile: exportFilename, dateCreated: new Date(), submissionVersion: experiment.experimentFiles.size(), substanceCount: substanceCount)
        file.save(failOnError: true)
        experiment.experimentFiles.add(file)
    }

    private String getUsername() {
        String username = springSecurityService.getPrincipal()?.username
        if (!username) {
            throw new AuthenticatedUserRequired('An authenticated user was expected this point');
        }
        return username
    }

    /* removes all data that gets populated via upload of results.  (That is, bard.db.experiment.ExperimentContextItem, bard.db.experiment.ExperimentContext, Result and bard.db.experiment.ResultContextItem */

    public void deleteExperimentResults(Experiment experiment, boolean skipExperimentContextItems) {
        // this is probably ridiculously slow, but my preference would be allow DB constraints to cascade the deletes, but that isn't in place.  So
        // walk the tree and delete all the objects.

        if (!skipExperimentContextItems) {
            new ArrayList(experiment.experimentContexts).each { ExperimentContext context ->
                new ArrayList(context.experimentContextItems).each { item ->
                    context.removeFromExperimentContextItems(item)
                    item.delete()
                }
                experiment.removeFromExperimentContexts(context)
                context.delete()
            }
        }

        bulkResultService.deleteResults(experiment)
    }
}
