import bard.db.dictionary.Element
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created by dlahr on 4/14/2014.
 */


final Element targetCell = Element.findById(369)
final Element atccCellName = Element.findById(1952)
final Element otherCellName = Element.findById(377)
final Element culturedCell = Element.findById(244)

final AssayTraitsComparator atComp = new AssayTraitsComparator([atccCellName, otherCellName, culturedCell] as List<Element>)


println("retrieving assay context items with target cell from database:")
List<AssayContextItem> aciList = AssayContextItem.findAllByValueElement(targetCell)
println("found ${aciList.size()}")

Map<AssayTraits, List<AssayTraits>> assayTraitsMap = new HashMap<>()

int progress = 0
println("analyzing:")
for (AssayContextItem aci : aciList) {
    AssayContext ac = aci.context

    AssayTraits at = null
    for (AssayContextItem otherAci : ac.contextItems) {

        Iterator<Element> cellAttrIter = atComp.attrCompList.iterator()
        while (cellAttrIter.hasNext()) {
            Element cellAttr = cellAttrIter.next()

            if (otherAci.attributeElementId == cellAttr.id) {
                if (! at) {
                    Map<Element, AssayContextItem> map = new HashMap<>()
                    map.put(cellAttr, otherAci)
                    at = new AssayTraits(ac.assay, map, atComp.attrCompList)
                } else {
                    println("multiple cell line specifications found:  assay.id:${ac.assay.id} ac.id:${ac.id}")
                }
            }
        }
    }

    if (at) {
        List<AssayTraits> list = assayTraitsMap.get(at)
        if (! list) {
            list = new LinkedList<>()
            assayTraitsMap.put(at, list)
        }

        list.add(at)
    }

    progress++

    if (progress%100 == 0) {
        println("progress:$progress")
    }
}


List<List<AssayTraits>> atListList = new ArrayList<>(assayTraitsMap.values())
atListList = atListList.findAll({List<AssayTraits> it -> it.size() > 1})

Collections.sort(atListList, new Comparator<List<AssayTraits>>() {
    @Override
    int compare(List<AssayTraits> o1, List<AssayTraits> o2) {
        return atComp.compare(o1.get(0), o2.get(0))
    }
})

println()
println("results:")
for (List<AssayTraits> list : atListList) {
    Collections.sort(list, atComp)

    for (AssayTraits at : list) {
        println(at)
    }

    println()
}


return

class AssayTraits {
    final List<Element> attrList

    final Assay assay

    Map<Element, AssayContextItem> attrValueMap

    AssayTraits(Assay assay, Map<Element, AssayContextItem> attrValueMap, List<Element> attrList) {
        this.assay = assay

        this.attrValueMap = Collections.unmodifiableMap(attrValueMap)

        this.attrList = attrList
    }

    @Override
    int hashCode() {
        int result = 0
        for (Element attribute : attrList) {
            if (attrValueMap.containsKey(attribute)) {
                result = (31*result) + attribute.id.hashCode()

                AssayContextItem aci = attrValueMap.get(attribute)
                int aciHashCode = aci.valueElementId ? aci.valueElementId.hashCode() : aci.extValueId.hashCode()

                result = (31*result) + aciHashCode
            }
        }
        return result
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof AssayTraits) {
            AssayTraits other = (AssayTraits)obj

            if (attrValueMap.keySet().size() != other.attrValueMap.keySet().size()) {
                return false
            } else {
                for (Element attribute : attrValueMap.keySet()) {
                    if (! other.attrValueMap.containsKey(attribute)) {
                        return false
                    }

                    AssayContextItem aci = attrValueMap.get(attribute)
                    AssayContextItem otherAci = other.attrValueMap.get(attribute)

                    if (aci.valueElementId && otherAci.valueElementId && aci.valueElementId != otherAci.valueElementId) {
                        return false
                    }
                    if (aci.extValueId && otherAci.extValueId && ! aci.extValueId.equals(otherAci.extValueId)) {
                        return false
                    }
                }

                return true
            }
        } else {
            return false
        }
    }

    @Override
    String toString() {
        StringBuilder builder = new StringBuilder()
        builder.append("assay.id:${assay.id} ")

        for (Element attr : attrValueMap.keySet()) {
            AssayContextItem aci = attrValueMap.get(attr)
            String v = aci.valueElement ? aci.valueElement.label : aci.extValueId

            builder.append("${attr.label}:$v ")
        }
        return builder.toString()
    }
}


class AssayTraitsComparator implements Comparator<AssayTraits> {
    final List<Element> attrCompList

    AssayTraitsComparator(List<Element> attrCompList) {
        this.attrCompList = Collections.unmodifiableList(attrCompList)
    }

    @Override
    int compare(AssayTraits o1, AssayTraits o2) {

        for (Element attr : attrCompList) {
            if (o1.attrValueMap.containsKey(attr) && o2.attrValueMap.containsKey(attr)) {
                AssayContextItem v1 = o1.attrValueMap.get(attr)
                AssayContextItem v2 = o2.attrValueMap.get(attr)

                if (v1.attributeElement && v2.attributeElement && v1.attributeElementId != v2.attributeElementId) {
                    return v1.attributeElementId - v2.attributeElementId
                }
                if (v1.extValueId && v2.extValueId && ! v1.extValueId.equals(v2.extValueId)) {
                    return v1.extValueId.compareTo(v2.extValueId)
                }
            } else {
                int a1 = o1.attrValueMap.containsKey(attr) ? attr.id : -1
                int a2 = o2.attrValueMap.containsKey(attr) ? attr.id : -1

                if (a1 != a2) {
                    return a1 - a2
                }
            }
        }

        return o1.assay.id - o2.assay.id
    }
}