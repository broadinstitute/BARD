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

// @param label excludeRetired

// @description find all contexts (assay, project, experiment) where an element appears.  excludeRetired defaults to true

import bard.db.dictionary.Element
import bard.db.enums.Status
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created by dlahr on 3/27/2014.
 */


List<Element> foundList = Element.findAllByLabelIlike(label)

if (foundList.size() == 0) {
    println("no matching element found for entered label:  $label")
    return
} else if (foundList.size() > 1) {
    println("DANGER:  multiple elements found that only differ in there capitalization.  Fix this!")
    for (Element e : foundList) {
        println("id:${e.id} label:${e.label}")
    }
    return
}

final boolean excludeRetiredBool = excludeRetired ? excludeRetired.trim().equalsIgnoreCase("true") : true

Element found = foundList.get(0)

List<AssayContextItem> aciList = AssayContextItem.findAllByValueElement(found)
List<AbstractContext> acList = prepareUniqueOrderedList(aciList, excludeRetiredBool)
aciList = AssayContextItem.findAllByAttributeElement(found)
acList.addAll(prepareUniqueOrderedList(aciList, excludeRetiredBool))
if (acList.size() > 0) {
    println("found in assay definitions:")
    displayAbstractContexts(acList, "assay")
}


List<ProjectContextItem> pciList = ProjectContextItem.findAllByValueElement(found)
pciList.addAll(ProjectContextItem.findAllByAttributeElement(found))
if (pciList.size() > 0) {
    println("found in projects:")
    List<AbstractContext> pcList = prepareUniqueOrderedList(pciList, excludeRetiredBool)
    displayAbstractContexts(pcList, "project")
}


List<ExperimentContextItem> eciList = ExperimentContextItem.findAllByValueElement(found)
eciList.addAll(ExperimentContextItem.findAllByAttributeElement(found))
if (eciList.size() > 0) {
    println("found in experiments:")
    List<AbstractContext> ecList = prepareUniqueOrderedList(eciList, excludeRetiredBool)
    displayAbstractContexts(ecList, "experiment")
}


return

class Utilities {
    static Long getParentId(AbstractContext ac) {
        if (ac instanceof AssayContext) {
            return ac.assay.id
        } else if (ac instanceof ProjectContext) {
            return ac.project.id
        } else if (ac instanceof ExperimentContext) {
            return ac.experiment.id
        } else {
            return null
        }
    }

    static Status getStatus(AbstractContext ac) {
        if (ac instanceof AssayContext) {
            return ac.assay.assayStatus
        } else if (ac instanceof ProjectContext) {
            return ac.project.projectStatus
        } else if (ac instanceof ExperimentContext) {
            return ac.experiment.experimentStatus
        } else {
            return null
        }
    }
}

class AbstractContextComparator implements Comparator<AbstractContext> {
    @Override
    int compare(AbstractContext o1, AbstractContext o2) {
        Long id1 = Utilities.getParentId(o1)
        Long id2 = Utilities.getParentId(o2)

        if (id1 != id2) {
            return id1 - id2
        } else {
            return o1.contextName.compareTo(o2.contextName)
        }
    }
}


void displayAbstractContexts(Collection<AbstractContext> acColl, String type) {
    for (AbstractContext ac : acColl) {
        println("$type id:${Utilities.getParentId(ac)}  context id:${ac.id} context name:${ac.contextName}")
        for (AbstractContextItem aci : ac.contextItems) {
            println("${aci.attributeElement.label} : ${aci.valueDisplay}")
        }
        println()
    }
}

List<AbstractContext> prepareUniqueOrderedList(List<AbstractContextItem> aciList, boolean excludeRetired) {
    List<AbstractContext> acList = aciList.collect({AbstractContextItem it ->
        if (! excludeRetired || Utilities.getStatus(it.context) != Status.RETIRED) {
            return it.context
        }
        return null
    })

    Set<AbstractContext> acValueSet = new HashSet<>(acList)
    acValueSet.remove(null)

    acList = new ArrayList<>(acValueSet)

    Collections.sort(acList, new AbstractContextComparator())

    return acList
}
