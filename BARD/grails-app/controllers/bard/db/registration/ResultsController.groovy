package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import grails.plugins.springsecurity.Secured
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.web.multipart.MultipartFile

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

        ResultsService.ImportSummary summary = resultsService.importResults(experiment.id, f.inputStream)

        [summary: summary, experiment: experiment]
    }
}
