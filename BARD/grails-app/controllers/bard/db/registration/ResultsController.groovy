package bard.db.registration

class ResultsController {

    def resultsService;

    def configureTemplate() {
        Assay assay = Assay.get(params.assayId)

        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def measureItems = assayItems.findAll { it.assayContext.assayContextMeasures.size() > 0 }
        assayItems.removeAll(measureItems)

        [assay: assay, assayItems: assayItems, measureItems: measureItems]
    }

    def generatePreview () {
        // TODO: parse params properly
        Assay assay = Assay.get(4250)
        def assayItems = assay.assayContextItems.findAll { it.attributeType != AttributeType.Fixed }
        def measureItems = assayItems.findAll { it.assayContext.assayContextMeasures.size() > 0 }
        assayItems.removeAll(measureItems)
        def constantItems = assayItems
        def measures = assay.measures

        def schema = resultsService.generateSchema(assay, constantItems as List, measures as List, measureItems as List)
        [rows: schema.asTable()]
    }
}
