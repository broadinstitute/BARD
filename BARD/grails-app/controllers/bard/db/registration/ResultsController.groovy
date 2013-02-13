package bard.db.registration

import bard.db.experiment.Experiment
import grails.plugins.springsecurity.Secured
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.web.multipart.MultipartFile

class FieldListCommand {
    def itemService

    List contextItemIds
    List measureIds
    List measureItemIds

    List queryList(def clazz, List ids) {
        def items = []
        ids.each { if(it != null) {
            items.add(clazz.get(it))
        } }

        return items
    }

    List<AssayContextItem> getContextItems() {
        return queryList(itemService, contextItemIds)
    }

    List<Measure> getMeasures() {
        return queryList(Measure, measureIds)
    }

    List<AssayContextItem> getMeasureItems() {
        return queryList(itemService, measureItemIds)
    }
}

@Secured(['isFullyAuthenticated()'])
class ResultsController {

    def resultsService;
    def itemService;

    def configureTemplate(String experimentId) {
        Experiment experiment = Experiment.get(experimentId)

        def assay = experiment.assay
        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def items = itemService.getLogicalItems(assayItems)

        def measureItems = items.findAll { it.assayContext.assayContextMeasures.size() > 0 }
        items.removeAll(measureItems)

        [experiment: experiment, assayItems: items, measureItems: measureItems]
    }

    def generatePreview (String experimentId, FieldListCommand fieldList) {
        Experiment experiment = Experiment.get(experimentId)

        def schema = resultsService.generateSchema(experiment, fieldList.contextItems, fieldList.measures, fieldList.measureItems)

        StringBuilder csv = new StringBuilder()
        for (row in schema.asTable()) {
            csv.append(row.join(","))
            csv.append("\n")
        }

        response.setContentType(GrailsWebUtil.getContentType("text/csv","UTF-8"));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"exp-${experimentId}.csv\"")
        response.getOutputStream().write(csv.toString().getBytes())
        response.getOutputStream().flush()

        return null
    }

    def uploadResults() {
        Experiment experiment = Experiment.get(params.experimentId)
        MultipartFile f = request.getFile('resultsFile')

        ResultsService.ImportSummary summary = resultsService.importResults(experiment, f.inputStream)

        [summary: summary]
    }
}
