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

println("retrieving assay context items with target cell from database:")
List<AssayContextItem> aciList = AssayContextItem.findAllByValueElement(targetCell)
println("found ${aciList.size()}")

Map<AssayTraits, List<AssayTraits>> assayTraitsMap = new HashMap<>()

int progress = 0
println("analyzing:")
for (AssayContextItem aci : aciList) {
    AssayContext ac = aci.context

    AssayTraits at = null
    boolean found = false
    for (AssayContextItem otherAci : ac.contextItems) {
        if (! found && otherAci.attributeElementId.equals(atccCellName.id)) {
            at = new AssayTraits(ac.assay, atccCellName.id, otherAci.extValueId, null)
            found = true
        } else if (! found && otherAci.attributeElementId.equals(otherCellName.id)) {
            at = new AssayTraits(ac.assay, otherCellName.id, null, otherAci.valueElementId)
            found = true
        } else if (found && (otherAci.attributeElementId.equals(atccCellName.id) || otherAci.attributeElementId.equals(otherCellName.id))) {
            println("multiple cell line specifications found:  assay.id:${ac.assay.id} ac.id:${ac.id}")
        }
    }

    if (found) {
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

final AssayTraitsComparator atComp = new AssayTraitsComparator()

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
    final Assay assay

    final Long cellSourceId
    final String atccId
    final Long otherCellNameId

    AssayTraits(Assay assay, Long cellSourceId, String atccId, Long otherCellNameId) {
        this.assay = assay
        this.cellSourceId = cellSourceId
        this.atccId = atccId
        this.otherCellNameId = otherCellNameId

        if ((atccId != null && otherCellNameId != null) || (null == atccId && null == otherCellNameId)) {
            throw new RuntimeException("AssayTraits constructor - one and only one of atccId and otherCellNameId must be non null")
        }
    }

    @Override
    int hashCode() {
        int result = cellSourceId.hashCode()
        result = (31*result) + (atccId ? atccId.hashCode() : 0)
        result = (31*result) + (otherCellNameId ? otherCellNameId.hashCode() : 0)
        return result
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof AssayTraits) {
            AssayTraits other = (AssayTraits)obj

            boolean result = cellSourceId == other.cellSourceId
            result = result && (atccId == null && other.atccId == null) || atccId.equals(other.atccId)
            result = result && (otherCellNameId == other.otherCellNameId)

            return result
        } else {
            return false
        }
    }

    @Override
    String toString() {
        String cellIdentifier = atccId != null ? "atccId:$atccId" : "otherCellNameId:$otherCellNameId"
        return "assay.id:${assay.id} $cellIdentifier".toString()
    }
}


class AssayTraitsComparator implements Comparator<AssayTraits> {
    @Override
    int compare(AssayTraits o1, AssayTraits o2) {
        if (o1.cellSourceId == o2.cellSourceId) {
            if (o1.atccId) {
                if (o1.atccId.equals(o2.atccId)) {
                    return o1.assay.id - o2.assay.id
                } else {
                    return o1.atccId.compareTo(o2.atccId)
                }
            } else {
                if (o1.otherCellNameId == o2.otherCellNameId) {
                    return o1.assay.id - o2.assay.id
                } else {
                    return o1.otherCellNameId - o2.otherCellNameId
                }
            }
        } else {
            return o1.cellSourceId - o2.cellSourceId
        }
    }
}