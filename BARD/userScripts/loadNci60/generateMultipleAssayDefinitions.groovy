import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.enums.ValueType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.ExperimentService
import bard.db.experiment.PanelExperiment
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.Panel
import bard.db.registration.PanelService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import registration.AssayService

/**
 * Created by dlahr on 3/14/14.
 */


final Integer assayId = 8265
final Integer assayContextId = 576582
final Integer screeningConcentrationContextId = 614759
final Integer otherCellNameElementId = 377
final Integer cellsPerWellElementId = 2302
final Integer percentViabilityElementId = 992
final File contextInfoFile = new File("C:/Local/i_drive/projects/bard/dataMigration/NCI60/NCI_CellLineName_Density.csv")
final Role newOwnerRole = Role.findById(16) //Broad owner role
final boolean flush = true
final Panel panel = Panel.findById(185) //panel to put new assay definitions in

SpringSecurityUtils.reauthenticate("dlahr", null)

SessionFactory sf = ctx.sessionFactory
BardContextUtils.setBardContextUsername(sf.currentSession, "dlahr")
println(BardContextUtils.getCurrentUsername(sf.currentSession))


println("read context info from file ${contextInfoFile.getAbsolutePath()}")
List<ContextInfo> ciList = readContextInfoFromFile(contextInfoFile)
//ciList = ciList.subList(0,2)

//investigateExistingCellLineNamesAndParents(ciList)
//result - some were found, all had parent that was "other cell name"

final Element otherCellName = Element.findById(otherCellNameElementId)

AssayCreator assayCreator = new AssayCreator()
assayCreator.assayService = ctx.assayService
assayCreator.templateAssay = Assay.findById(assayId)
assayCreator.newOwnerRole = newOwnerRole
assayCreator.variableContext = AssayContext.findById(assayContextId)
assayCreator.otherCellName = otherCellName
assayCreator.cellsPerWell = Element.findById(cellsPerWellElementId)
assayCreator.flush = flush

CellLineNameFinderOrCreator cellLineNameFinderOrCreator = new CellLineNameFinderOrCreator(otherCellName, flush)

PanelExperiment panelExperiment = null
Assay.withTransaction {status ->
    println("create panelExperiment")
    panelExperiment = new PanelExperiment()
    panelExperiment.panel = panel
    panelExperiment.modifiedBy = "dlahr"

    Utilities.trySave(panelExperiment, flush, "panelExperiment")

    final Element percentViability = Element.findById(percentViabilityElementId)
    final AssayContext screenConcContext = AssayContext.findById(screeningConcentrationContextId)
    ExperimentCreator experimentCreator = new ExperimentCreator(newOwnerRole, panelExperiment, flush,
            (ExperimentService)(ctx.experimentService), percentViability, screenConcContext)

    for (ContextInfo ci : ciList) {
        Element cellName = cellLineNameFinderOrCreator.findOrCreate(ci.cellLineName)
        assert cellName

        ci.assay = assayCreator.createCopyOfAssay(cellName, ci.cellsPerContainer)

        ci.experiment = experimentCreator.createExperiment(ci.assay)
    }

    println("add assays to panel")
    List<Assay> assayList = ciList.collect({ContextInfo ci -> return ci.assay})
    PanelService panelService = ctx.panelService
    panelService.associateAssays(panel.id, assayList)

    Utilities.trySave(panel, flush, "panel")

//    status.setRollbackOnly()
}



println()
println("results:  ")
println("panelExperiment.id:  ${panelExperiment.id}")
for (ContextInfo ci : ciList) {
    List<String> fields = [ci.cellSerial, ci.cellLineName, ci.cellsPerContainer, ci.assay.id, ci.experiment.id] as List<String>
    println(fields.join(","))
}



return


class ContextInfo {
    Integer cellSerial
    String cellLineName
    Integer cellsPerContainer
    Assay assay
    Experiment experiment

    @Override
    String toString() {
        return "$cellSerial $cellLineName $cellsPerContainer ${assay.id} ${experiment.id}"
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

class CellLineNameFinderOrCreator {
    Element otherCellName
    boolean flush

    CellLineNameFinderOrCreator(Element otherCellName, boolean flush) {
        this.otherCellName = otherCellName
        this.flush = flush
    }

    Element findOrCreate(String cellLineName) {
        Element cellName = Element.findByLabelIlike(cellLineName)
        if (! cellName) {
            print("$cellLineName not found, creating ")
            cellName = new Element()
            cellName.label = cellLineName
            cellName.dateCreated = new Date()
            cellName.lastUpdated = new Date()

            Utilities.trySave(cellName, flush, "element for cell line name ${cellName.label}")

            ElementHierarchy eh = new ElementHierarchy()
            eh.childElement = cellName
            eh.parentElement = otherCellName
            eh.relationshipType = "subClassOf"
            eh.dateCreated = new Date()
            eh.lastUpdated = new Date()

            Utilities.trySave(eh, flush, "elementHierarchy for cell line name ${cellName.label}")

            println(" elementId:${cellName.id} elementHierarchyId:${eh.id}")
        } else {
            println("${cellLineName} found elementId:${cellName.id}")
        }

        return cellName
    }
}


class Utilities {
    static void trySave(def obj, boolean flush, String msg) {
        if (! obj.save(flush: flush)) {
            println("failed in attempt to save $msg")
            println(obj.errors)
            throw new Exception(obj.errors.toString())
        }
    }

    /**
     * searches for an assayContext within the provided assay that matches the provided searchAc, based on name,
     * attributeType, attributeElement, and valueElement
     * @param a
     * @param searchAc
     * @return
     */
    static AssayContext findMatchingAssayContext(Assay a, AssayContext searchAc, boolean verbose) {
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
}


class AssayCreator {
    AssayService assayService
    Assay templateAssay
    Role newOwnerRole
    AssayContext variableContext
    Element otherCellName
    Element cellsPerWell
    boolean flush

    Assay createCopyOfAssay(Element cellName, Integer cellsPerContainer) {
        Assay newAssay = assayService.cloneAssayForEditing(templateAssay, "NCI60")
        newAssay.assayName = "${templateAssay.assayName} for cell line ${cellName.label}"
        newAssay.ownerRole = newOwnerRole

        AssayContext ac = Utilities.findMatchingAssayContext(newAssay, variableContext, false)
        assert ac

        addContextItems(ac, cellName, cellsPerContainer)

        Utilities.trySave(newAssay, flush, "assay for cell line ${cellName.label}")

        return newAssay
    }


    void addContextItems(AssayContext ac, Element cellName, Integer cellsPerContainer) {

        AssayContextItem aci = new AssayContextItem()
        aci.attributeElement = otherCellName
        aci.valueElement = cellName
        ac.addToAssayContextItems(aci)
        aci.valueType = ValueType.ELEMENT
        finishAssayContextItem(aci)

        Utilities.trySave(aci, flush, "assayContextItem specifying cell line name")

        if (cellsPerWell && cellsPerContainer) {
            aci = new AssayContextItem()
            aci.attributeElement = cellsPerWell
            aci.valueNum = cellsPerContainer
            aci.qualifier = "= "
            ac.addToAssayContextItems(aci)
            aci.valueType = ValueType.NUMERIC
            finishAssayContextItem(aci)

            Utilities.trySave(aci, flush, "assayContextItem specifying cell line name")
        }
    }

    static void finishAssayContextItem(AssayContextItem aci) {
        aci.valueDisplay = aci.deriveDisplayValue()
        aci.lastUpdated = aci.dateCreated
    }
}


class ExperimentCreator {

    Role newOwnerRole
    PanelExperiment panelExperiment
    boolean flush
    ExperimentService experimentService
    Element percentViability
    AssayContext screeningConcContext

    ExperimentCreator(Role newOwnerRole, PanelExperiment panelExperiment, boolean flush,
                      ExperimentService experimentService, Element percentViability,
                      AssayContext screeningConcContext) {
        this.newOwnerRole = newOwnerRole
        this.panelExperiment = panelExperiment
        this.flush = flush
        this.experimentService = experimentService
        this.percentViability = percentViability
        this.screeningConcContext = screeningConcContext
    }

    Experiment createExperiment(Assay assay) {
        Experiment e = new Experiment()
        e.experimentName = "NCI60 experiment from ${assay.assayName}".toString()
        e.ownerRole = newOwnerRole
        e.assay = assay
        panelExperiment.addToExperiments(e)

        Utilities.trySave(e, flush, "${e.experimentName}")

        AssayContext newScreenConcContext = Utilities.findMatchingAssayContext(assay, screeningConcContext, false)
        List<Integer> newScreenConcContextIds = [newScreenConcContext.id] as List<Integer>
        ExperimentMeasure em = experimentService.createNewExperimentMeasure(e.id, null, percentViability, null,
                newScreenConcContextIds, null, true)

        e.addToExperimentMeasures(em)

        return e
    }
}


