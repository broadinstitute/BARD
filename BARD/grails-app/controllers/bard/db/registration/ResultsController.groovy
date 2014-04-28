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

package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import bard.db.experiment.results.ImportSummary
import grails.plugins.springsecurity.Secured
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.web.multipart.MultipartFile
import au.com.bytecode.opencsv.CSVWriter

class FieldListCommand {
    def itemService

    List contextItemIds

    List queryList(def clazz, List ids) {
        def items = []
        ids.each { if(it != null) {
            items.add(clazz.get(it))
        } }

        return items
    }

    List<AssayContextItem> getExperimentContextItems() {
        return queryList(itemService, contextItemIds)
    }
}

@Secured(['isFullyAuthenticated()'])
class ResultsController {

    def resultsService;
    def itemService;

    def configureTemplate(String experimentId) {
        Experiment experiment = Experiment.get(experimentId)

        def (experimentItems, measures, measureItems) = resultsService.generateMaxSchemaComponents(experiment)

        [experiment: experiment, experimentItems: experimentItems, items: measureItems]
    }

    def generatePreview (String experimentId, FieldListCommand fieldList) {
        Experiment experiment = Experiment.get(experimentId)

        def (experimentContextItems, measures, measureItems) = resultsService.generateMaxSchemaComponents(experiment)

        Set experimentContextItemsSet = new HashSet(experimentContextItems)
        Set measureItemsSet = new HashSet(measureItems)

        def userSelectedItems = fieldList.experimentContextItems
        experimentContextItemsSet.addAll(userSelectedItems)
        measureItemsSet.removeAll(userSelectedItems)

        println("userSelectedItems=${userSelectedItems}")
        println("experimentContextItemsSet=${experimentContextItemsSet}")

        def schema = resultsService.generateSchema(experiment, experimentContextItemsSet as List, measures, measureItemsSet as List)

        StringWriter stringWriter = new StringWriter()
        CSVWriter csvWriter = new CSVWriter(stringWriter)
        for (List row in schema.asTable()) {
            String[] values = row.toArray()
            csvWriter.writeNext(values)
        }
        stringWriter.close()

        response.setContentType(GrailsWebUtil.getContentType("text/csv","UTF-8"));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"exp-${experimentId}.csv\"")
        response.getOutputStream().write(stringWriter.toString().getBytes())
        response.getOutputStream().flush()

        return null
    }

    def uploadResults() {
        Experiment experiment = Experiment.get(params.experimentId)
        MultipartFile f = request.getFile('resultsFile')

        ImportSummary summary = resultsService.importResults(experiment.id, f.inputStream)

        [summary: summary, experiment: experiment]
    }
}
