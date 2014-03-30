import bard.db.dictionary.Element
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

final String label = "biology"

List<Element> foundList = Element.findAllByLabelIlike(label)

if (foundList.size() == 0) {
    println("no matching element found")
    return
} else if (foundList.size() > 1) {
    println("DANGER:  multiple elements found that only differ in there capitalization.  Fix this!")
    for (Element e : foundList) {
        println("id:${e.id} label:${e.label}")
    }
    return
}

Element found = foundList.get(0)

List<AssayContextItem> aciList = AssayContextItem.findAllByValueElement(found)
List<AbstractContext> acList = prepareUniqueOrderedList(aciList)
aciList = AssayContextItem.findAllByAttributeElement(found)
acList.addAll(prepareUniqueOrderedList(aciList))
if (acList.size() > 0) {
    println("found in assay definitions:")
    displayAbstractContexts(acList, "assay")
}


List<ProjectContextItem> pciList = ProjectContextItem.findAllByValueElement(found)
pciList.addAll(ProjectContextItem.findAllByAttributeElement(found))
if (pciList.size() > 0) {
    println("found in projects:")
    List<AbstractContext> pcList = prepareUniqueOrderedList(pciList)
    displayAbstractContexts(pcList, "project")
}


List<ExperimentContextItem> eciList = ExperimentContextItem.findAllByValueElement(found)
eciList.addAll(ExperimentContextItem.findAllByAttributeElement(found))
if (eciList.size() > 0) {
    println("found in experiments:")
    List<AbstractContext> ecList = prepareUniqueOrderedList(eciList)
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

List<AbstractContext> prepareUniqueOrderedList(List<AbstractContextItem> aciList) {
    List<AbstractContext> acList = aciList.collect({AbstractContextItem it -> return it.context})

    Set<AbstractContext> acValueSet = new HashSet<>(acList)

    acList = new ArrayList<>(acValueSet)

    Collections.sort(acList, new AbstractContextComparator())

    return acList
}