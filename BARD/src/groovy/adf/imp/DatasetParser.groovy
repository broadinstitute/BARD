package adf.imp

import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/7/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatasetParser {
    List<FileState> openFiles;

    public DatasetParser(List<String> filenames) {
        openFiles = filenames.collect {
            new FileState(DatasetImportTransform.sortCsvFile(it))
        }
    }

    Batch readNext() {
        Long nextSid = openFiles.collect { it.nextSid }.min()

        Batch batch = new Batch(sid: nextSid,
                datasets: openFiles.findAll { it.nextSid == nextSid }.collect {
                    it.readNextDataset()
                })

        return batch
    }

    boolean hasNext() {
        boolean reachedEnd = true;
        openFiles.each {
            if(!it.reachedEnd) {
                reachedEnd = false;
            }
        }
        return !reachedEnd
    }
}

class FileState {
    static final Pattern ELEMENT_WITH_MODIFIER = Pattern.compile("^(.*)\\s\\(([^)]+)\\)\$");

    List<String> columns;
    CSVReader reader;
    List<String> nextRow = null;
    Long nextSid;
    boolean reachedEnd = false;

    Integer parentResultIdIndex = -1;
    Integer resultIdIndex = -1;
    Integer replicateIndex = -1;

    List<DatasetColumn> resultTypes = [];
    List<DatasetColumn> contextItems = [];
    Map<String, Integer> columnByName = [:]

    public FileState(CSVReader reader) {
        this.reader = reader
        columns = reader.readNext() as List
        columns.eachWithIndex { String entry, int i -> columnByName[entry] = i }

        parentResultIdIndex = columnByName["parentResultId"]
        resultIdIndex = columnByName["resultId"]
        replicateIndex = columnByName["replicateIndex"]

        for(int i=0;i<columns.size();i++) {
            // skip the special columns.  All others should be result type or context items
            if(i == parentResultIdIndex || i == resultIdIndex || i == replicateIndex || columns[i] == "sid") {
                continue;
            }

            def (element, modifier) = findElement(columns[i]);

            if(element == null) {
                throw new RuntimeException("Could not find element \"${columns[i]}\"");
            }

            DatasetColumn column = new DatasetColumn(attribute: element, statsModifier: modifier, index: i)

            if(isResultType(element)) {
                resultTypes.add(column)
            } else {
                contextItems.add(column)
            }
        }

        populateNext();
    }

    private List findElement(String name) {
        Matcher m = ELEMENT_WITH_MODIFIER.matcher(name)
        if(m.matches()) {
            String elementName = m.group(1);
            String modifierName = m.group(2);

            Element element = Element.findByLabel(elementName);
            Element modifier = Element.findByLabel(modifierName);

            if(element != null && modifier != null) {
                return [element, modifier]
            }
        }

        Element element = Element.findByLabel(name);

        return [element, null];
    }

    boolean isResultType(Element element) {
        // todo: fix this
        return true;
    }

    void populateNext() {
        nextRow = reader.readNext() as List
        if (nextRow == null) {
            reachedEnd = true;
            nextRow = null;
            nextSid = null;
            reader.close();
        } else {
            nextSid = Long.parseLong(nextRow[0])
        }
    }

    Dataset readNextDataset() {
        List<List<String>> rows = [];
        Long sid = nextSid;
        while (true) {
            if (nextSid != sid) {
                break
            }
            rows.add(nextRow)

            populateNext();
            if (reachedEnd)
                break;
        }

        Dataset dataset = new Dataset(parentResultIdIndex: parentResultIdIndex,
                resultIdIndex: resultIdIndex,
                replicateIndex: replicateIndex,
                resultTypes: resultTypes,
                contextItems: contextItems,
                rows: rows);
        return dataset;
    }
}


class DatasetColumn {
    Element attribute;
    Element statsModifier;
    int index;
}

class Dataset {
    Integer parentResultIdIndex;
    Integer resultIdIndex;
    Integer replicateIndex;

    List<DatasetColumn> resultTypes;
    List<DatasetColumn> contextItems;
    List<List<String>> rows;

}

class Batch {
    Long sid;
    List<Dataset> datasets;
}
