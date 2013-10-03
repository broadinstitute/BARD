package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.LogicalKey
import bard.db.experiment.results.RowParser
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AttributeType
import bard.db.registration.ExternalReference
import bard.db.registration.ItemService
import bard.db.registration.Measure

import java.util.concurrent.atomic.AtomicInteger

class PubchemValidationService {
    PubchemReformatService pubchemReformatService
    ResultsService resultsService
    ResultsExportService resultsExportService

    AtomicInteger uniqueNumberGenerator = new AtomicInteger();

    def makeExperimentMeasure(PubchemReformatService.MappedStub stub, Assay assay) {
        Measure measure = new Measure(resultType: stub.resultType, statsModifier: stub.statsModifier)
        // need to populate ids because they're used in dup check
        measure.id = uniqueNumberGenerator.incrementAndGet()
        ExperimentMeasure experimentMeasure = new ExperimentMeasure(measure: measure)
        experimentMeasure.id = uniqueNumberGenerator.incrementAndGet()

//        println("stub.resultType=${stub.resultType} stub.contextItemColumns=${stub.contextItemColumns}")
        Set<Element> contextAttributes = new HashSet( stub.contextItemColumns + stub.contextItems.keySet())
        if(contextAttributes.size() > 0) {
            AssayContext context = new AssayContext()
            for(Element e in contextAttributes) {
                context.addToAssayContextItems(new AssayContextItem(attributeElement: e, attributeType: AttributeType.Free))
            }
            AssayContextMeasure link = new AssayContextMeasure()
            measure.addToAssayContextMeasures(link)
            context.addToAssayContextMeasures(link)

            assay.addToAssayContexts(context)
        }

//        println("children=${stub.children}")
        for(child in stub.children) {
            ExperimentMeasure childExpMeasure =  makeExperimentMeasure(child, assay)
            childExpMeasure.parentChildRelationship = child.parentChildRelationship
            experimentMeasure.addToChildMeasures(childExpMeasure)
        }

        assay.experiments.first().addToExperimentMeasures(experimentMeasure)

        experimentMeasure
    }

    def generateResultTree(Long aid, Closure tidMapper) {
        PubchemReformatService.ResultMap resultMap = pubchemReformatService.loadMap(aid)
        Collection<PubchemReformatService.MappedStub> newMeasures = pubchemReformatService.createMeasures(resultMap).collect {
            def mapped = pubchemReformatService.mapStub(it)
            return mapped
        }

        // make fake measures from newMeasure
        Experiment experiment = new Experiment()
        Assay assay = new Assay()
        assay.addToExperiments(experiment)

//        ExternalReference xref = ExternalReference.findByExtAssayRef("aid=${aid}")
//        xref.experiment.assay.contexts.each {context ->
//            context.clone(experiment.assay)
//        }

//        Map<PubchemReformatService.MappedStub, ExperimentMeasure> stubToMeasure = [:]

//        println("measures ${experiment.experimentMeasures}")
        Collection<ExperimentMeasure>measures = newMeasures.collect { makeExperimentMeasure(it, experiment.assay) }
        // link up parents
//        newMeasures.each {
//            ExperimentMeasure experimentMeasure = stubToMeasure[it]
//            ExperimentMeasure parentExperimentMeasure = stubToMeasure[it.parent]
//            println "${experimentMeasure} has parent ${parentExperimentMeasure}"
//            if(parentExperimentMeasure!=null)
//                parentExperimentMeasure.addToChildMeasures(experimentMeasure)
//        }

        // make a fake row
        Map<String,String> pubchemRow = [:]
        for(record in resultMap.allRecords) {
            if(record.qualifierTid)
                pubchemRow[record.qualifierTid] = ""
            pubchemRow[record.tid] = tidMapper( record.tid )
            for(contextItem in record.contextItemColumns) {
                pubchemRow[contextItem.tid] = tidMapper(contextItem.tid)
            }
        }
//        println "pubchemRow=${pubchemRow}"

        List dynamicColumns = pubchemReformatService.constructCapColumns(experiment)
        assert dynamicColumns.size() > 0
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        def osw = new OutputStreamWriter(baos)
        PubchemReformatService.CapCsvWriter writer = new PubchemReformatService.CapCsvWriter(100, [:], dynamicColumns, osw)

        pubchemReformatService.convertRow(measures, 1, pubchemRow, resultMap, writer, null, null)
        writer.close();

//        println("reformatted: \"${new String(baos.toByteArray())}\"")

        ResultsService.Template template = resultsService.generateMaxSchema(experiment)
        ImportSummary errors = new ImportSummary()
        RowParser parsed = resultsService.initialParse(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())), errors, template, true)
        assert !errors.hasErrors()

        Map<Measure, Collection<ItemService.Item>> itemsByMeasure = resultsService.constructItemsByMeasure(experiment)
        Collection<Result> results = resultsService.createResults(parsed.rows, measures, errors, itemsByMeasure)
        assert !errors.hasErrors()

        checkForDuplicates(errors, results)
        if(errors.hasErrors()) {
            errors.errors.each {
                println("error: ${it}")
            }
        }

        return resultsExportService.resultsToPrettyPrintString(100L, new ArrayList(results))
    }

    private void checkForDuplicates(ImportSummary errors, Collection<Result> results) {
        Map<LogicalKey,LogicalKey> seen = new HashMap()
        IdentityHashMap<LogicalKey,Result> mapToOriginal = new IdentityHashMap();

        for (result in results) {
            LogicalKey key = resultsService.constructKey(result)
            mapToOriginal[key] = result
            key.valueDisplay = "STUB"
            if (seen.containsKey(key)) {
                errors.addError(0, 0, "Found duplicate: ${mapToOriginal[seen[key]].valueDisplay} == ${mapToOriginal[key].valueDisplay}")
            } else {
                seen[key] = key
            }
        }
    }

    def generateResultTreeForUnique(Long aid) {
        return generateResultTree(aid, {tid -> "TID-${tid}"})
    }
}
