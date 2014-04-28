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

package adf.exp

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bard.db.experiment.JsonSubstanceResults

/**
 * Created by ddurkin on 3/20/14.
 */
class ResultSetPipeline {

    Map<Path, Box> resultSetBoxMap;

    public ResultSetPipeline() {
        resultSetBoxMap = [:]
        // create the top level box
        resultSetBoxMap.put(null, new Box())
    }

    void populateBoxes(JsonSubstanceResults jsr) {
        populateBox(jsr.rootElem, null, resultSetBoxMap[null])
    }

    public static List findSingletons(List<JsonResult> jsonResultList) {
        Collection<List<JsonResult>> grouped = jsonResultList.groupBy { new ResultKey(it) } . values()
        List<JsonResult> multivalued = []
        List<JsonResult> singleton = []
        grouped.each {
            if(it.size() == 1) {
                singleton.addAll(it)
            } else {
                multivalued.addAll(it)
            }
        }
        return [singleton, multivalued]
    }

//    private void populateBox(List<JsonResult> jsonResultList, Path path, Box box) {
//        // Divide child result types into those that appear once and those that appear more then once.
//        // Those that appear multiple times _must_ be put in their own box.  Those that are singletons _might_ be
//        // possible to keep in the same box.   However, another sample might have multiple records, in which case
//        // we need to update our bookkeeping to indicate they belong in a different box
//        def (singletons, multivalued) = findSingletons(jsonResultList)
//        for(JsonResult jr in singletons) {
//            // if we don't already have a child box for this, then we can (for now) say
//            // it's a result column within the current box.
//            Path childPath = new Path(jr, path)
//            Box existingBox = resultSetBoxMap[childPath];
//            if(existingBox == null) {
//                box.addResultType(jr);
//                addContextItems(box, jr.contextItems)
//            }
//        }
//
//        for (JsonResult jr in multivalued) {
//            Path childPath = new Path(jr, path)
//            Box childBox = resultSetBoxMap[path]
//            if(childBox == null) {
//                // in case this got added as a column in the box already, remove it
//                box.removeResultType(jr);
//                childBox = new Box()
//                resultSetBoxMap[path] = childBox
//            }
//            addContextItems(box, jr.contextItems)
//            populateBox(jr.related, childPath, childBox);
//        }
//    }

    void add(JsonResult jr, Path path) {
        add(new ResultJsonContainer(jr), path)
    }

    void add(JsonResultContextItem jrci, Path path) {
        add(new ResultJsonContainer(jrci), path)
    }

    void addContextItems(Box box, List<JsonResultContextItem> jrciList) {
        List<JsonResultContextItem> uniqueJrciList = jrciList.unique { a, b -> a.attribute<=>b.attribute }
        for (JsonResultContextItem uniqueJrci in uniqueJrciList) {
            box.addContextItem(uniqueJrci)
        }
    }


//    void printlnRow(Long sid, Map<Path, CSVWriter> files) {
//        for (Path path in resultSetBoxMap.keySet()) {
//            CSVWriter writer = files.get(path);
//            Box box = resultSetBoxMap.get(path)
//            final int maxResultRows = box.getNumberOfRows()
//            Set<Path> columnHeaders = box.resultsPerMeasureMap.keySet()
//
//            for (int i = 0; i < maxResultRows; i++) {
//                List<String> rowValues = []
//                columnHeaders.eachWithIndex { Path columnHeader, int index ->
//                    if (index == 0) {
//                        rowValues.add(sid)
//                    }
//                    final List<ResultJsonContainer> resultJsonContainerList = box.resultsPerMeasureMap.get(columnHeader)
//                    final String valueDisplay = resultJsonContainerList?.getAt(i)?.valueDisplay
//                    rowValues.add(valueDisplay)
//                }
//                writeRow(writer, rowValues)
//            }
//        }
//    }
//
//
//
//    private void printJsonResult(JsonResult jr, Path path) {
//        String offset = buildOffset(path)
//        final String relationShip = StringUtils.trimToEmpty(jr.relationship).padRight(16)
//        println("${offset}${jr.resultType.padRight(30)} ${relationShip} ${jr.valueDisplay}")
//    }
//
//    private void printJsonResultContextItem(JsonResultContextItem jrci, Path path) {
//        final String offset = buildOffset(path)
//        println("${offset}attribute: ${jrci.attribute.padRight(30)} -> value: ${jrci.valueDisplay}")
//    }
//
//
//    private String buildOffset(Path path) {
//        String offset = ""
//        path.getPath().size().times {
//            offset += "    "
//        }
//        offset
//    }

    public void print() {
        resultSetBoxMap.values().each {
            println it
        }
    }
}
