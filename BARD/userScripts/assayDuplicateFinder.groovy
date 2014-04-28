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

// @description findValues indicates the assay component values that should be used to find the assay contexts that will be used for comparision.  Default value is 'target cell, host cell'.   matchAttributes are the attributes within a context that will be used to match assay definitions.  Default value is 'assay component role, ATCC cell name, cultured cell, other cell name'.   excludePanels indicates that assay definitions that are part of panels will not be included - default is 'true' indicating they are not included

// @param findValues matchAttributes excludePanels

import bard.db.dictionary.Element
import bard.db.enums.Status
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem


/**
 * Created by dlahr on 4/14/2014.
 */

if (! findValues) {
    findValues = "target cell, host cell"
}
List<String> findValueLabelList = findValues.split(",") as List<String>

if (! matchAttributes) {
    matchAttributes = "assay component role, ATCC cell name, cultured cell, other cell name"
}
String[] attrLabelArray = matchAttributes.split(",")

if (! excludePanels) {
    excludePanels = "true"
}
final boolean excludePanelsBoolean = excludePanels.trim().equalsIgnoreCase("true")


List<Element> aciMetaDataAttr = Element.findAllByLabelIlike("assay component concentration%")
aciMetaDataAttr.add(Element.findByLabel("assay component name"))
println("meta data to use:  $aciMetaDataAttr")


List<Element> findValueList = Element.findAllByLabelInList(findValueLabelList)

List<Element> attrList = new ArrayList<>(attrLabelArray.length)
for (String attrLabel : attrLabelArray) {
    Element found = Element.findByLabel(attrLabel.trim())
    if (! found) {
        println("could not find provided attribute in system:  $attrLabel")
        return
    } else {
        attrList.add(found)
    }
}

Map<AssayTraits, List<AssayTraits>> assayTraitsMap = new HashMap<>()

MetaDataBuilder<AssayContext> acMdb = new AssayContextMetaDataBuilder()
MetaDataBuilder<AssayContextItem> aciMdb = new AssayContextItemMetaDataBuilder(aciMetaDataAttr)

List<String> allKeys = new LinkedList<>(acMdb.keys)
allKeys.addAll(aciMdb.keys)
final AssayTraitsComparator atComp = new AssayTraitsComparator(attrList, allKeys)


println("retrieving assay contexts that contain context items with values $findValueLabelList that are not retired from database:")
Set<AssayContext> acSet =
        AssayContextItem.findAllByValueElementInList(findValueList).collect({
            AssayContextItem aci ->
                Assay a = aci.assayContext.assay
                if (a.assayStatus != Status.RETIRED) {
                    if (! excludePanelsBoolean || a.panelAssays.size() == 0) {
                        return aci.assayContext
                    }
                }
        }) as Set<AssayContext>

acSet.remove(null)

//        new HashSet<>()
//for (AssayContextItem aci : ) {
//    acSet.add(aci.assayContext)
//}
//acSet = acSet.findAll({AssayContext ac -> ac.assay.assayStatus != Status.RETIRED}) as Set<AssayContext>
println("found ${acSet.size()}")


int progress = 0
println("analyzing:")
for (AssayContext ac : acSet) {

    AssayTraits at = new AssayTraits(ac.assay, atComp.attrCompList, allKeys)

    acMdb.addMetaData(at.metaData, ac)

    for (AssayContextItem otherAci : ac.contextItems) {
        aciMdb.addMetaData(at.metaData, otherAci)

        for (Element attr : atComp.attrCompList) {
            if (otherAci.attributeElementId == attr.id) {
                at.attrValueMap.put(attr, otherAci)
            }
        }
    }


    if (at.attrValueMap.size() > 0) {
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

    final Map<String, String> metaData
    final List<String> metaDataKeyList

    AssayTraits(Assay assay, List<Element> attrList, List<String> metaDataKeyList) {
        this.assay = assay

        this.attrList = attrList

        this.attrValueMap = new HashMap<>()

        metaData = new HashMap<>()
        this.metaDataKeyList = metaDataKeyList
    }

    @Override
    int hashCode() {
        int result = 0
        for (Element attribute : attrList) {
            if (attrValueMap.containsKey(attribute)) {
                result = (31*result) + attribute.id.hashCode()

                AssayContextItem aci = attrValueMap.get(attribute)
                int aciHashCode = aci.valueElementId ? aci.valueElementId.hashCode() :
                        aci.extValueId ? aci.extValueId.hashCode() : aci.valueDisplay.hashCode()

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
                    if (aci.valueDisplay && otherAci.valueDisplay && ! aci.valueDisplay.equalsIgnoreCase(otherAci.valueDisplay)) {
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

        for (Element attr : attrList) {
            AssayContextItem aci = attrValueMap.get(attr)

            if (aci) {
                String v = aci.valueElement ? aci.valueElement.label :
                        aci.extValueId ? aci.extValueId : aci.valueDisplay
                builder.append("${attr.label}:$v ")
            }
        }

        for (String key : metaDataKeyList) {
            String m = metaData.get(key)

            if (m) {
                builder.append("$key:$m ")
            }
        }

        return builder.toString()
    }
}


class AssayTraitsComparator implements Comparator<AssayTraits> {
    final List<Element> attrCompList

    final List<String> metaDataKeyList

    AssayTraitsComparator(List<Element> attrCompList, List<String> metaDataKeyList) {
        this.attrCompList = Collections.unmodifiableList(attrCompList)

        this.metaDataKeyList = Collections.unmodifiableList(metaDataKeyList)
    }

    @Override
    int compare(AssayTraits o1, AssayTraits o2) {

        for (Element attr : attrCompList) {
            if (o1.attrValueMap.containsKey(attr) && o2.attrValueMap.containsKey(attr)) {
                AssayContextItem v1 = o1.attrValueMap.get(attr)
                AssayContextItem v2 = o2.attrValueMap.get(attr)

                if (v1.attributeElement && v2.attributeElement && v1.attributeElementId != v2.attributeElementId) {
                    return v1.attributeElementId - v2.attributeElementId
                } else if (v1.extValueId && v2.extValueId && ! v1.extValueId.equals(v2.extValueId)) {
                    return v1.extValueId.compareTo(v2.extValueId)
                } else if (v1.valueDisplay && v2.valueDisplay && !v1.valueDisplay.equalsIgnoreCase(v2.valueDisplay)) {
                    return v1.valueDisplay.compareTo(v2.valueDisplay)
                }
            } else {
                int a1 = o1.attrValueMap.containsKey(attr) ? 1 : 0
                int a2 = o2.attrValueMap.containsKey(attr) ? 1 : 0

                if (a1 != a2) {
                    return a1 - a2
                }
            }
        }

        for (String key : metaDataKeyList) {
            if (o1.metaData.containsKey(key) && o2.metaData.containsKey(key)) {
                String m1 = o1.metaData.get(key)
                String m2 = o2.metaData.get(key)

                if (! m1.equalsIgnoreCase(m2)) {
                    return m1.compareTo(m2)
                }
            } else {
                int k1 = o1.metaData.containsKey(key) ? 1 : 0
                int k2 = o1.metaData.containsKey(key) ? 1 : 0

                if (k1 != k2) {
                    return k1 - k2
                }

            }
        }

        return o1.assay.id - o2.assay.id
    }
}

interface MetaDataBuilder<T> {
    void addMetaData(Map<String, String> metaData, T source)

    List<String> getKeys()
}

class AssayContextMetaDataBuilder implements MetaDataBuilder<AssayContext> {
    private static final String key = "owner"

    private static final List<String> keyList = Collections.unmodifiableList([key] as List<String>)

    @Override
    void addMetaData(Map<String, String> metaData, AssayContext source) {
        metaData.put(key, source.assay.ownerRole.displayName)
    }

    @Override
    List<String> getKeys() {
        return keyList
    }
}

class AssayContextItemMetaDataBuilder implements MetaDataBuilder<AssayContextItem> {

    private final List<String> keyList

    final List<Element> attrList

    AssayContextItemMetaDataBuilder(List<Element> attrList) {
        this.attrList = Collections.unmodifiableList(attrList)

        keyList = attrList.collect({Element it -> it.label}) as List<String>
    }

    @Override
    void addMetaData(Map<String, String> metaData, AssayContextItem source) {
        for (Element attr : attrList) {
            if (source.attributeElementId == attr.id) {
                metaData.put(attr.label, source.valueDisplay)
            }
        }
    }

    @Override
    List<String> getKeys() {
        return keyList
    }
}
