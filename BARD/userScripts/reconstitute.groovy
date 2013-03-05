import au.com.bytecode.opencsv.CSVReader
import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy
import bard.db.experiment.Substance

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/1/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */

def dir = "/Users/pmontgom/data/result-deposition-export"

class Result {
    Long id
    Experiment experiment
    Element resultType
    Substance substance
    Element statsModifier
    Integer replicateNumber

    String qualifier
    Float valueNum
    Float valueMin
    Float valueMax
    String valueDisplay

    Set<ResultContextItem> resultContextItems = [] as Set<ResultContextItem>
    Set<ResultHierarchy> resultHierarchiesForResult = [] as Set<ResultHierarchy>
    Set<ResultHierarchy> resultHierarchiesForParentResult = [] as Set<ResultHierarchy>
}

class X {
}

X x = new X();

def test = System.properties.hasProperty("testload")
if (test) {
    x.readResults("${dir}/results-export-head.csv")
    x.readHierarchy("${dir}/hierarchy-export-head.csv")
    x.readItems("${dir}/item-export-head.csv")
} else {
    x.readResults("${dir}/results-export.csv")
    x.readHierarchy("${dir}/hierarchy-export.csv")
    x.readItems("${dir}/item-export.csv")
}
println "Total ${x.results.size()} results"


