package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import grails.plugin.spock.IntegrationSpec
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/6/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
class BulkResultServiceIntegrationSpec extends IntegrationSpec {
    BulkResultService bulkResultService

    Result parent;
    Result child;
    Experiment experiment

    def setup() {
        experiment = Experiment.build()
        Element resultType = Element.build()
        Element statsModifier = Element.build()
        Substance substance = Substance.build()

        Element childResultType = Element.build()

        // construct a parent and a child with one context item
        parent = new Result()
        parent.experiment = experiment
        parent.qualifier = "< "
        parent.readyForExtraction = ReadyForExtraction.NOT_READY
        parent.replicateNumber = 1
        parent.resultStatus = "Pending"
        parent.resultType = resultType
        parent.statsModifier = statsModifier
        parent.substance = substance
        parent.valueDisplay = "parent"
        parent.valueMin = 1
        parent.valueMax = 100
        parent.valueNum = 50

        ResultContextItem item = new ResultContextItem()
        item.result = parent
        parent.resultContextItems.add(item)
        item.valueDisplay = "item"
        item.valueNum = 1000
        item.valueMin = 1001
        item.valueMax = 2001
        item.attributeElement = Element.build()
        item.qualifier = ">"
        item.valueElement = Element.build()

        child = new Result()
        child.experiment = experiment
        child.qualifier = "< "
        child.readyForExtraction = ReadyForExtraction.NOT_READY
        child.replicateNumber = 1
        child.resultStatus = "Pending"
        child.resultType = resultType
        child.statsModifier = statsModifier
        child.substance = substance
        child.valueDisplay = "child"

        ResultHierarchy hierarchy = new ResultHierarchy()
        hierarchy.parentResult = parent
        parent.resultHierarchiesForParentResult.add(hierarchy)
        hierarchy.result = child
        child.resultHierarchiesForResult.add(hierarchy)
        hierarchy.hierarchyType = HierarchyType.Child
    }

    boolean compareMaps(Map a, Map b) {
        assert a.keySet() == b.keySet()

        Set nestedProps = ["resultContextItems", "resultHierarchiesForParentResult", "resultHierarchiesForResult"] as Set

        for(k in a.keySet()) {
            if (nestedProps.contains(k))
                continue
            assert a[k] == b[k]
        }
        return true
    }

    void 'test load, query, delete' () {
        when:
        Element.withSession { s -> s.flush() }
        bulkResultService.insertResults("test", experiment, [parent, child])

        List<Result> fromDb = bulkResultService.findResults(experiment)

        Result dbParent = fromDb.find { it.valueDisplay == "parent" }
        Result dbChild = fromDb.find { it.valueDisplay == "child" }

        then:
        System.identityHashCode(parent) != System.identityHashCode(dbParent)
        System.identityHashCode(child) != System.identityHashCode(dbChild)

        dbParent != null
        dbChild != null

        compareMaps(parent.properties,dbParent.properties)
        dbParent.resultContextItems.size() == 1
        compareMaps(dbParent.resultContextItems.first().properties,dbParent.resultContextItems.first().properties)
        dbParent.resultHierarchiesForParentResult.size() == 1

        compareMaps(child.properties,dbChild.properties)
        dbChild.resultHierarchiesForResult.size() == 1

        when:
        bulkResultService.deleteResults(experiment)

        then:
        bulkResultService.findResults(experiment).size() == 0
    }

    void 'test delete'() {
        when: "Results are inserted under two different experiments, delete only touches one of them"
        Experiment experiment1 = Experiment.build()
        Experiment experiment2 = Experiment.build()

        Element.withSession { s -> s.flush() }
        bulkResultService.insertResults("test", experiment1, [parent, child])
        bulkResultService.insertResults("test", experiment2, [parent, child])

        then:
        bulkResultService.findResults(experiment1).size() == 2
        bulkResultService.findResults(experiment2).size() == 2

        when:
        bulkResultService.deleteResults(experiment1)

        then:
        bulkResultService.findResults(experiment1).size() == 0

        when:
        List<Result> remaining = bulkResultService.findResults(experiment2)
        Result dbParent = remaining.find { it.valueDisplay == "parent" }

        then:
        remaining.size() == 2
        dbParent.resultHierarchiesForParentResult.size() == 1
        dbParent.resultContextItems.size() == 1

        then:
        bulkResultService.deleteResults(experiment2)
        bulkResultService.findResults(experiment1).size() == 0
        bulkResultService.findResults(experiment2).size() == 0
    }

}
