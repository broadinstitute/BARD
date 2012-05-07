package bard.db.registration

import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.BiologyDescriptor
import bard.db.dictionary.InstanceDescriptor

class AssayService {

    Map getMeasureContextItemsForAssay(Assay assay) {
        Map map = [:]
        assay.measureContextItems.each {
            if (it.measureContext == null) {
                Map submap = addItem(it)
                mergeMaps(map, submap)
            }
        }
        map.'Result Types' = getResultTypeMapForAssay(assay)
        return map
    }

    void mergeMaps(Map map, Map subMap) {
        subMap.keySet().each {
            if (map.containsKey(it)) {
                mergeMaps(map.get(it), subMap.get(it))
            }
            else {
                map.put(it, subMap.get(it))
            }
        }
    }

    Map getResultTypeMapForAssay(Assay assay) {
        Map map = [:]
        assay.measureContexts.each {
            List info = []
            it.measures.each {
                String measureText = it.resultType.resultTypeName
                if (it.entryUnit != null) {
                    measureText += " " + it.entryUnit.unit
                }
                info.add(measureText)
            }
            if (it.measureContextItems != null) {
                info.addAll(it.measureContextItems)
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
        throw new RuntimeException("Unsupported Element Type")
    }

    // get an array of labels by navigating up through the parents
    // need to handle each of the trees seperately (yuk)
    // then create the nested maps using a loop and a put
    // at the end put the measure context item into the bottom of the map


}
