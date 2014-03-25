import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentMeasure
import bard.db.model.AbstractContextItem
import org.apache.commons.lang3.StringUtils

/**
 * Created by ddurkin on 3/17/14.
 */



Experiment experiment = Experiment.findById(745L)
println(experiment.getDisplayName())
Set<ExperimentMeasure> experimentMeasures= experiment.experimentMeasures

Collection<ExperimentMeasure> rootMeasures = experiment.getRootMeasures()

for(ExperimentMeasure em in rootMeasures){
    printExperimentMeasure(em)
    printChildrenMeasuresSorted(em)
}

experiment.assay.assayContexts.each{ println("AssayContext contextName : ${it.contextName}")}
experiment.experimentContexts.each{    ExperimentContext ec ->
    println(ec.contextName)
    ec.contextItems.each { AbstractContextItem ci ->
        println("attribute: ${ci.attributeElement.label} -> value: ${ci.valueDisplay}")
    }
}

private List<ExperimentMeasure> printChildrenMeasuresSorted(ExperimentMeasure em, String offset = "") {
    em.getChildrenMeasuresSorted().each { ExperimentMeasure child ->
        printExperimentMeasure(child, "    " + offset)
        printChildrenMeasuresSorted(child, "    ")
    }
}


void printExperimentMeasure(ExperimentMeasure em, String offset= ""){
    final String relationShip = StringUtils.trimToEmpty(em.parentChildRelationship?.id).padRight(20)
    println("${offset}${em.displayLabel.padRight(20)} ${relationShip} priorityElement: ${em.priorityElement}")
}