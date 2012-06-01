package bard.db.registration

import bard.db.dictionary.*

class AssayService {

    Map getMeasureContextItemsForAssay(Assay assay) {
        Map map = [:]
        map.'Assay Context' = assay.measureContextItems
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
        assay.measureContexts.each {
            List info = []
            it.measures.each {
                String measureText = ResultType.findByElement(Element.get(it.resultTypeId))?.resultTypeName
                if (it.entryUnit != null) {
                    measureText += " " + it.entryUnit.unit
                }
                info.add(measureText)
            }
            if (it.measureContextItems != null) {
                it.measureContextItems.each {
                    if (it.parentGroup == null) {
                        info.add(it)
                    }
                }
             }
            map.put(it.contextName, info)
        }
        return map
    }

    Map getPathForAssayDescriptor(AssayDescriptor node, def value) {
        Map map =[:]
        map.put(node.label, value)
        AssayDescriptor parent = node.getParent()
        if (parent != null) {
            map = getPathForAssayDescriptor(parent, map)
        }
        return map
    }

    Map getPathForBiologyDescriptor(BiologyDescriptor node, def value) {
        Map map =[:]
        map.put(node.label, value)
        BiologyDescriptor parent = node.getParent()
        if (parent != null) {
            map = getPathForBiologyDescriptor(parent, map)
        }
        return map
    }

    Map getPathForInstanceDescriptor(InstanceDescriptor node, def value) {
        Map map =[:]
        map.put(node.label, value)
        InstanceDescriptor parent = node.getParent()
        if (parent != null) {
            map = getPathForInstanceDescriptor(parent, map)
        }
        return map
    }

    // TODO get rid of path reconstruction
    Map addItem(MeasureContextItem item) {
        AssayDescriptor assayDescriptor = AssayDescriptor.findByElement(item.attributeElement)
        if (assayDescriptor != null) {
            return getPathForAssayDescriptor(assayDescriptor, item)
        }
        BiologyDescriptor biologyDescriptor = BiologyDescriptor.findByElement(item.attributeElement)
        if (biologyDescriptor != null) {
            return getPathForBiologyDescriptor(biologyDescriptor, item)
        }
        InstanceDescriptor instanceDescriptor = InstanceDescriptor.findByElement(item.attributeElement)
        if (instanceDescriptor != null) {
            return getPathForInstanceDescriptor(instanceDescriptor, item)
        }
        throw new RuntimeException("Unsupported Element Type: " + item)
    }

    // get an array of labels by navigating up through the parents
    // need to handle each of the trees seperately (yuk)
    // then create the nested maps using a loop and a put
    // at the end put the measure context item into the bottom of the map


}
