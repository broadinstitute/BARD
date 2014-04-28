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

package adf.imp

import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element
import bard.db.experiment.JsonSubstanceResults

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

    public DatasetParser(List<String> filenames, Closure isResultType) {
        openFiles = filenames.collect {
            new FileState(DatasetSoryBySid.sortCsvFile(it), isResultType)
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

    Integer sidIndex = -1;
    Integer parentResultIdIndex = -1;
    Integer resultIdIndex = -1;
    Integer replicateIndex = -1;

    List<DatasetColumn> resultTypes = [];
    List<DatasetColumn> contextItems = [];
    Map<String, Integer> columnByName = [:]
    Closure isResultType;

    public FileState(CSVReader reader, Closure isResultType) {
        this.isResultType = isResultType
        this.reader = reader
        columns = reader.readNext() as List
        columns.eachWithIndex { String entry, int i -> columnByName[entry] = i }

        sidIndex = columnByName["sid"]
        parentResultIdIndex = columnByName["parentResultId"]
        resultIdIndex = columnByName["resultId"]
        replicateIndex = columnByName["replicateIndex"]

        for(int i=0;i<columns.size();i++) {
            // skip the special columns.  All others should be result type or context items
            if(i == parentResultIdIndex || i == resultIdIndex || i == replicateIndex || i == sidIndex) {
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
