package bard.db.registration

import bard.db.dictionary.*

class AssayService {

    Map getAssayContextItemsForAssay(Assay assay) {
        Map map = [:]
        map.'Assay Context' = assay.assayContextItems
        map.'Result Context' = getResultTypeMapForAssay(assay)
        return map
    }

    void mergeMaps(def map, def subMap) {
        log.info("  Map:    " + map)
        log.info("  SubMap: " +subMap)
        subMap.keySet().each {
            if (map.containsKey(it)) {
                if (map.get(it) instanceof Map && subMap.get(it) instanceof Map) {
                    mergeMaps(map.get(it), subMap.get(it))
                }
                else {
                    def item = map.get(it)
                    if (item instanceof List) {
                        item.push(subMap)
                    }
                    else {
                        List list = []
                        list.push(item)
                        list.push(subMap)
                        map.put(it,list)
                    }
                }
            }
            else {
                map.put(it, subMap.get(it))
            }
        }
    }

    Map getResultTypeMapForAssay(Assay assay) {
        Map map = [:]
        // TODO change logic below to match the following:
        // get measures
        //   those with parent measure id == null are top level
        //     then get child measures
        //     then get measure context for parent measure
        //       then get measure context items for measure context
        //         if a list type, concatenate all the values together into a single line item (e.g. concentrations for dose)
        assay.assayContexts.each {
            List info = []
            it.measures.each {
                String measureText = ResultType.findByElement(Element.get(it.resultTypeId))?.resultTypeName
                if (it.entryUnit != null) {
                    measureText += " " + it.entryUnit.unit
                }
                info.add(measureText)
            }
            if (it.assayContextItems != null) {
                it.assayContextItems.each {
                    if (it.parentGroup == null) {
                        info.add(it)
                    }
                }
             }
            map.put(it.contextName, info)
        }
        return map
    }


}
