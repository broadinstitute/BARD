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

package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.LogicalKey
import bard.db.experiment.results.RowParser
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

import java.util.concurrent.atomic.AtomicInteger

class PubchemValidationService {
    PubchemReformatService pubchemReformatService
    ResultsService resultsService
    ResultsExportService resultsExportService

    AtomicInteger uniqueNumberGenerator = new AtomicInteger();

    def makeExperimentMeasure(PubchemReformatService.MappedStub stub, Assay assay) {
        ExperimentMeasure experimentMeasure = new ExperimentMeasure(resultType: stub.resultType, statsModifier: stub.statsModifier)
        // need to populate ids because they're used in dup check
        experimentMeasure.id = uniqueNumberGenerator.incrementAndGet()
        // ExperimentMeasure experimentMeasure = new ExperimentMeasure(measure: experimentMeasure)
        //   experimentMeasure.id = uniqueNumberGenerator.incrementAndGet()

//        println("stub.resultType=${stub.resultType} stub.contextItemColumns=${stub.contextItemColumns}")
        Set<Element> contextAttributes = new HashSet(stub.contextItemColumns + stub.contextItems.keySet())
        if (contextAttributes.size() > 0) {
            AssayContext context = new AssayContext()
            for (Element e in contextAttributes) {
                context.addToAssayContextItems(new AssayContextItem(attributeElement: e, attributeType: AttributeType.Free))
            }
            AssayContextExperimentMeasure link = new AssayContextExperimentMeasure()
            experimentMeasure.addToAssayContextExperimentMeasures(link)
            context.addToAssayContextExperimentMeasures(link)

            assay.addToAssayContexts(context)
        }

//        println("children=${stub.children}")
        for (child in stub.children) {
            ExperimentMeasure childExpMeasure = makeExperimentMeasure(child, assay)
            childExpMeasure.parentChildRelationship = child.parentChildRelationship
            experimentMeasure.addToChildMeasures(childExpMeasure)
        }

        assay.experiments.first().addToExperimentMeasures(experimentMeasure)

        experimentMeasure
    }

    def generateResultTree(Long aid, Closure tidMapper) {
        throw new RuntimeException(" verifyOrCreateMeasures Requires rework");

        /* PubchemReformatService.ResultMap resultMap = pubchemReformatService.loadMap(aid)
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

        ResultsService.Template template = resultsService.generateMaxSchema(experiment)
        ImportSummary errors = new ImportSummary()
        RowParser parsed = resultsService.initialParse(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())), errors, template, true)
        assert !errors.hasErrors()

        List rows = parsed.readNextSampleRows()

        Map<Measure, Collection<ItemService.Item>> itemsByMeasure = resultsService.constructItemsByMeasure(experiment)
        Collection<Result> results = resultsService.createResults(rows, measures, errors, itemsByMeasure)
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
         */
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
        return generateResultTree(aid, { tid -> "TID-${tid}" })
    }
}
