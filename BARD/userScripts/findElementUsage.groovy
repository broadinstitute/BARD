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

final boolean excludeRetiredBool = excludeRetired.trim().equalsIgnoreCase("true")

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