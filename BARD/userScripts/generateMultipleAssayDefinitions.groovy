import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.enums.ValueType
import bard.db.experiment.Experiment
import bard.db.experiment.PanelExperiment
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.Panel
import bard.db.registration.PanelAssay
import bard.db.registration.PanelService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import registration.AssayService

/**
 * Created by dlahr on 3/14/14.
 */


final Integer assayId = 8265
final Integer assayContextId = 576582
final File contextInfoFile = new File("C:/Local/i_drive/projects/bard/dataMigration/NCI60/NCI_CellLineName_Density.csv")
final Role newOwnerRole = Role.findById(16) //Broad owner role
final boolean flush = true
Panel panel = Panel.findById(185) //panel to put new assay definitions in

SpringSecurityUtils.reauthenticate("dlahr", null)


println("read context info from file ${contextInfoFile.getAbsolutePath()}")
List<ContextInfo> ciList = readContextInfoFromFile(contextInfoFile)
//ciList = ciList.subList(0,2)

//investigateExistingCellLineNamesAndParents(ciList)
//result - some were found, all had parent that was "other cell name"

Element otherCellName = Element.findById(377)
Element cellsPerWell = Element.findById(2302)

Assay templateAssay = Assay.findById(assayId)
AssayContext variableContext = AssayContext.findById(assayContextId)

AssayService assayService = ctx.assayService

PanelExperiment panelExperiment = null
Assay.withTransaction {status ->
    println("create panelExperiment")
    panelExperiment = new PanelExperiment()
    panelExperiment.panel = panel
    panelExperiment.modifiedBy = "dlahr"

    trySave(panelExperiment, flush, "panelExperiment")

    for (ContextInfo ci : ciList) {
        Element cellName = findOrCreateCellLineName(ci.cellLineName, otherCellName, true)
        assert cellName

        Assay newAssay = assayService.cloneAssayForEditing(templateAssay, "NCI60")
        newAssay.assayName = "${templateAssay.assayName} for cell line ${cellName.label}"
        newAssay.ownerRole = newOwnerRole

        AssayContext ac = findMatchingAssayContext(newAssay, variableContext, false)
        assert ac

        addContextItems(ac, otherCellName, cellName, cellsPerWell, ci.cellsPerContainer, flush)

        trySave(newAssay, flush, "assay for cell line ${cellName.label}")

        ci.assay = newAssay

        Experiment e = new Experiment()
        e.experimentName = "Experiment from ${newAssay.assayName}".toString()
        e.ownerRole = newOwnerRole
        e.assay = newAssay
        panelExperiment.addToExperiments(e)

        trySave(e, flush, "experiment for cell line ${cellName.label}")

        ci.experimentId = e.id
    }

    println("add assays to panel")
    List<Assay> assayList = ciList.collect({ContextInfo ci -> return ci.assay})
    PanelService panelService = ctx.panelService
    panelService.associateAssays(panel.id, assayList)

    trySave(panel, flush, "panel")

    status.setRollbackOnly()
}



println()
println("results:  ")
println("panelExperiment.id:  ${panelExperiment.id}")
for (ContextInfo ci : ciList) {
    List<String> fields = [ci.cellSerial, ci.cellLineName, ci.cellsPerContainer, ci.assay.id, ci.experimentId] as List<String>
    println(fields.join(","))
}



return


class ContextInfo {
    Integer cellSerial
    String cellLineName
    Integer cellsPerContainer
    Assay assay
    Long experimentId

    @Override
    String toString() {
        return "$cellSerial $cellLineName $cellsPerContainer ${assay.id} $experimentId"
    }
}

List<ContextInfo> readContextInfoFromFile(File inputFile) {
    List<ContextInfo> result = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(inputFile))
    reader.readLine() //skip header

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split(",")

        ContextInfo ci = new ContextInfo()
        ci.cellSerial = Integer.valueOf(split[0])
        ci.cellLineName = split[1]

        if (split.length > 2 && split[2]) {
            ci.cellsPerContainer = Integer.valueOf(split[2])
        }

        result.add(ci)
    }

    reader.close()

    return result
}


void investigateExistingCellLineNamesAndParents(List<ContextInfo> ciList) {
    for (ContextInfo ci : ciList) {
        Element cell = Element.findByLabelIlike(ci.cellLineName)
        if (cell) {
            StringBuilder builder = new StringBuilder()
            builder.append(ci.cellLineName)

            if (cell.childHierarchies.size() > 1) {
                System.err.println("warning: cell line name has multiple parents:")
                builder.append(" multiple parents ")
            }

            for (ElementHierarchy eh : cell.childHierarchies) {
                builder.append(" ${eh.parentElement.id}:${eh.parentElement.label}")
            }
            builder.append(" ${cell.id} ${cell.label}")
            println(builder.toString())
        }
    }
}

/**
 * searches for an assayContext within the provided assay that matches the provided searchAc, based on name,
 * attributeType, attributeElement, and valueElement
 * @param a
 * @param searchAc
 * @return
 */
AssayContext findMatchingAssayContext(Assay a, AssayContext searchAc, boolean verbose) {
    for (AssayContext ac : a.assayContexts) {

        if (verbose) {
            println(ac.contextName)
        }

        if (ac.contextName.equalsIgnoreCase(searchAc.contextName)) {

            if (verbose) {
                println("${ac.assayContextItems.size()} ${searchAc.assayContextItems.size()}")
            }

            if (ac.assayContextItems.size() == searchAc.assayContextItems.size()) {
                boolean contextItemsMatch = true
                for (int i = 0; i < ac.assayContextItems.size(); i++) {
                    AssayContextItem aci = ac.assayContextItems.get(i)
                    AssayContextItem searchAci = searchAc.assayContextItems.get(i)

                    if (verbose) {
                        println("${aci.attributeElementId}:${aci.valueElementId} ${searchAci.attributeElementId}:${searchAci.valueElementId}")
                    }

                    contextItemsMatch = contextItemsMatch && (aci.attributeType.equals(searchAci.attributeType))
                    contextItemsMatch = contextItemsMatch && (aci.attributeElementId == searchAci.attributeElementId)
                    contextItemsMatch = contextItemsMatch && (aci.valueElementId == searchAci.valueElementId)
                }

                if (contextItemsMatch) {
                    return ac
                }
            }
        }
    }

    return null
}


Element findOrCreateCellLineName(String cellLineName, Element otherCellName, boolean flush) {
    Element cellName = Element.findByLabelIlike(cellLineName)
    if (! cellName) {
        print("$cellLineName not found, creating ")
        cellName = new Element()
        cellName.label = cellLineName
        cellName.dateCreated = new Date()
        cellName.lastUpdated = new Date()

        trySave(cellName, flush, "element for cell line name ${cellName.label}")

        ElementHierarchy eh = new ElementHierarchy()
        eh.childElement = cellName
        eh.parentElement = otherCellName
        eh.relationshipType = "subClassOf"
        eh.dateCreated = new Date()
        eh.lastUpdated = new Date()

        trySave(eh, flush, "elementHierarchy for cell line name ${cellName.label}")

        println(" elementId:${cellName.id} elementHierarchyId:${eh.id}")
    } else {
        println("${cellLineName} found elementId:${cellName.id}")
    }

    return cellName
}


void finishAssayContextItem(AssayContextItem aci) {
    aci.valueDisplay = aci.deriveDisplayValue()
    aci.lastUpdated = aci.dateCreated
}


void addContextItems(AssayContext ac, Element otherCellName, Element cellName,
                     Element cellsPerWell, Integer cellsPerContainer, boolean flush) {

    AssayContextItem aci = new AssayContextItem()
    aci.attributeElement = otherCellName
    aci.valueElement = cellName
    ac.addToAssayContextItems(aci)
    aci.valueType = ValueType.ELEMENT
    finishAssayContextItem(aci)

    trySave(aci, flush, "assayContextItem specifying cell line name")

    if (cellsPerWell && cellsPerContainer) {
        aci = new AssayContextItem()
        aci.attributeElement = cellsPerWell
        aci.valueNum = cellsPerContainer
        aci.qualifier = "= "
        ac.addToAssayContextItems(aci)
        aci.valueType = ValueType.NUMERIC
        finishAssayContextItem(aci)

        trySave(aci, flush, "assayContextItem specifying cell line name")
    }
}

void trySave(def obj, boolean flush, String msg) {
    if (! obj.save(flush: flush)) {
        println("failed in attempt to save $msg")
        println(obj.errors)
        throw new Exception(obj.errors.toString())
    }
}