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
