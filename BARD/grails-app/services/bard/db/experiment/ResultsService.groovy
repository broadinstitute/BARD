package bard.db.experiment

import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.results.*
import bard.db.registration.*
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AttributeType
import bard.db.registration.ItemService
import bard.db.registration.PugService
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
        Closure statusCallback = { msg -> return }
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

        return [itemService.getLogicalItems(assayItems), experiment.experimentMeasures as List, itemService.getLogicalItems(measureItems)]
    }

    Template generateMaxSchema(Experiment experiment) {
        def (experimentItems, measures, measureItems) = generateMaxSchemaComponents(experiment)
        return generateSchema(experiment, experimentItems, measures, measureItems)
    }

    /**
     * Construct list of columns that a result upload could possibly contain
     */
    Template generateSchema(Experiment experiment, List<ItemService.Item> constantItems, List<ExperimentMeasure> measures, List<ItemService.Item> measureItems) {
        Set<String> constants = [] as Set
        Set<String> columns = [] as Set

        // add all the non-fixed context items
        for (item in constantItems) {
            String name = item.attributeElement.label
            constants.add(name)
        }

        // add all of the measurements
        for (measure in measures) {
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

    static class LineReader {
        CSVReader reader;
        int lineNumber = 0;

        List<List<String>> topLines = []

        String[] readLine() {
            lineNumber++;
            String[] line = reader.readNext()

            if (line != null && topLines.size() < LINES_TO_SHOW_USER)
                topLines.add(line as List)

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

    RowParser parseConstantRegion(LineReader reader, ImportSummary errors, Collection<ItemService.Item> constantItems, boolean skipExperimentContextItems) {
        RowParser result = new RowParser()
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

    Collection<Result> extractResultFromEachRow(ExperimentMeasure measure, Collection<Row> rows, Map<Integer, Collection<Row>> byParent, IdentityHashMap<RawCell, Row> unused, ImportSummary errors, Map<ExperimentMeasure, ItemService.Item> itemsByMeasure) {
        List<Result> results = []

        String label = measure.displayLabel

        for (row in rows) {
            // change this to a call to find
            RawCell cell = row.find(label)

            if (cell != null) {
                // mark this cell as having been consumed
                unused.remove(cell)
                String cellValue = cell.value

                Result result = createResult(row.replicate, measure, cellValue, row.sid, errors)
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
                for (child in measure.childMeasures) {
                    Collection<Result> resultChildren = extractResultFromEachRow(child, possibleChildRows, byParent, unused, errors, itemsByMeasure)

                    for (childResult in resultChildren) {
                        linkResults(child.parentChildRelationship, errors, 0, childResult, result);
                    }
                }

                // likewise create each of the context items associated with this measure
                results.add(result);
                for (item in itemsByMeasure[measure]) {
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

    Result createResult(Integer replicate, ExperimentMeasure measure, String valueString, Long substanceId, ImportSummary errors) {
        def parsed = parseAnything(valueString)

        if (parsed instanceof Cell) {
            Cell cell = parsed
            Element unit = measure.resultType.unit;

            Result result = new Result()
            result.qualifier = cell.qualifier
            result.valueDisplay = cell.valueDisplay + ((unit == null || cell.valueDisplay == "NA") ? "" : " ${unit.abbreviation}")
            result.valueNum = cell.value
            result.valueMin = cell.minValue
            result.valueMax = cell.maxValue
            result.statsModifier = measure.statsModifier
            result.resultType = measure.resultType
            result.replicateNumber = replicate
            result.substanceId = substanceId
            result.dateCreated = new Date()
            result.resultStatus = "Pending"
            result.measure = measure
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

    RowParser initialParse(Reader input, ImportSummary errors, Template template, boolean skipExperimentContextItems) {
        LineReader reader = new LineReader(new BufferedReader(input))

        // first section
        List potentialExperimentColumns = itemService.getLogicalItems(template.experiment.assay.assayContexts.collectMany { AssayContext context ->
            context.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        })

        RowParser result = parseConstantRegion(reader, errors, potentialExperimentColumns, skipExperimentContextItems)
        if (errors.hasErrors())
            return

        // main header
        List<String> columns = parseTableHeader(reader, template, errors)
        if (errors.hasErrors())
            return

//        result.linesParsed = reader.lineNumber
//        result.topLines = reader.topLines
        result.setup(reader, columns, errors)

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
            [em,
                    em.assayContextExperimentMeasures.collectMany { AssayContextExperimentMeasure acm ->
                        itemService.getLogicalItems(acm.assayContext.contextItems)
                    }]
        }

        return itemsByMeasure
    }

    static class ParseTimer {
        long prevUpdate;
        int count;
        Closure statusCallback = statusCallback

        public ParseTimer(Closure statusCallback) {
            prevUpdate = System.currentTimeMillis()
            int count = 0;
        }

        public void updateCount(int newCount) {
            long now = System.currentTimeMillis()

            if(now - prevUpdate > (10*1000)) {
                int delta = newCount - count
                float speed = ((float)delta)/((now-prevUpdate)/1000.0);
                count = newCount
                prevUpdate = now;

                statusCallback("Parsing lines per second: ${speed} (Current line: ${newCount})")
            }
        }
    }

    ImportSummary importResultsWithoutSavingOriginal(Experiment experiment, InputStream input, String originalFilename, String exportFilename, ImportOptions options) {
        ImportSummary errors = new ImportSummary()

        Template template = generateMaxSchema(experiment)
        Map<ExperimentMeasure, Collection<ItemService.Item>> itemsByMeasure = constructItemsByMeasure(experiment)

        RowParser parser = initialParse(new InputStreamReader(input), errors, template, options.skipExperimentContexts)

        if (parser != null && !errors.hasErrors()) {

            Collection<ExperimentContext> contexts = parser.contexts;
            ResultPersister persister = new ResultPersister(errors, options, experiment, originalFilename, exportFilename, contexts)
            persister.start()

            ParseTimer timer = new ParseTimer(options.statusCallback);

            while(true) {
                List<Row> rows = parser.readNextSampleRows();
                errors.linesParsed = parser.reader.lineNumber
                timer.updateCount(errors.linesParsed)

                // populate the top few lines in the summary.
                errors.topLines = parser.reader.topLines
                errors.substanceCount = parser.sampleIds.size()

                if(rows == null) {
                    break;
                }

                if(errors.hasErrors()) {
                    break;
                }

                Collection<Result> resultsForSample = createResults(rows, experiment.experimentMeasures, errors, itemsByMeasure)

                if (!errors.hasErrors()) {
                    checkForDuplicates(errors, resultsForSample)
                }

                if (!errors.hasErrors()) {
                    persister.addResultsForSample(resultsForSample)
                }
            }

            if (!errors.hasErrors() && errors.resultsCreated == 0) {
                errors.addError(0, 0, "No results were produced")
            }

            if (!errors.hasErrors()) {
                persister.finish()
            } else {
                persister.abort()
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
        Map<Result, LogicalKey> resultToKey = [:]

        for (result in results) {
            LogicalKey key = constructKey(result)
            resultToKey[result] = key
        }

        // connect parents
        for (result in results) {
            LogicalKey childKey = resultToKey[result]
            assert childKey != null
            result.resultHierarchiesForResult.each {
                LogicalKey parentKey = resultToKey[it.parentResult]
                assert parentKey != null
                childKey.parentKey = parentKey
            }
        }

        Set<LogicalKey> seen = new HashSet()

        for (key in resultToKey.values()) {
            boolean added = seen.add(key)
            if (!added) {
                errors.addError(0, 0, "Found duplicate: ${key}")
            }
        }
    }

    class ResultPersister {
        String originalFilename
        String exportFilename
        ImportSummary summary;
        ImportOptions options
        Experiment experiment
        Collection<ExperimentContext> contexts;
        Writer writer

        public ResultPersister(ImportSummary summary, ImportOptions options, Experiment experiment, String originalFilename, String exportFilename, Collection<ExperimentContext> contexts) {
            this.summary = summary
            this.options = options
            this.experiment = experiment
            this.originalFilename = originalFilename
            this.exportFilename = exportFilename
            this.contexts = contexts;
        }

        public void start() {
            deleteExperimentResults(experiment, options.skipExperimentContexts)

            if(!options.skipExperimentContexts) {
                contexts.each {
                    it.experiment = experiment
                    experiment.addToExperimentContexts(it)

                    summary.experimentAnnotationsCreated += it.contextItems.size()
                }
            }

            this.writer = resultsExportService.createWriter(exportFilename)
        }

        public void addResultsForSample(Collection<Result> results) {
            results.each {
                String label = it.displayLabel
                Integer count = summary.resultsPerLabel.get(label)
                if (count == null) {
                    count = 0
                }
                summary.resultsPerLabel.put(label, count + 1)

                if (it.resultHierarchiesForParentResult.size() > 0 || it.resultHierarchiesForResult.size() > 0)
                    summary.resultsWithRelationships++;

                summary.resultAnnotations += it.resultContextItems.size()
            }

            summary.resultsCreated += results.size()

            if (options.writeResultsToDb)
                bulkResultService.insertResults(getUsername(), experiment, results)

            Set<Long> sids = new HashSet(results.collect {it.substanceId} )
            assert sids.size() == 1

            resultsExportService.writeResultsForSubstance(writer, sids.first(), results as List)
        }

        public void finish() {
            writer.close()
            addExperimentFileToDb(experiment, originalFilename, exportFilename, summary.substanceCount)
        }

        public void abort() {
            writer.close()
        }
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
