package bard.db.experiment

import bard.db.BardIntegrationSpec
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.enums.ReadyForExtraction
import spock.lang.IgnoreRest
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/6/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BulkResultServiceIntegrationSpec extends BardIntegrationSpec {
    BulkResultService bulkResultService

    Result parent;
    Result child;
    Experiment experiment

    def setup() {
    }

    void buildForTests() {
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
        parent.substanceId = substance.id
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
        child.substanceId = substance.id
        child.valueDisplay = HierarchyType.SUPPORTED_BY.getId()

        ResultHierarchy hierarchy = new ResultHierarchy()
        hierarchy.parentResult = parent
        parent.resultHierarchiesForParentResult.add(hierarchy)
        hierarchy.result = child
        child.resultHierarchiesForResult.add(hierarchy)
        hierarchy.hierarchyType = HierarchyType.SUPPORTED_BY

    }

    boolean compareMaps(Map a, Map b) {
        assert a.keySet() == b.keySet()

        Set nestedProps = ["resultContextItems", "resultHierarchiesForParentResult", "resultHierarchiesForResult"] as Set

        for (k in a.keySet()) {
            if (nestedProps.contains(k))
                continue
            assert a[k] == b[k]
        }
        return true
    }

    @IgnoreRest
    void 'test load, query, delete for results with external urls'() {
        given:
        experiment = Experiment.build()
        Element resultType = Element.build()
        Element statsModifier = Element.build()
        Substance substance = Substance.build()


        // construct a parent and a child with one context item
        final Result parentResult = new Result()
        parentResult.experiment = experiment
        parentResult.qualifier = "< "
        parentResult.readyForExtraction = ReadyForExtraction.NOT_READY
        parentResult.replicateNumber = 1
        parentResult.resultStatus = "Pending"
        parentResult.resultType = resultType
        parentResult.statsModifier = statsModifier
        parentResult.substanceId = substance.id
        parentResult.valueDisplay = "parentResult"
        parentResult.valueMin = 1
        parentResult.valueMax = 100
        parentResult.valueNum = 50

        ResultContextItem item = new ResultContextItem()
        item.result = parentResult
        item.extValueId = "222"
        item.valueDisplay = "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=222"
        item.attributeElement = Element.build(externalURL: "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=",label:"someLabel")

        parentResult.resultContextItems.add(item)
        when: "Results are inserted"
        //Experiment experiment = Experiment.build()

        Element.withSession { s -> s.flush() }
        bulkResultService.insertResults("test", experiment, [parentResult])


        then:
        final List<Result> results = bulkResultService.findResults(experiment)
        assert results.size() == 1
        Result result = results.get(0)
        assert result
        final List<ResultContextItem> items = new ArrayList<ResultContextItem>(result.resultContextItems)
        assert 1 == items.size()
        ResultContextItem foundItem = items.get(0)
        assert foundItem
        assert item.extValueId == foundItem.extValueId
        assert item.valueDisplay == foundItem.valueDisplay
        assert foundItem.qualifier == ""

        //Result dbParent = results.find { it.valueDisplay == "parent" }
        //assert dbParent


    }

    void 'test load, query, delete'() {
        given:
        buildForTests()
        when:
        Element.withSession { s -> s.flush() }
        bulkResultService.insertResults("test", experiment, [parent, child])

        List<Result> fromDb = bulkResultService.findResults(experiment)

        Result dbParent = fromDb.find { it.valueDisplay == "parent" }
        Result dbChild = fromDb.find { it.valueDisplay == HierarchyType.SUPPORTED_BY.getId() }

        then:
        System.identityHashCode(parent) != System.identityHashCode(dbParent)
        System.identityHashCode(child) != System.identityHashCode(dbChild)

        dbParent != null
        dbChild != null

        compareMaps(parent.properties, dbParent.properties)
        dbParent.resultContextItems.size() == 1
        compareMaps(dbParent.resultContextItems.first().properties, dbParent.resultContextItems.first().properties)
        dbParent.resultHierarchiesForParentResult.size() == 1

        compareMaps(child.properties, dbChild.properties)
        dbChild.resultHierarchiesForResult.size() == 1

        when:
        bulkResultService.deleteResults(experiment)

        then:
        bulkResultService.findResults(experiment).size() == 0
    }

    void 'test delete'() {
        given:
        buildForTests()
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
